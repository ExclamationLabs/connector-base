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
import com.exclamationlabs.connid.base.connector.stub.driver.StubDriver;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import java.math.BigDecimal;
import java.util.*;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StubWriteOnlyConnectorTest {

  private BaseWriteOnlyConnector<StubConfiguration> connector;
  private StubDriver driver;
  private OperationOptions testOperationOptions;

  @BeforeEach
  public void setup() {
    connector = new StubWriteOnlyConnector();
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
    assertFalse(connector.readEnabled());
    assertFalse(connector.isReadOnly());
    assertTrue(connector.isWriteOnly());
    assertFalse(connector.isCrud());
    assertTrue(connector.createEnabled());
    assertTrue(connector.updateEnabled());
    assertTrue(connector.deleteEnabled());
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

    Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, testOperationOptions);
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(driver.isInitializeInvoked());
    assertEquals("user create", driver.getMethodInvoked());
    assertNotNull(driver.getMethodParameter1());
    assertTrue(driver.getMethodParameter1() instanceof StubUser);
    IdentityModel userParameter = (IdentityModel) driver.getMethodParameter1();
    assertNull(userParameter.getIdentityIdValue());
    assertNotNull(userParameter.getIdentityNameValue());
    assertNull(driver.getMethodParameter2());
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

    Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, testOperationOptions);
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(driver.isInitializeInvoked());
    assertEquals("user create with group ids", driver.getMethodInvoked());
    assertNotNull(driver.getMethodParameter1());
    assertTrue(driver.getMethodParameter1() instanceof StubUser);
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
        () -> connector.create(ObjectClass.ACCOUNT, attributes, testOperationOptions));
    assertTrue(driver.isInitializeInvoked());
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
        connector.updateDelta(
            ObjectClass.ACCOUNT, new Uid("1234"), attributes, testOperationOptions);
    assertNotNull(response);
    assertTrue(response.isEmpty());
    assertTrue(driver.isInitializeInvoked());
    assertEquals("user update", driver.getMethodInvoked());
    assertNotNull(driver.getMethodParameter1());
    assertTrue(driver.getMethodParameter1() instanceof String);
    assertEquals("1234", driver.getMethodParameter1().toString());

    assertNotNull(driver.getMethodParameter2());
    assertTrue(driver.getMethodParameter2() instanceof StubUser);
    IdentityModel userParameter = (IdentityModel) driver.getMethodParameter2();
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
        connector.updateDelta(
            ObjectClass.ACCOUNT, new Uid("1234"), attributes, testOperationOptions);
    assertNotNull(response);
    assertTrue(response.isEmpty());
    assertTrue(driver.isInitializeInvoked());
    assertEquals("user update with group ids", driver.getMethodInvoked());
    assertEquals("1234", driver.getMethodParameter1().toString());
    assertNotNull(driver.getMethodParameter2());
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
            connector.updateDelta(
                ObjectClass.ACCOUNT, new Uid("1234"), attributes, testOperationOptions));
  }

  @Test
  public void testUserDelete() {
    connector.delete(ObjectClass.ACCOUNT, new Uid("1234"), testOperationOptions);
    assertTrue(driver.isInitializeInvoked());
    assertEquals("user delete", driver.getMethodInvoked());
    assertEquals("1234", driver.getMethodParameter1());
    assertNull(driver.getMethodParameter2());
  }

  @Test
  public void testGroupCreate() {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(
        new AttributeBuilder()
            .setName(StubGroupAttribute.GROUP_NAME.name())
            .addValue("Avengers")
            .build());

    Uid newId = connector.create(ObjectClass.GROUP, attributes, testOperationOptions);
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(driver.isInitializeInvoked());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(driver.isInitializeInvoked());
    assertEquals("group create", driver.getMethodInvoked());
    assertNotNull(driver.getMethodParameter1());
    assertTrue(driver.getMethodParameter1() instanceof StubGroup);
    IdentityModel groupParameter = (IdentityModel) driver.getMethodParameter1();
    assertNull(groupParameter.getIdentityIdValue());
    assertNotNull(groupParameter.getIdentityNameValue());
    assertNull(driver.getMethodParameter2());
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
        () -> connector.create(ObjectClass.GROUP, attributes, testOperationOptions));
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
        connector.updateDelta(ObjectClass.GROUP, new Uid("1234"), attributes, testOperationOptions);
    assertNotNull(response);
    assertTrue(response.isEmpty());
    assertTrue(driver.isInitializeInvoked());
    assertEquals("group update", driver.getMethodInvoked());
    assertNotNull(driver.getMethodParameter1());
    assertEquals("1234", driver.getMethodParameter1().toString());

    assertNotNull(driver.getMethodParameter2());
    assertTrue(driver.getMethodParameter2() instanceof StubGroup);
    IdentityModel groupParameter = (IdentityModel) driver.getMethodParameter2();
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
            connector.updateDelta(
                ObjectClass.GROUP, new Uid("1234"), attributes, testOperationOptions));
  }

  @Test
  public void testGroupDelete() {
    connector.delete(ObjectClass.GROUP, new Uid("1234"), testOperationOptions);
    assertTrue(driver.isInitializeInvoked());
    assertEquals("group delete", driver.getMethodInvoked());
    assertNotNull(driver.getMethodParameter1());
    assertEquals("1234", driver.getMethodParameter1().toString());
    assertNull(driver.getMethodParameter2());
  }

  @Test
  public void testConstruction() {
    StubConnectorTest.executeTestConstruction(connector, "StubWriteOnlyConnector");
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
