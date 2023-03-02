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

import com.exclamationlabs.connid.base.connector.stub.StubReadOnlyConnector;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.util.StubInvocationChecker;
import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.api.operations.*;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StubReadOnlyConnectorTest
    extends ApiIntegrationTest<StubConfiguration, StubReadOnlyConnector> {

  @Override
  protected StubConfiguration getConfiguration() {
    return new StubConfiguration();
  }

  @Override
  protected Class<StubReadOnlyConnector> getConnectorClass() {
    return StubReadOnlyConnector.class;
  }

  @Override
  protected void readConfiguration(StubConfiguration configuration) {}

  @BeforeEach
  public void setup() {
    super.setup();
    StubInvocationChecker.reset();
  }

  @Test
  public void testTest() {
    getConnectorFacade().test();
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("test", StubInvocationChecker.getMethodInvoked());
  }

  @Test
  public void testSchema() {
    assertNotNull(getConnectorFacade().schema());
  }

  @Test
  public void testConnectorPermissions() {
    StubReadOnlyConnector connector = new StubReadOnlyConnector();
    assertTrue(connector.readEnabled());
    assertTrue(connector.isReadOnly());
    assertFalse(connector.isWriteOnly());
    assertFalse(connector.isCrud());
    assertFalse(connector.createEnabled());
    assertFalse(connector.updateEnabled());
    assertFalse(connector.deleteEnabled());
  }

  @Test
  public void testPermissions() {
    Set<?> operationSet = getConnectorFacade().getSupportedOperations();
    assertTrue(operationSet.contains(SearchApiOp.class));
    assertTrue(operationSet.contains(GetApiOp.class));
    assertTrue(operationSet.contains(SchemaApiOp.class));
    assertTrue(operationSet.contains(TestApiOp.class));
    assertFalse(operationSet.contains(DeleteApiOp.class));
    assertFalse(operationSet.contains(CreateApiOp.class));
    assertFalse(operationSet.contains(UpdateDeltaApiOp.class));
  }

  @Test
  public void testUsersGet() {
    results = new ArrayList<>();
    getConnectorFacade()
        .search(new ObjectClass("user"), null, handler, new OperationOptionsBuilder().build());
    assertTrue(results.size() > 1);
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));

    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user getAll none", StubInvocationChecker.getMethodInvoked());
    assertNull(StubInvocationChecker.getMethodParameter1());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testUserGet() {
    Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("1234").build();

    results = new ArrayList<>();
    getConnectorFacade()
        .search(
            new ObjectClass("user"),
            new EqualsFilter(idAttribute),
            handler,
            new OperationOptionsBuilder().build());

    assertEquals(1, results.size());
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));

    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user getOne", StubInvocationChecker.getMethodInvoked());
    assertEquals("1234", StubInvocationChecker.getMethodParameter1().toString());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testGroupsGet() {
    results = new ArrayList<>();
    getConnectorFacade()
        .search(new ObjectClass("group"), null, handler, new OperationOptionsBuilder().build());

    assertTrue(results.size() > 1);
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));

    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("group getAll", StubInvocationChecker.getMethodInvoked());
    assertNull(StubInvocationChecker.getMethodParameter1());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testGroupGet() {
    Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("1234").build();

    results = new ArrayList<>();
    getConnectorFacade()
        .search(
            new ObjectClass("group"),
            new EqualsFilter(idAttribute),
            handler,
            new OperationOptionsBuilder().build());

    assertEquals(1, results.size());
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));

    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("group getOne", StubInvocationChecker.getMethodInvoked());
    assertEquals("1234", StubInvocationChecker.getMethodParameter1().toString());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testCreateUser() {
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            getConnectorFacade()
                .create(
                    new ObjectClass("user"),
                    Collections.emptySet(),
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testCreateGroup() {
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            getConnectorFacade()
                .create(
                    new ObjectClass("group"),
                    Collections.emptySet(),
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testUpdateUser() {
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            getConnectorFacade()
                .updateDelta(
                    new ObjectClass("user"),
                    new Uid("test"),
                    Collections.emptySet(),
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testUpdateGroup() {
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            getConnectorFacade()
                .updateDelta(
                    new ObjectClass("group"),
                    new Uid("test"),
                    Collections.emptySet(),
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testDeleteUser() {
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            getConnectorFacade()
                .delete(
                    new ObjectClass("user"),
                    new Uid("test"),
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testDeleteGroup() {
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            getConnectorFacade()
                .delete(
                    new ObjectClass("group"),
                    new Uid("test"),
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testConstruction() {
    StubReadOnlyConnector connector = new StubReadOnlyConnector();
    connector.init(new StubConfiguration());
    StubConnectorTest.executeTestConstruction(connector, "StubReadOnlyConnector");
  }

  @Test
  public void testDummyAuthentication() {
    StubReadOnlyConnector connector = new StubReadOnlyConnector();
    connector.setAuthenticator(configuration -> "testing");
    assertEquals(
        "testing",
        connector.getAuthenticator().authenticate(connector.getConnectorConfiguration()));
  }

  @Test
  public void testSchemaConnector() {
    StubReadOnlyConnector connector = new StubReadOnlyConnector();
    StubConnectorTest.executeTestSchema(connector, 0);
  }
}
