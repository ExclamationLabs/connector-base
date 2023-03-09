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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.exclamationlabs.connid.base.connector.configuration.basetypes.RestConfiguration;
import com.exclamationlabs.connid.base.connector.driver.DriverInvocator;
import com.exclamationlabs.connid.base.connector.driver.exception.DriverRenewableTokenExpiredException;
import com.exclamationlabs.connid.base.connector.driver.exception.DriverTokenExpiredException;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import com.exclamationlabs.connid.base.connector.test.util.ConnectorMockRestTest;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BaseRestDriverTest extends ConnectorMockRestTest {

  private static final String USER_ID = "123";

  private static final String USER_ID_CUSTOM_RESPONSE = "999111";
  private static final String USER_ID_CUSTOM_RESPONSE2 = "999222";

  private static final String USER_ID_RAW = "456";
  private static final String USER_NAME = "Bob";
  private static final String USER_NAME_RAW = "BobRaw";
  private static final String USER_NAME_RAW2 = "BobRaw2";

  private static final String USER_EMAIL = "bob@bob.com";
  private static final String USER_ID2 = "456";
  private static final String USER_NAME2 = "Jack";
  private static final String USER_EMAIL2 = "jack@jack.com";

  private static final String GROUP_ID = "777";
  private static final String GROUP_NAME = "Rabbits";
  private static final String GROUP_ID2 = "888";
  private static final String GROUP_NAME2 = "Bunnies";

  private static final String SINGLE_USER_RESPONSE =
      "{id:\""
          + USER_ID
          + "\", "
          + " userName:\""
          + USER_NAME
          + "\", "
          + " email:\""
          + USER_EMAIL
          + "\" }";
  private static final String SECOND_USER_RESPONSE =
      "{id:\""
          + USER_ID2
          + "\", "
          + " userName:\""
          + USER_NAME2
          + "\", "
          + " email:\""
          + USER_EMAIL2
          + "\" }";
  private static final String MULTI_USER_RESPONSE =
      "{people: [ " + SINGLE_USER_RESPONSE + "," + SECOND_USER_RESPONSE + " ]}";

  private static final String MULTI_USER_DUPLICATE_RESPONSE =
      "{people: [ "
          + SINGLE_USER_RESPONSE
          + ","
          + SECOND_USER_RESPONSE
          + ","
          + SECOND_USER_RESPONSE
          + " ]}";

  private static final String SINGLE_GROUP_RESPONSE =
      "{id:\"" + GROUP_ID + "\", " + " name:\"" + GROUP_NAME + "\" }";
  private static final String SECOND_GROUP_RESPONSE =
      "{id:\"" + GROUP_ID2 + "\", " + " name:\"" + GROUP_NAME2 + "\" }";
  private static final String MULTI_GROUP_RESPONSE =
      "{groups: [ " + SINGLE_GROUP_RESPONSE + "," + SECOND_GROUP_RESPONSE + " ]}";

  private BaseRestDriver<StubConfiguration> driver;
  private static final Map<String, String> moreHeaders = new HashMap<>();

  @BeforeEach
  public void setup() {
    CustomRetryConfiguration retryConfiguration = new CustomRetryConfiguration();
    retryConfiguration.setIoErrorRetries(5);
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
  public void getOneUserTestCustomResponseHandling() {
    prepareMockResponse();
    StubUser user =
        (StubUser) driver.getOne(StubUser.class, USER_ID_CUSTOM_RESPONSE, Collections.emptyMap());
    assertNull(user);
  }

  @Test
  public void getOneUserTestCustomResponseHandlingThrower() {
    prepareMockResponse();
    assertThrows(
        ConnectorException.class,
        () -> driver.getOne(StubUser.class, USER_ID_CUSTOM_RESPONSE2, Collections.emptyMap()));
  }

  @Test
  public void getUsers() {
    prepareMockResponse(MULTI_USER_RESPONSE);
    Set<IdentityModel> users =
        driver.getAll(StubUser.class, new ResultsFilter(), new ResultsPaginator(), null);
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
    Set<IdentityModel> users =
        driver.getAll(StubUser.class, new ResultsFilter(), new ResultsPaginator(), null);
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
    Set<IdentityModel> groups =
        driver.getAll(StubGroup.class, new ResultsFilter(), new ResultsPaginator(), null);
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
  public void createUserRawStringInputAndResponse() {
    prepareMockResponse(USER_ID_RAW);
    StubUser newUser = new StubUser();
    newUser.setUserName(USER_NAME_RAW);
    newUser.setEmail(USER_EMAIL);
    assertEquals(USER_ID_RAW, driver.create(StubUser.class, newUser));
  }

  @Test
  public void createUserVoidInputAndResponse() {
    prepareMockResponse();
    StubUser newUser = new StubUser();
    newUser.setUserName(USER_NAME_RAW2);
    newUser.setEmail(USER_EMAIL);
    assertEquals("wasRaw", driver.create(StubUser.class, newUser));
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
    retryConfiguration.setIoErrorRetries(2);
    driver = new TestRestDriver();
    driver.initialize(retryConfiguration, configuration -> "good");
    moreHeaders.put("uno", "one");
    moreHeaders.put("dos", "two");

    prepareMockResponseAfterTwoExceptions(new IOException("test"));
    StubUser user = (StubUser) driver.getOne(StubUser.class, USER_ID, Collections.emptyMap());
    assertEquals(USER_NAME, user.getUserName());
    assertEquals(USER_EMAIL, user.getEmail());
  }

  @Test
  public void getOneUserNotFound() {
    prepareClientFaultResponse("{not_found:1}", HttpStatus.SC_NOT_FOUND);
    assertThrows(
        UnknownUidException.class,
        () -> driver.getOne(StubUser.class, USER_ID, Collections.emptyMap()));
  }

  @Test
  public void getOneUserFatalClientException() {
    prepareClientException(new ClientProtocolException());
    assertThrows(
        ConnectorException.class,
        () -> driver.getOne(StubUser.class, USER_ID, Collections.emptyMap()));
  }

  @Test
  public void getOneUserFatalClientIOException() {
    prepareClientException(new IOException());
    assertThrows(
        ConnectorException.class,
        () -> driver.getOne(StubUser.class, USER_ID, Collections.emptyMap()));
  }

  @Test
  public void getOneUserFatalTokenExpirationException() {
    prepareClientException(new DriverTokenExpiredException());
    assertThrows(
        ConnectorException.class,
        () -> driver.getOne(StubUser.class, USER_ID, Collections.emptyMap()));
  }

  @Test
  public void getOneUserRenewableTokenExpirationException() {
    prepareClientException(new DriverRenewableTokenExpiredException());
    assertThrows(
        ConnectorException.class,
        () -> driver.getOne(StubUser.class, USER_ID, Collections.emptyMap()));
  }

  class TestRestDriver extends BaseRestDriver<StubConfiguration> {

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
      return "https://www.somewhere-out-there.com";
    }

    @Override
    public void test() throws ConnectorException {}

    @Override
    public void close() {}

    @Override
    protected boolean performAdditionalResponseHandling(
        int responseStatusCode, Header[] responseHeaders, String method, URI uri)
        throws RuntimeException {
      if (uri.toString().contains(USER_ID_CUSTOM_RESPONSE)) {
        return true;
      } else if (uri.toString().contains(USER_ID_CUSTOM_RESPONSE2)) {
        throw new ConnectorException("get out");
      }
      return false;
    }
  }

  static class TestRestUserInvocator
      implements DriverInvocator<BaseRestDriver<StubConfiguration>, StubUser> {

    @Override
    public String create(BaseRestDriver<StubConfiguration> driver, StubUser userModel)
        throws ConnectorException {
      if (USER_NAME_RAW.equalsIgnoreCase(userModel.getUserName())) {
        return driver
            .executeRequest(
                new RestRequest.Builder<>(String.class)
                    .withRequestUri("/users")
                    .withPost()
                    .withRequestBody("raw Bob creation request")
                    .withHeaders(moreHeaders)
                    .build())
            .getResponseObject();
      } else if (USER_NAME_RAW2.equalsIgnoreCase(userModel.getUserName())) {
        driver.executeRequest(
            new RestRequest.Builder<>(Void.class)
                .withRequestUri("/users")
                .withPost()
                .withHeaders(moreHeaders)
                .build());
        return "wasRaw";
      } else {
        StubUser response =
            driver
                .executeRequest(
                    new RestRequest.Builder<>(StubUser.class)
                        .withRequestUri("/users")
                        .withPost()
                        .withRequestBody(userModel)
                        .withHeaders(moreHeaders)
                        .build())
                .getResponseObject();
        return response.getId();
      }
    }

    @Override
    public void update(BaseRestDriver<StubConfiguration> driver, String userId, StubUser userModel)
        throws ConnectorException {
      driver.executeRequest(
          new RestRequest.Builder<>(Void.class)
              .withRequestUri("/users/" + userId)
              .withPatch()
              .withRequestBody(userModel)
              .withHeaders(moreHeaders)
              .build());
    }

    @Override
    public void delete(BaseRestDriver<StubConfiguration> driver, String userId)
        throws ConnectorException {
      driver.executeRequest(
          new RestRequest.Builder<>(Void.class)
              .withRequestUri("/users/" + userId)
              .withDelete()
              .withHeaders(moreHeaders)
              .build());
    }

    @Override
    public Set<StubUser> getAll(
        BaseRestDriver<StubConfiguration> driver,
        ResultsFilter filter,
        ResultsPaginator paginator,
        Integer resultCap)
        throws ConnectorException {
      TestUsersResponse usersResponse =
          driver
              .executeRequest(
                  new RestRequest.Builder<>(TestUsersResponse.class)
                      .withRequestUri("/users")
                      .withGet()
                      .withHeaders(moreHeaders)
                      .build())
              .getResponseObject();
      return new HashSet<>(usersResponse.getPeople());
    }

    @Override
    public StubUser getOne(
        BaseRestDriver<StubConfiguration> driver,
        String userId,
        Map<String, Object> operationOptionsData)
        throws ConnectorException {
      return driver
          .executeRequest(
              new RestRequest.Builder<>(StubUser.class)
                  .withRequestUri("/users/" + userId)
                  .withGet()
                  .withHeaders(moreHeaders)
                  .build())
          .getResponseObject();
    }
  }

  static class TestRestGroupInvocator
      implements DriverInvocator<BaseRestDriver<StubConfiguration>, StubGroup> {

    @Override
    public String create(BaseRestDriver<StubConfiguration> driver, StubGroup userModel)
        throws ConnectorException {
      StubGroup response =
          driver
              .executeRequest(
                  new RestRequest.Builder<>(StubGroup.class)
                      .withRequestUri("/groups")
                      .withPost()
                      .withHeaders(moreHeaders)
                      .withRequestBody(userModel)
                      .build())
              .getResponseObject();

      return response.getId();
    }

    @Override
    public void update(BaseRestDriver<StubConfiguration> driver, String userId, StubGroup userModel)
        throws ConnectorException {
      driver.executeRequest(
          new RestRequest.Builder<>(Void.class)
              .withRequestUri("/groups/" + userId)
              .withPatch()
              .withHeaders(moreHeaders)
              .withRequestBody(userModel)
              .build());
    }

    @Override
    public void delete(BaseRestDriver<StubConfiguration> driver, String userId)
        throws ConnectorException {
      driver.executeRequest(
          new RestRequest.Builder<>(Void.class)
              .withRequestUri("/groups/" + userId)
              .withDelete()
              .withHeaders(moreHeaders)
              .build());
    }

    @Override
    public Set<StubGroup> getAll(
        BaseRestDriver<StubConfiguration> driver,
        ResultsFilter filter,
        ResultsPaginator paginator,
        Integer resultCap)
        throws ConnectorException {
      TestGroupsResponse groupsResponse =
          driver
              .executeRequest(
                  new RestRequest.Builder<>(TestGroupsResponse.class)
                      .withRequestUri("/groups")
                      .withGet()
                      .withHeaders(moreHeaders)
                      .build())
              .getResponseObject();
      return new HashSet<>(groupsResponse.getGroups());
    }

    @Override
    public StubGroup getOne(
        BaseRestDriver<StubConfiguration> driver,
        String groupId,
        Map<String, Object> operationOptionsData)
        throws ConnectorException {
      return driver
          .executeRequest(
              new RestRequest.Builder<>(StubGroup.class)
                  .withRequestUri("/groups/" + groupId)
                  .withGet()
                  .withHeaders(moreHeaders)
                  .build())
          .getResponseObject();
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
    Mockito.when(this.stubResponseEntity.getContent())
        .thenReturn(new ByteArrayInputStream(SINGLE_USER_RESPONSE.getBytes()));
    Mockito.when(this.stubResponse.getEntity()).thenReturn(this.stubResponseEntity);

    Mockito.when(stubResponse.getStatusLine())
        .thenReturn(stubStatusLine)
        .thenReturn(stubStatusLine)
        .thenReturn(stubStatusLine);
    Mockito.when(stubStatusLine.getStatusCode()).thenReturn(408).thenReturn(408).thenReturn(200);
    Mockito.when(stubClient.execute(any(HttpRequestBase.class)))
        .thenThrow(t)
        .thenThrow(t)
        .thenReturn(stubResponse);
  }

  static class CustomRetryConfiguration extends StubConfiguration implements RestConfiguration {

    private Integer retries;

    @Override
    public void validate() {}

    public CustomRetryConfiguration() {
      super();
    }

    @Override
    public Integer getIoErrorRetries() {
      return retries;
    }

    @Override
    public void setIoErrorRetries(Integer input) {
      retries = input;
    }
  }
}
