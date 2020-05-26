package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import org.identityconnectors.framework.common.objects.*;

import java.util.List;
import java.util.Set;

/**
 * Base users adapter that needs to be subclassed in order to map a specific user model
 * data type to ConnId attributes and vice versa.  This extends BaseAdapter
 * so that the Adapter has composition and can reference the driver.
 */
public abstract class BaseUsersAdapter<U extends UserIdentityModel, G extends GroupIdentityModel>
        extends BaseAdapter<U,G> {


    @Override
    protected ObjectClass getType() {
        return ObjectClass.ACCOUNT;
    }

    protected abstract U constructUser(Set<Attribute> attributes, boolean creation);

    protected abstract ConnectorObject constructConnectorObject(U modelType);

    @Override
    public Uid create(Set<Attribute> attributes) {
        U user = constructUser(attributes, true);
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
    public Uid update(Uid uid, Set<Attribute> attributes) {
        U user = constructUser(attributes, false);
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
    public void delete(Uid uid) {
        getDriver().deleteUser(uid.getUidValue());
    }

    @Override
    public void get(String query, ResultsHandler resultsHandler) {
        if (queryAllRecords(query)) {
            // query for all users
            List<U> allUsers = getDriver().getUsers();

            for (UserIdentityModel currentUser : allUsers) {
                resultsHandler.handle(
                        new ConnectorObjectBuilder()
                                .setUid(currentUser.getIdentityIdValue())
                                .setName(currentUser.getIdentityNameValue())
                                .setObjectClass(getType())
                                .build());
            }
        } else {
            // Query for single user
            U singleUser = getDriver().getUser(query);
            if (singleUser != null) {
                resultsHandler.handle(constructConnectorObject(singleUser));
            }
        }
    }

    protected boolean groupAdditionControlledByUpdate() {
        return false;
    }

    protected boolean groupRemovalControlledByUpdate() {
        return false;
    }
}
