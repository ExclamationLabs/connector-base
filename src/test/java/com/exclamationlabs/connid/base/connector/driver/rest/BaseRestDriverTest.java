package com.exclamationlabs.connid.base.connector.driver.rest;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import com.exclamationlabs.connid.base.connector.test.util.ConnectorMockRestTest;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

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

    private static final String SINGLE_GROUP_RESPONSE =
            "{id:\"" + GROUP_ID + "\", " +
                    " name:\"" + GROUP_NAME + "\" }";
    private static final String SECOND_GROUP_RESPONSE =
            "{id:\"" + GROUP_ID2 + "\", " +
                    " name:\"" + GROUP_NAME2 + "\" }";
    private static final String MULTI_GROUP_RESPONSE =
            "{groups: [ " + SINGLE_GROUP_RESPONSE + "," +
                    SECOND_GROUP_RESPONSE + " ]}";

    private BaseRestDriver<StubUser, StubGroup> driver;

    @Before
    public void setup() {
        driver = new TestRestDriver();
        driver.initialize(new StubConfiguration(), new Authenticator() {
            @Override
            public Set<ConnectorProperty> getRequiredPropertyNames() {
                return null;
            }

            @Override
            public String authenticate(BaseConnectorConfiguration configuration) throws ConnectorSecurityException {
                return "good";
            }
        });
    }

    @Test
    public void getOneUser() {
        prepareMockResponse(SINGLE_USER_RESPONSE);
        StubUser user = driver.getUser(USER_ID);
        assertEquals(USER_NAME, user.getUserName());
        assertEquals(USER_EMAIL, user.getEmail());
    }

    @Test
    public void getUsers() {
        prepareMockResponse(MULTI_USER_RESPONSE);
        List<StubUser> users = driver.getUsers();
        assertEquals(USER_NAME, users.get(0).getUserName());
        assertEquals(USER_EMAIL, users.get(0).getEmail());
        assertEquals(USER_NAME2, users.get(1).getUserName());
        assertEquals(USER_EMAIL2, users.get(1).getEmail());
    }

    @Test
    public void getOneGroup() {
        prepareMockResponse(SINGLE_GROUP_RESPONSE);
        StubGroup group = driver.getGroup(GROUP_ID);
        assertEquals(GROUP_NAME, group.getName());
    }

    @Test
    public void getGroups() {
        prepareMockResponse(MULTI_GROUP_RESPONSE);
        List<StubGroup> groups = driver.getGroups();
        assertEquals(GROUP_NAME, groups.get(0).getName());
        assertEquals(GROUP_NAME2, groups.get(1).getName());
    }

    @Test
    public void createUser() {
        prepareMockResponse(SINGLE_USER_RESPONSE);
        StubUser newUser = new StubUser();
        newUser.setUserName(USER_NAME);
        newUser.setEmail(USER_EMAIL);
        assertEquals(USER_ID, driver.createUser(newUser));
    }

    @Test
    public void createGroup() {
        prepareMockResponse(SINGLE_GROUP_RESPONSE);
        StubGroup newGroup = new StubGroup();
        newGroup.setName(GROUP_NAME);
        assertEquals(GROUP_ID, driver.createGroup(newGroup));
    }

    @Test
    public void updateUserPatch() {
        prepareMockResponseEmpty();
        StubUser updateUser = new StubUser();
        updateUser.setUserName(USER_NAME);
        driver.updateUser(USER_ID, updateUser);
    }

    @Test
    public void updateGroupPut() {
        prepareMockResponseEmpty();
        StubGroup group = new StubGroup();
        group.setName(GROUP_NAME);
        driver.updateGroup(GROUP_ID, group);
    }

    @Test
    public void deleteUser() {
        prepareMockResponseEmpty();
        driver.deleteUser(USER_ID);
    }

    @Test
    public void deleteGroup() {
        prepareMockResponseEmpty();
        driver.deleteGroup(GROUP_ID);
    }

    @Test(expected = UnknownUidException.class)
    public void getOneUserNotFound() {
        prepareClientFaultResponse("{not_found:1}", HttpStatus.SC_NOT_FOUND);
        driver.getUser(USER_ID);
    }

    @Test(expected = ConnectorException.class)
    public void getOneUserFatalClientException() {
        prepareClientException(new ClientProtocolException());
        driver.getUser(USER_ID);
    }

    @Test(expected = ConnectorException.class)
    public void getOneUserFatalClientIOException() {
        prepareClientException(new IOException());
        driver.getUser(USER_ID);
    }

    class TestRestDriver extends BaseRestDriver<StubUser, StubGroup> {

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

        @Override
        public String createUser(StubUser userModel) throws ConnectorException {
            StubUser response = executePostRequest("/users", StubUser.class, userModel);
            return response.getId();
        }

        @Override
        public String createGroup(StubGroup groupModel) throws ConnectorException {
            StubGroup response = executePostRequest("/groups", StubGroup.class, groupModel);
            return response.getId();
        }

        @Override
        public void updateUser(String userId, StubUser userModel) throws ConnectorException {
            executePatchRequest("/users/" + userId, null, userModel);
        }

        @Override
        public void updateGroup(String groupId, StubGroup groupModel) throws ConnectorException {
            executePutRequest("/groups/" + groupId, null, groupModel);
        }

        @Override
        public void deleteUser(String userId) throws ConnectorException {
            executeDeleteRequest("/users/" + userId, null);
        }

        @Override
        public void deleteGroup(String groupId) throws ConnectorException {
            executeDeleteRequest("/groups/" + groupId, null);
        }

        @Override
        public List<StubUser> getUsers() throws ConnectorException {
            TestUsersResponse usersResponse = executeGetRequest("/users", TestUsersResponse.class);
            return usersResponse.getPeople();
        }

        @Override
        public List<StubGroup> getGroups() throws ConnectorException {
            TestGroupsResponse usersResponse = executeGetRequest("/groups", TestGroupsResponse.class);
            return usersResponse.getGroups();
        }

        @Override
        public StubUser getUser(String userId) throws ConnectorException {
            return executeGetRequest("/users/" + userId, StubUser.class);
        }

        @Override
        public StubGroup getGroup(String groupId) throws ConnectorException {
            return executeGetRequest("/groups/" + groupId, StubGroup.class);
        }

        @Override
        public void addGroupToUser(String groupId, String userId) throws ConnectorException {

        }

        @Override
        public void removeGroupFromUser(String groupId, String userId) throws ConnectorException {

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
}
