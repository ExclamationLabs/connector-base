package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.model.AccessManagementModel;
import org.identityconnectors.framework.common.objects.*;

import java.util.List;
import java.util.Set;

public interface UsersAdapter<T extends U,U extends AccessManagementModel,G extends AccessManagementModel> extends Adapter<T,U,G> {

    @Override
    default ObjectClass getType() {
        return ObjectClass.ACCOUNT;
    }

    @Override
    default Uid create(Set<Attribute> attributes) {
        T user = constructModel(attributes, true);
        String newUserId = getDriver().createUser(user);

        // TODO: group update?
        /*
        if (! groupAdditionControlledByUpdate()) {
            getDriver().addGroupToUser(carp, newUserId);
        }
        */

        return new Uid(newUserId);
    }

    @Override
    default Uid update(Uid uid, Set<Attribute> attributes) {
        T user = constructModel(attributes, false);
        getDriver().updateUser(uid.getUidValue(), user);

        // TODO: group update?
        /*
        if (! groupAdditionControlledByUpdate()) {
            getDriver().addGroupToUser(carp, newUserId);
        }
        */

        return uid;
    }

    @Override
    default void delete(Uid uid) {
        getDriver().deleteUser(uid.getUidValue());
    }

    @Override
    default void get(String query, ResultsHandler resultsHandler) {
        if (queryAllRecords(query)) {
            // query for all users
            List<U> allUsers = getDriver().getUsers();

            for (U currentUser : allUsers) {
                resultsHandler.handle(
                        new ConnectorObjectBuilder()
                                .setUid(currentUser.getAccessManagementIdValue())
                                .setName(currentUser.getAccessManagementNameValue())
                                .setObjectClass(getType())
                                .build());
            }
        } else {
            // Query for single user
            U singleUser = getDriver().getUser(query);
            if (singleUser != null) {
                resultsHandler.handle(constructConnectorObject((T) singleUser));
            }
        }
    }

}
