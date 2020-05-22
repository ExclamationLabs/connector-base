/*
    Copyright 2020 Exclamation Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import org.identityconnectors.framework.common.objects.*;

import java.util.List;
import java.util.Set;

/**
 * This interface orchestrates the creating, reading, updating and deleting
 * of users that needs to be integrated between ConnId/Midpoint
 * and a data system holding IAM data (accessed via a Driver)
 */
public interface UsersAdapter<U extends UserIdentityModel, G extends GroupIdentityModel>
        extends Adapter<U, G> {

    @Override
    default ObjectClass getType() {
        return ObjectClass.ACCOUNT;
    }

    boolean groupAdditionControlledByUpdate();

    boolean groupRemovalControlledByUpdate();

    U constructModel(Set<Attribute> attributes, boolean creation);

    ConnectorObject constructConnectorObject(U modelType);

    @Override
    default Uid create(Set<Attribute> attributes) {
        U user = constructModel(attributes, true);
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
        U user = constructModel(attributes, false);
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

}
