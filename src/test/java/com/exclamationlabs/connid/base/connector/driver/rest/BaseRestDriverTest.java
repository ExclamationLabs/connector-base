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

package com.exclamationlabs.connid.base.connector.driver.rest;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.configuration.DefaultConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.RestConfiguration;
import com.exclamationlabs.connid.base.connector.driver.DriverInvocator;
import com.exclamationlabs.connid.base.connector.driver.exception.DriverRenewableTokenExpiredException;
import com.exclamationlabs.connid.base.connector.driver.exception.DriverTokenExpiredException;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import com.exclamationlabs.connid.base.connector.test.util.ConnectorMockRestTest;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class BaseRestDriverTest extends ConnectorMockRestTest {

    private static final String USER_ID = "123";
    private static final String USER_NAME = "Bob";
    private static final String USER_EMAIL = "bob@bob.com";
    private static final String USER_ID2 = "456";
    private static final String USER_NAME2 = "Jack";
    private static final String USER_EMAIL2 = "jack@jack.com";

    private static final String GROUP_ID = "777";
    private static final String GROUP_NAME = "Rabbits";
    private static final String GROUP_ID2 = "888";
    private static final String GROUP_NAME2 = "Bunnies";

    private static final String SINGLE_USER_RESPONSE =
            "{id:\"" + USER_ID + "\", " +
            " userName:\"" + USER_NAME + "\", " +
            " email:\"" + USER_EMAIL + "\" }";
    private static final String SECOND_USER_RESPONSE =
            "{id:\"" + USER_ID2 + "\", " +
                    " userName:\"" + USER_NAME2 + "\", " +
                    " email:\"" + USER_EMAIL2 + "\" }";
    private static final String MULTI_USER_RESPONSE =
            "{people: [ " + SINGLE_USER_RESPONSE + "," +
                    SECOND_USER_RESPONSE + " ]}";

    private static final String MULTI_USER_DUPLICATE_RESPONSE =
            "{people: [ " + SINGLE_USER_RESPONSE + "," +
                    SECOND_USER_RESPONSE + "," + SECOND_USER_RESPONSE + " ]}";

    private static final String SINGLE_GROUP_RESPONSE =
            "{id:\"" + GROUP_ID + "\", " +
                    " name:\"" + GROUP_NAME + "\" }";
    private static final String SECOND_GROUP_RESPONSE =
            "{id:\"" + GROUP_ID2 + "\", " +
                    " name:\"" + GROUP_NAME2 + "\" }";
    private static final String MULTI_GROUP_RESPONSE =
            "{groups: [ " + SINGLE_GROUP_RESPONSE + "," +
                    SECOND_GROUP_RESPONSE + " ]}";

    private BaseRestDriver driver;
    private static final Map<String, String> moreHeaders = new HashMap<>();

    @Before
    public void setup() {
        CustomRetryConfiguration retryConfiguration = new CustomRetryConfiguration();
        retryConfiguration.setRestIoErrorRetries(5);
        driver = new TestRestDriver();
        driver.initialize(retryConfiguration, configuration -> "good");
        moreHeaders.put("uno", "one");
        moreHeaders.put("dos", "two");
    }

    @Test
    public void getOneUser() {
        prepareMockResponse(SINGLE_USER_RESPONSE);
        StubUser user = (StubUser) driver.getOne(StubUser.class, USER_ID, Collections.emptyMap());
        assertEquals(USER_NAME, user.getUserName());
        assertEquals(USER_EMAIL, user.getEmail());
    }

    @Test
    public void getUsers() {
        prepareMockResponse(MULTI_USER_RESPONSE);
        Set<IdentityModel> users = driver.getAll(StubUser.class, Collections.emptyMap());
        List<IdentityModel> userList = new ArrayList<>(users);
        userList.sort(Comparator.comparing(IdentityModel::getIdentityNameValue));
        assertEquals(USER_NAME, ((StubUser) userList.get(0)).getUserName());
        assertEquals(USER_EMAIL, ((StubUser) userList.get(0)).getEmail());
        assertEquals(USER_NAME2, ((StubUser) userList.get(1)).getUserName());
        assertEquals(USER_EMAIL2, ((StubUser) userList.get(1)).getEmail());
    }

    @Test
    public void getUsersNoDuplicates() {
        prepareMockResponse(MULTI_USER_DUPLICATE_RESPONSE);
        Set<IdentityModel> users = driver.getAll(StubUser.class, Collections.emptyMap());
        assertEquals(2, users.size());
    }

    @Test
    public void getOneGroup() {
        prepareMockResponse(SINGLE_GROUP_RESPONSE);
        StubGroup group = (StubGroup) driver.getOne(StubGroup.class, GROUP_ID, Collections.emptyMap());
        assertEquals(GROUP_NAME, group.getName());
    }

    @Test
    public void getGroups() {
        prepareMockResponse(MULTI_GROUP_RESPONSE);
        Set<IdentityModel> groups = driver.getAll(StubGroup.class, Collections.emptyMap());
        List<IdentityModel> groupsList = new ArrayList<>(groups);
        groupsList.sort(Comparator.comparing(IdentityModel::getIdentityNameValue));

        assertEquals(GROUP_NAME2, ((StubGroup) groupsList.get(0)).getName());
        assertEquals(GROUP_NAME, ((StubGroup) groupsList.get(1)).getName());
    }

    @Test
    public void createUser() {
        prepareMockResponse(SINGLE_USER_RESPONSE);
        StubUser newUser = new StubUser();
        newUser.setUserName(USER_NAME);
        newUser.setEmail(USER_EMAIL);
        assertEquals(USER_ID, driver.create(StubUser.class, newUser));
    }

    @Test
    public void createGroup() {
        prepareMockResponse(SINGLE_GROUP_RESPONSE);
        StubGroup newGroup = new StubGroup();
        newGroup.setName(GROUP_NAME);
        assertEquals(GROUP_ID, driver.create(StubGroup.class, newGroup));
    }

    @Test
    public void updateUserPatch() {
        prepareMockResponse();
        StubUser updateUser = new StubUser();
        updateUser.setUserName(USER_NAME);
        driver.update(StubUser.class, USER_ID, updateUser);
    }

    @Test
    public void updateGroupPut() {
        prepareMockResponse();
        StubGroup group = new StubGroup();
        group.setName(GROUP_NAME);
        driver.update(StubGroup.class, GROUP_ID, group);
    }

    @Test
    public void deleteUser() {
        prepareMockResponse();
        driver.delete(StubUser.class, USER_ID);
    }

    @Test
    public void deleteGroup() {
        prepareMockResponse();
        driver.delete(StubGroup.class, GROUP_ID);
    }

    @Test
    public void testIOExceptionRetries() throws IOException {
        CustomRetryConfiguration retryConfiguration = new CustomRetryConfiguration();
        retryConfiguration.setRestIoErrorRetries(2);
        driver = new TestRestDriver();
        driver.initialize(retryConfiguration, configuration -> "good");
        moreHeaders.put("uno", "one");
        moreHeaders.put("dos", "two");

        prepareMockResponseAfterTwoExceptions(new IOException("test"));
        StubUser user = (StubUser) driver.getOne(StubUser.class, USER_ID, Collections.emptyMap());
        assertEquals(USER_NAME, user.getUserName());
        assertEquals(USER_EMAIL, user.getEmail());
    }

    @Test(expected = UnknownUidException.class)
    public void getOneUserNotFound() {
        prepareClientFaultResponse("{not_found:1}", HttpStatus.SC_NOT_FOUND);
        driver.getOne(StubUser.class, USER_ID, Collections.emptyMap());
    }

    @Test(expected = ConnectorException.class)
    public void getOneUserFatalClientException() {
        prepareClientException(new ClientProtocolException());
        driver.getOne(StubUser.class, USER_ID, Collections.emptyMap());
    }

    @Test(expected = ConnectorException.class)
    public void getOneUserFatalClientIOException() {
        prepareClientException(new IOException());
        driver.getOne(StubUser.class, USER_ID, Collections.emptyMap());
    }

    @Test(expected = ConnectorException.class)
    public void getOneUserFatalTokenExpirationException() {
        prepareClientException(new DriverTokenExpiredException());
        driver.getOne(StubUser.class, USER_ID, Collections.emptyMap());
    }

    @Test(expected = ConnectorException.class)
    public void getOneUserRenewableTokenExpirationException() {
        prepareClientException(new DriverRenewableTokenExpiredException());
        driver.getOne(StubUser.class, USER_ID, Collections.emptyMap());
    }

    class TestRestDriver extends BaseRestDriver {

        public TestRestDriver() {
            addInvocator(StubUser.class, new TestRestUserInvocator());
            addInvocator(StubGroup.class, new TestRestGroupInvocator());
        }

        @Override
        protected HttpClient createClient() {
            return stubClient;
        }

        @Override
        protected RestFaultProcessor getFaultProcessor() {
            return new TestFaultProcessor();
        }

        @Override
        protected String getBaseServiceUrl() {
            return "http://www.somewhere-out-there.com";
        }

        @Override
        public Set<ConnectorProperty> getRequiredPropertyNames() {
            return null;
        }

        @Override
        public void test() throws ConnectorException {

        }

        @Override
        public void close() {

        }

    }

    static class TestRestUserInvocator implements DriverInvocator<BaseRestDriver,StubUser> {

        @Override
        public String create(BaseRestDriver driver, StubUser userModel)
                throws ConnectorException {
            StubUser response = driver.executePostRequest(
                    "/users", StubUser.class, userModel, moreHeaders).getResponseObject();
            return response.getId();
        }

        @Override
        public void update(BaseRestDriver driver, String userId, StubUser userModel)
                throws ConnectorException {
            driver.executePatchRequest(
                    "/users/" + userId, null, userModel, moreHeaders);
        }

        @Override
        public void delete(BaseRestDriver driver, String userId) throws ConnectorException {
            driver.executeDeleteRequest("/users/" + userId, null, moreHeaders);
        }

        @Override
        public Set<StubUser> getAll(BaseRestDriver driver, Map<String,Object> operationOptionsData)
                throws ConnectorException {
            TestUsersResponse usersResponse = driver.executeGetRequest(
                    "/users", TestUsersResponse.class, moreHeaders).getResponseObject();
            return new HashSet<>(usersResponse.getPeople());
        }

        @Override
        public Set<StubUser> getAllFiltered(BaseRestDriver driver, Map<String, Object> operationOptionsData, String filterAttribute, String filterValue) throws ConnectorException {
            return getAll(driver, operationOptionsData);
        }

        @Override
        public StubUser getOne(BaseRestDriver driver, String userId, Map<String,Object> operationOptionsData)
                throws ConnectorException {
            return driver.executeGetRequest("/users/" + userId, StubUser.class, moreHeaders).getResponseObject();
        }
    }

    static class TestRestGroupInvocator implements DriverInvocator<BaseRestDriver,StubGroup> {

        @Override
        public String create(BaseRestDriver driver, StubGroup userModel)
                             throws ConnectorException {
            StubGroup response = driver.executePostRequest(
                    "/groups", StubGroup.class, userModel, moreHeaders).getResponseObject();
            return response.getId();
        }

        @Override
        public void update(BaseRestDriver driver, String userId, StubGroup userModel)
                throws ConnectorException {
            driver.executePatchRequest(
                    "/groups/" + userId, null, userModel, moreHeaders);
        }

        @Override
        public void delete(BaseRestDriver driver, String userId) throws ConnectorException {
            driver.executeDeleteRequest("/groups/" + userId, null, moreHeaders);
        }

        @Override
        public Set<StubGroup> getAll(BaseRestDriver driver, Map<String,Object> operationOptionsData)
                throws ConnectorException {
            TestGroupsResponse groupsResponse = driver.executeGetRequest(
                    "/groups", TestGroupsResponse.class, moreHeaders).getResponseObject();
            return new HashSet<>(groupsResponse.getGroups());
        }

        @Override
        public Set<StubGroup> getAllFiltered(BaseRestDriver driver, Map<String, Object> operationOptionsData, String filterAttribute, String filterValue) throws ConnectorException {
            return getAll(driver, operationOptionsData);
        }

        @Override
        public StubGroup getOne(BaseRestDriver driver, String groupId, Map<String,Object> operationOptionsData)
                throws ConnectorException {
            return driver.executeGetRequest("/groups/" + groupId, StubGroup.class, moreHeaders).getResponseObject();
        }
    }

    static class TestFaultProcessor implements RestFaultProcessor {

        @Override
        public void process(HttpResponse response, GsonBuilder gsonBuilder) throws ConnectorException {
            try {
                String entitySize = "" + response.getEntity().getContent().available();

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                    throw new UnknownUidException(entitySize);
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }

        }
    }

    static class TestUsersResponse {
        List<StubUser> people;

        public List<StubUser> getPeople() {
            return people;
        }

        @SuppressWarnings("unused")
        public void setPeople(List<StubUser> people) {
            this.people = people;
        }
    }

    static class TestGroupsResponse {
        List<StubGroup> groups;

        public List<StubGroup> getGroups() {
            return groups;
        }

        @SuppressWarnings("unused")
        public void setGroups(List<StubGroup> groups) {
            this.groups = groups;
        }
    }


    protected void prepareMockResponseAfterTwoExceptions(Throwable t) throws IOException {
        Mockito.when(this.stubResponseEntity.getContent()).thenReturn(
                new ByteArrayInputStream( SINGLE_USER_RESPONSE.getBytes()));
        Mockito.when(this.stubResponse.getEntity()).thenReturn(this.stubResponseEntity);

        Mockito.when(stubResponse.getStatusLine()).thenReturn(stubStatusLine).thenReturn(stubStatusLine).thenReturn(stubStatusLine);
        Mockito.when(stubStatusLine.getStatusCode()).thenReturn(408).thenReturn(408).thenReturn(200);
        Mockito.when(stubClient.execute(any(HttpRequestBase.class))).thenThrow(t).thenThrow(t).thenReturn(stubResponse);
    }

    static class CustomRetryConfiguration extends DefaultConnectorConfiguration implements RestConfiguration {

        private Integer retries;

        @Override
        public void validate() {
        }

        public CustomRetryConfiguration() {
            super();
        }

        @Override
        public Integer getRestIoErrorRetries() {
            return retries;
        }

        @Override
        public void setRestIoErrorRetries(Integer input) {
            retries = input;
        }
    }
}
