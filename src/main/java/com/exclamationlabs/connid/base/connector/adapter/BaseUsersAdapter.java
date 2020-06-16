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
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base users adapter that needs to be subclassed in order to map a specific user model
 * data type to ConnId attributes and vice versa.  This extends BaseAdapter
 * so that the Adapter has composition and can reference the driver.
 */
public abstract class BaseUsersAdapter<U extends UserIdentityModel, G extends GroupIdentityModel>
        extends BaseAdapter<U,G> {

    private static final Log LOG = Log.getLog(BaseUsersAdapter.class);

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

        if ((! groupAdditionControlledByUpdate()) &&
                groupMembershipAttributePresent(attributes, user.getAssignedGroupsAttributeName())) {
            Optional<Attribute> groupIds = attributes.stream().filter(current ->
                    current.getName().equals(user.getAssignedGroupsAttributeName())).findFirst();

            if (groupIds.isPresent() && groupIds.get().getValue() != null) {
                for (Object currentGroupId : groupIds.get().getValue()) {
                    getDriver().addGroupToUser(currentGroupId.toString(), newUserId);
                }
            }
        }

        return new Uid(newUserId);
    }

    @Override
    public Uid update(Uid uid, Set<Attribute> attributes) {
        U user = constructUser(attributes, false);
        getDriver().updateUser(uid.getUidValue(), user);

        if ((! groupAdditionControlledByUpdate()) &&
                groupMembershipAttributePresent(attributes, user.getAssignedGroupsAttributeName())) {
            // get current set of assigned groups
            U checkUser = getDriver().getUser(uid.getUidValue());

            Optional<Attribute> gatherUpdatedGroupIds = attributes.stream().filter(current ->
                    current.getName().equals(user.getAssignedGroupsAttributeName())).findFirst();

            List<String> updatedGroupIds = (gatherUpdatedGroupIds.isPresent() &&
                    gatherUpdatedGroupIds.get().getValue() != null)
                    ? gatherUpdatedGroupIds.get().getValue().stream().map(
                    Object::toString).collect(Collectors.toList())
                    : new ArrayList<>();

            updateGroupsForUser(uid.getUidValue(), checkUser.getAssignedGroupIds(), updatedGroupIds);

        }

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

    private static boolean groupMembershipAttributePresent(Set<Attribute> attributes, String groupName) {
        Optional<Attribute> hasUpdatedGroupIds = attributes.stream().filter(current ->
                current.getName().equals(groupName)).findFirst();
        return hasUpdatedGroupIds.isPresent();
    }

    private void updateGroupsForUser(String userId, List<String> currentGroupIds,
                                     List<String> updatedGroupIds) {

        for (String groupId : currentGroupIds) {
            if (! updatedGroupIds.contains(groupId)) {
                // group was removed
                getDriver().removeGroupFromUser(groupId, userId);
                LOG.info("Successfully removed group id {0} from user id {1}", groupId, userId);
            }
        }

        for (String groupId : updatedGroupIds) {
            if (! currentGroupIds.contains(groupId)) {
                getDriver().addGroupToUser(groupId, userId);
                LOG.info("Successfully added group id {0} to user id {1}", groupId, userId);
            }
        }

    }

    protected boolean groupAdditionControlledByUpdate() {
        return false;
    }
}
