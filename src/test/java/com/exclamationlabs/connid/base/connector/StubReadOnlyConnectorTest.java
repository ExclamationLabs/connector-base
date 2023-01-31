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

package com.exclamationlabs.connid.base.connector;

import static org.junit.jupiter.api.Assertions.*;

import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.connector.stub.StubReadOnlyConnector;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.driver.StubDriver;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import com.exclamationlabs.connid.base.connector.test.util.ConnectorTestUtils;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StubReadOnlyConnectorTest {

  private BaseReadOnlyConnector<StubConfiguration> connector;
  private StubDriver driver;
  private OperationOptions testOperationOptions;

  @BeforeEach
  public void setup() {
    connector = new StubReadOnlyConnector();
    StubConfiguration configuration = new StubConfiguration();
    connector.init(configuration);
    driver = (StubDriver) connector.getDriver();
    testOperationOptions = new OperationOptionsBuilder().build();
  }

  @Test
  public void testTest() {
    connector.test();
    assertEquals("test", driver.getMethodInvoked());
  }

  @Test
  public void testPermissions() {
    assertTrue(connector.readEnabled());
    assertTrue(connector.isReadOnly());
    assertFalse(connector.isWriteOnly());
    assertFalse(connector.isCrud());
    assertFalse(connector.createEnabled());
    assertFalse(connector.updateEnabled());
    assertFalse(connector.deleteEnabled());
  }

  @Test
  public void testUsersGet() {
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

    connector.executeQuery(ObjectClass.ACCOUNT, "", resultsHandler, testOperationOptions);
    assertEquals(
        new StubDriver()
            .getAll(StubUser.class, new ResultsFilter(), new ResultsPaginator(), null)
            .size(),
        idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    assertTrue(driver.isInitializeInvoked());
    assertEquals("user getAll none", driver.getMethodInvoked());
    assertNull(driver.getMethodParameter1());
    assertNull(driver.getMethodParameter2());
  }

  @Test
  public void testUserGet() {
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

    connector.executeQuery(ObjectClass.ACCOUNT, "1234", resultsHandler, testOperationOptions);
    assertEquals(1, idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    assertTrue(driver.isInitializeInvoked());
    assertEquals("user getOne", driver.getMethodInvoked());
    assertEquals("1234", driver.getMethodParameter1().toString());
    assertNull(driver.getMethodParameter2());
  }

  @Test
  public void testGroupsGet() {
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

    connector.executeQuery(ObjectClass.GROUP, "", resultsHandler, testOperationOptions);
    assertEquals(
        new StubDriver()
            .getAll(StubGroup.class, new ResultsFilter(), new ResultsPaginator(), null)
            .size(),
        idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    assertTrue(driver.isInitializeInvoked());
    assertEquals("group getAll", driver.getMethodInvoked());
    assertNull(driver.getMethodParameter1());
    assertNull(driver.getMethodParameter2());
  }

  @Test
  public void testGroupGet() {
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

    connector.executeQuery(ObjectClass.GROUP, "1234", resultsHandler, testOperationOptions);
    assertEquals(1, idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    assertTrue(driver.isInitializeInvoked());
    assertEquals("group getOne", driver.getMethodInvoked());
    assertEquals("1234", driver.getMethodParameter1().toString());
    assertNull(driver.getMethodParameter2());
  }

  @Test
  public void testConstruction() {
    StubConnectorTest.executeTestConstruction(connector, "StubReadOnlyConnector");
  }

  @Test
  public void testDummyAuthentication() {
    assertEquals(
        "NA", connector.getAuthenticator().authenticate(connector.getConnectorConfiguration()));
  }

  @Test
  public void testSchema() {
    StubConnectorTest.executeTestSchema(connector, 0);
  }
}
