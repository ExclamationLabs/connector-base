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

import com.exclamationlabs.connid.base.connector.adapter.result.IdentityResult;
import com.exclamationlabs.connid.base.connector.adapter.result.IdentityResultRecord;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.SearchResultsHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Base users adapter that needs to be subclassed in order to map a specific user model
 * data type to ConnId attributes and vice versa.  This extends BaseAdapter
 * so that the Adapter has composition and can reference the driver.
 */
public abstract class BaseUsersAdapter<U extends UserIdentityModel, G extends GroupIdentityModel>
        extends BaseAdapter<U,G> {

    private static final Log LOG = Log.getLog(BaseUsersAdapter.class);

    private final Map<String, IdentityResult> identityResultMap = new HashMap<>();

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
                    ? gatherUpdatedGroupIds.get().getValue().stream().filter(Objects::nonNull).map(
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
    public void get(String query, ResultsHandler resultsHandler, OperationOptions options) {
        if (queryAllRecords(query)) {
            LOG.info("Operation options for query: {0}", options);
            List<IdentityResultRecord> recordList;
            String currentSearchCookie;
            if (options.getPagedResultsCookie() != null &&
                    identityResultMap.containsKey(options.getPagedResultsCookie())) {
                LOG.info("Cookie found for search: {0}", options.getPagedResultsCookie());
                recordList = identityResultMap.get(
                        options.getPagedResultsCookie()).getCachedResults();
                currentSearchCookie = options.getPagedResultsCookie();
            } else {
                IdentityResult newResult = new IdentityResult();
                // query for all users
                List<U> allUsers = getDriver().getUsers();
                for (UserIdentityModel currentUser : allUsers) {
                    newResult.addResult(currentUser.getIdentityIdValue(),
                            currentUser.getIdentityNameValue());
                }
                recordList = newResult.getCachedResults();
                currentSearchCookie = newResult.getUuid();
                identityResultMap.put(currentSearchCookie, newResult);
            }
            LOG.info("Total result records: {0}", recordList.size());

            int currentPageSize = options.getPageSize() != null && options.getPageSize() > 1
                    ? options.getPageSize() : DEFAULT_PAGE_SIZE;
            int currentOffset = options.getPagedResultsOffset() != null
                    ? options.getPagedResultsOffset() - 1 : 0;
            LOG.info("In use page size {0}, offset {1}",
                    currentPageSize, currentOffset);
            int stopOffset = Math.min(currentOffset + currentPageSize, recordList.size());
            LOG.info("Start and stop offsets {0}, {1}",
                    currentOffset, stopOffset);
           // for (int xx=currentOffset; xx < stopOffset; xx++) {
            for (int xx=0; xx < recordList.size(); xx++) {
                resultsHandler.handle(
                        new ConnectorObjectBuilder()
                                .setUid(recordList.get(xx).getId())
                                .setName(recordList.get(xx).getName())
                                .setObjectClass(getType())
                                .build());
            }

            boolean allResultsReturned = currentOffset + currentPageSize >= recordList.size();
            int remainingResults = allResultsReturned ? 0
                    : recordList.size() - currentOffset - currentPageSize;

            if (resultsHandler instanceof SearchResultsHandler) {
                LOG.info("AAA Constructing SearchResult with cookie {0}, remainingResults {1}, "
                                + "allResultsReturned {2}", currentSearchCookie, remainingResults,
                        allResultsReturned);
                /*
                SearchResult searchResult = new SearchResult(currentSearchCookie, remainingResults,
                        allResultsReturned);

                 */
                SearchResult searchResult = new SearchResult(currentSearchCookie, 0);
                ((SearchResultsHandler) resultsHandler).handleResult(searchResult);
            } else {
                LOG.info("ResultsHandler not a SearchResultsHandler");
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
        if (currentGroupIds == null) {
            currentGroupIds = new ArrayList<>();
        }

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
