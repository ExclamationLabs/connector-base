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

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.stub.StubWriteOnlyConnector;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubGroupAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import com.exclamationlabs.connid.base.connector.stub.util.StubInvocationChecker;
import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import java.math.BigDecimal;
import java.util.*;
import org.identityconnectors.framework.api.operations.*;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StubWriteOnlyConnectorTest
    extends ApiIntegrationTest<StubConfiguration, StubWriteOnlyConnector> {

  @Override
  protected StubConfiguration getConfiguration() {
    return new StubConfiguration();
  }

  @Override
  protected Class<StubWriteOnlyConnector> getConnectorClass() {
    return StubWriteOnlyConnector.class;
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
    StubWriteOnlyConnector connector = new StubWriteOnlyConnector();
    assertFalse(connector.readEnabled());
    assertFalse(connector.isReadOnly());
    assertTrue(connector.isWriteOnly());
    assertFalse(connector.isCrud());
    assertTrue(connector.createEnabled());
    assertTrue(connector.updateEnabled());
    assertTrue(connector.deleteEnabled());
  }

  @Test
  public void testPermissions() {
    Set<?> operationSet = getConnectorFacade().getSupportedOperations();
    assertFalse(operationSet.contains(SearchApiOp.class));
    assertFalse(operationSet.contains(GetApiOp.class));
    assertTrue(operationSet.contains(SchemaApiOp.class));
    assertTrue(operationSet.contains(TestApiOp.class));
    assertTrue(operationSet.contains(DeleteApiOp.class));
    assertTrue(operationSet.contains(CreateApiOp.class));
    assertTrue(operationSet.contains(UpdateDeltaApiOp.class));
  }

  @Test
  public void testUserCreateNoGroups() {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_NAME.name())
            .addValue("Dummy")
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.EMAIL.name())
            .addValue("dummy@dummy.com")
            .build());
    Uid newId =
        getConnectorFacade()
            .create(new ObjectClass("user"), attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user create", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertTrue(StubInvocationChecker.getMethodParameter1() instanceof StubUser);
    IdentityModel userParameter = (IdentityModel) StubInvocationChecker.getMethodParameter1();
    assertNull(userParameter.getIdentityIdValue());
    assertNotNull(userParameter.getIdentityNameValue());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testUserCreateWithGroups() {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_NAME.name())
            .addValue("Dummy")
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.EMAIL.name())
            .addValue("dummy@dummy.com")
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.GROUP_IDS.name())
            .addValue(Arrays.asList("id1", "id2"))
            .build());

    Uid newId =
        getConnectorFacade()
            .create(new ObjectClass("user"), attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user create with group ids", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertTrue(StubInvocationChecker.getMethodParameter1() instanceof StubUser);
  }

  @Test
  public void testUserCreateBadDataType() {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_NAME.name())
            .addValue(new BigDecimal(5))
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.EMAIL.name())
            .addValue("dummy@dummy.com")
            .build());

    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            getConnectorFacade()
                .create(
                    new ObjectClass("user"), attributes, new OperationOptionsBuilder().build()));
    assertTrue(StubInvocationChecker.isInitializeInvoked());
  }

  @Test
  public void testUserModifyNoGroups() {
    Set<AttributeDelta> attributes = new HashSet<>();
    attributes.add(
        new AttributeDeltaBuilder()
            .setName(StubUserAttribute.USER_NAME.name())
            .addValueToReplace("Dummy")
            .build());
    attributes.add(
        new AttributeDeltaBuilder()
            .setName(StubUserAttribute.EMAIL.name())
            .addValueToReplace("dummy@dummy.com")
            .build());

    Set<AttributeDelta> response =
        getConnectorFacade()
            .updateDelta(
                new ObjectClass("user"),
                new Uid("1234"),
                attributes,
                new OperationOptionsBuilder().build());
    assertNotNull(response);
    assertTrue(response.isEmpty());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user update", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertTrue(StubInvocationChecker.getMethodParameter1() instanceof String);
    assertEquals("1234", StubInvocationChecker.getMethodParameter1().toString());

    assertNotNull(StubInvocationChecker.getMethodParameter2());
    assertTrue(StubInvocationChecker.getMethodParameter2() instanceof StubUser);
    IdentityModel userParameter = (IdentityModel) StubInvocationChecker.getMethodParameter2();
    assertNull(userParameter.getIdentityIdValue());
    assertNotNull(userParameter.getIdentityNameValue());
  }

  @Test
  public void testUserModifyWithGroups() {
    Set<AttributeDelta> attributes = new HashSet<>();
    attributes.add(
        new AttributeDeltaBuilder()
            .setName(StubUserAttribute.USER_NAME.name())
            .addValueToReplace("Dummy")
            .build());
    attributes.add(
        new AttributeDeltaBuilder()
            .setName(StubUserAttribute.EMAIL.name())
            .addValueToReplace("dummy@dummy.com")
            .build());
    attributes.add(
        new AttributeDeltaBuilder()
            .setName(StubUserAttribute.GROUP_IDS.name())
            .addValueToReplace(Arrays.asList("id1", "id2"))
            .build());

    Set<AttributeDelta> response =
        getConnectorFacade()
            .updateDelta(
                new ObjectClass("user"),
                new Uid("1234"),
                attributes,
                new OperationOptionsBuilder().build());
    assertNotNull(response);
    assertTrue(response.isEmpty());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user update with group ids", StubInvocationChecker.getMethodInvoked());
    assertEquals("1234", StubInvocationChecker.getMethodParameter1().toString());
    assertNotNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testUserModifyDataType() {
    Set<AttributeDelta> attributes = new HashSet<>();
    attributes.add(
        new AttributeDeltaBuilder()
            .setName(StubUserAttribute.USER_NAME.name())
            .addValueToReplace(new BigDecimal(5))
            .build());
    attributes.add(
        new AttributeDeltaBuilder()
            .setName(StubUserAttribute.EMAIL.name())
            .addValueToReplace("dummy@dummy.com")
            .build());

    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            getConnectorFacade()
                .updateDelta(
                    new ObjectClass("user"),
                    new Uid("1234"),
                    attributes,
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testUserDelete() {
    getConnectorFacade()
        .delete(new ObjectClass("user"), new Uid("1234"), new OperationOptionsBuilder().build());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user delete", StubInvocationChecker.getMethodInvoked());
    assertEquals("1234", StubInvocationChecker.getMethodParameter1());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testGroupCreate() {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(
        new AttributeBuilder()
            .setName(StubGroupAttribute.GROUP_NAME.name())
            .addValue("Avengers")
            .build());

    Uid newId =
        getConnectorFacade()
            .create(new ObjectClass("group"), attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("group create", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertTrue(StubInvocationChecker.getMethodParameter1() instanceof StubGroup);
    IdentityModel groupParameter = (IdentityModel) StubInvocationChecker.getMethodParameter1();
    assertNull(groupParameter.getIdentityIdValue());
    assertNotNull(groupParameter.getIdentityNameValue());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testGroupCreateBadDataType() {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(
        new AttributeBuilder()
            .setName(StubGroupAttribute.GROUP_NAME.name())
            .addValue(new BigDecimal(5))
            .build());
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            getConnectorFacade()
                .create(
                    new ObjectClass("group"), attributes, new OperationOptionsBuilder().build()));
  }

  @Test
  public void testGroupModify() {
    Set<AttributeDelta> attributes = new HashSet<>();
    attributes.add(
        new AttributeDeltaBuilder()
            .setName(StubGroupAttribute.GROUP_NAME.name())
            .addValueToReplace("Avengers")
            .build());

    Set<AttributeDelta> response =
        getConnectorFacade()
            .updateDelta(
                new ObjectClass("group"),
                new Uid("1234"),
                attributes,
                new OperationOptionsBuilder().build());
    assertNotNull(response);
    assertTrue(response.isEmpty());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("group update", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertEquals("1234", StubInvocationChecker.getMethodParameter1().toString());

    assertNotNull(StubInvocationChecker.getMethodParameter2());
    assertTrue(StubInvocationChecker.getMethodParameter2() instanceof StubGroup);
    IdentityModel groupParameter = (IdentityModel) StubInvocationChecker.getMethodParameter2();
    assertNull(groupParameter.getIdentityIdValue());
    assertNotNull(groupParameter.getIdentityNameValue());
  }

  @Test
  public void testGroupModifyDataType() {
    Set<AttributeDelta> attributes = new HashSet<>();
    attributes.add(
        new AttributeDeltaBuilder()
            .setName(StubGroupAttribute.GROUP_NAME.name())
            .addValueToReplace(new BigDecimal(5))
            .build());
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            getConnectorFacade()
                .updateDelta(
                    new ObjectClass("group"),
                    new Uid("1234"),
                    attributes,
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testGroupDelete() {
    getConnectorFacade()
        .delete(new ObjectClass("group"), new Uid("1234"), new OperationOptionsBuilder().build());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("group delete", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertEquals("1234", StubInvocationChecker.getMethodParameter1().toString());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testUsersGet() {
    results = new ArrayList<>();
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            getConnectorFacade()
                .search(
                    new ObjectClass("user"), null, handler, new OperationOptionsBuilder().build()));
  }

  @Test
  public void testGroupsGet() {
    results = new ArrayList<>();
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            getConnectorFacade()
                .search(
                    new ObjectClass("group"),
                    null,
                    handler,
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testConstruction() {
    StubWriteOnlyConnector connector = new StubWriteOnlyConnector();
    connector.init(new StubConfiguration());
    StubConnectorTest.executeTestConstruction(connector, "StubWriteOnlyConnector");
  }

  @Test
  public void testDummyAuthentication() {
    StubWriteOnlyConnector connector = new StubWriteOnlyConnector();

    assertEquals(
        "stubAuth",
        connector.getAuthenticator().authenticate(connector.getConnectorConfiguration()));
  }

  @Test
  public void testSchemaConnector() {
    StubWriteOnlyConnector connector = new StubWriteOnlyConnector();
    StubConnectorTest.executeTestSchema(connector, 0);
  }
}
