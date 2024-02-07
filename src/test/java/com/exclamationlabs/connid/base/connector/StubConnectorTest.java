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

import com.exclamationlabs.connid.base.connector.configuration.basetypes.ResultsConfiguration;
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.stub.StubConnector;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubGroupAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import com.exclamationlabs.connid.base.connector.stub.util.StubInvocationChecker;
import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import java.math.BigDecimal;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StubConnectorTest extends ApiIntegrationTest<StubConfiguration, StubConnector> {

  @Override
  protected StubConfiguration getConfiguration() {
    return new StubConfiguration();
  }

  @Override
  protected Class<StubConnector> getConnectorClass() {
    return StubConnector.class;
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
    assertEquals("test", StubInvocationChecker.getMethodInvoked());
  }

  @Test
  public void testSchema() {
    assertNotNull(getConnectorFacade().schema());
  }

  @Test
  public void testIdentityToString() {
    StubUser test = new StubUser();
    test.setId("id");
    test.setUserName("name");
    assertEquals("id;name", test.toString());
  }

  @Test
  public void testAccessMethods() {
    StubConnector connector = new StubConnector();
    assertTrue(connector.isCrud());
    assertTrue(connector.readEnabled());
    assertTrue(connector.createEnabled());
    assertTrue(connector.updateEnabled());
    assertTrue(connector.deleteEnabled());

    assertFalse(connector.isReadOnly());
    assertFalse(connector.isWriteOnly());
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
    assertTrue(StubInvocationChecker.isInitializeInvoked());
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
  public void testUsersGetFilteredUid() {
    results = new ArrayList<>();
    Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("filteredId").build();

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

    assertEquals(
        "filteredName", results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user getOne", StubInvocationChecker.getMethodInvoked());
    assertEquals("filteredId", StubInvocationChecker.getMethodParameter1().toString());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testUserGetObject() {
    ConnectorObject response =
        getConnectorFacade()
            .getObject(
                new ObjectClass("user"), new Uid("1234"), new OperationOptionsBuilder().build());
    assertNotNull(response);
    assertTrue(
        StringUtils.isNotBlank(response.getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            response.getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user getOne", StubInvocationChecker.getMethodInvoked());
    assertEquals("1234", StubInvocationChecker.getMethodParameter1().toString());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testUserGet() {
    results = new ArrayList<>();
    Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("1234").build();

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
    results = new ArrayList<>();
    Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("1234").build();

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
  public void testConstruction() {

    StubConnector connector = new StubConnector();
    connector.init(new StubConfiguration());
    executeTestConstruction(connector, "StubConnector");
  }

  @Test
  public void testDummyAuthentication() {
    StubConnector connector = new StubConnector();
    assertEquals(
        "stubAuth",
        connector.getAuthenticator().authenticate(connector.getConnectorConfiguration()));
  }

  @Test
  public void testSchemaConnector() {
    StubConnector connector = new StubConnector();
    executeTestSchema(connector, 0);
  }

  @Test
  public void testSchemaWithPagination() {
    StubConnector connector = new StubConnector();
    StubConfiguration configuration = new StubPagingConfiguration();
    connector.init(configuration);
    executeTestSchema(connector, 7);
  }

  static void executeTestConstruction(
      final BaseConnector<?> testConnector, final String expectedConnectorName) {
    ObjectClass userObjectClass = new ObjectClass("user");
    ObjectClass groupObjectClass = new ObjectClass("group");
    assertNotNull(testConnector.getConnectorFilterTranslator(userObjectClass));
    assertTrue(
        testConnector.getConnectorFilterTranslator(userObjectClass)
            instanceof DefaultFilterTranslator);

    assertNotNull(testConnector.getAdapter(userObjectClass).getConnectorAttributes());
    assertNotNull(testConnector.getAdapter(groupObjectClass).getConnectorAttributes());

    assertNotNull(testConnector.getConnectorSchemaBuilder());
    assertNotNull(testConnector.getAuthenticator());
    assertNotNull(testConnector.getConnectorConfiguration());

    assertEquals(expectedConnectorName, testConnector.getName());
  }

  static void executeTestSchema(
      final BaseConnector<?> testConnector, int expectedOperationOptions) {
    Schema schemaResult = testConnector.schema();
    assertNotNull(schemaResult);

    ObjectClassInfo userSchema = schemaResult.findObjectClassInfo("user");
    assertNotNull(userSchema);
    assertNotNull(userSchema.getAttributeInfo());
    assertEquals(19, userSchema.getAttributeInfo().size());

    ObjectClassInfo groupSchema = schemaResult.findObjectClassInfo("group");
    assertNotNull(groupSchema);
    assertNotNull(groupSchema.getAttributeInfo());
    assertEquals(3, groupSchema.getAttributeInfo().size());

    Set<OperationOptionInfo> info = schemaResult.getOperationOptionInfo();
    assertNotNull(info);
    assertEquals(expectedOperationOptions, info.size());
  }

  static class StubPagingConfiguration extends StubConfiguration implements ResultsConfiguration {

    @Override
    public Boolean getDeepGet() {
      return null;
    }

    @Override
    public void setDeepGet(Boolean input) {}

    @Override
    public Boolean getDeepImport() {
      return null;
    }

    @Override
    public void setDeepImport(Boolean input) {}

    @Override
    public Integer getImportBatchSize() {
      return null;
    }

    @Override
    public void setImportBatchSize(Integer input) {}

    @Override
    public Boolean getPagination() {
      return true;
    }

    @Override
    public void setPagination(Boolean input) {}
  }
}
