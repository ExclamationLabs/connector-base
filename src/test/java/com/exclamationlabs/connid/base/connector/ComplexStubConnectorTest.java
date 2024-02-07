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

import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.stub.ComplexStubConnector;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubClubAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubGroupAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubSupergroupAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute;
import com.exclamationlabs.connid.base.connector.stub.configuration.ComplexStubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.model.StubClub;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubSupergroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import com.exclamationlabs.connid.base.connector.stub.util.StubInvocationChecker;
import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComplexStubConnectorTest
    extends ApiIntegrationTest<ComplexStubConfiguration, ComplexStubConnector> {

  private ComplexStubConfiguration stubConfiguration;

  @Override
  protected ComplexStubConfiguration getConfiguration() {
    return stubConfiguration == null ? new ComplexStubConfiguration() : stubConfiguration;
  }

  @Override
  protected Class<ComplexStubConnector> getConnectorClass() {
    return ComplexStubConnector.class;
  }

  @Override
  protected void readConfiguration(ComplexStubConfiguration configuration) {}

  @BeforeEach
  public void setup() {
    super.setup();
    stubConfiguration = new ComplexStubConfiguration();
    StubInvocationChecker.reset();
  }

  @Test
  public void testGetAllWithResultCap() {
    getConfiguration().setDeepGet(false);
    getConfiguration().setDeepImport(false);
    setup();
    getConnectorFacade().test();
    assertEquals("user got seven", StubInvocationChecker.getMethodInvoked());
    assertNull(StubInvocationChecker.getMethodParameter1());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testGetAllImportDeepBatch() {
    getConfiguration().setImportBatchSize(15);
    setup();
    results = new ArrayList<>();

    getConnectorFacade()
        .search(new ObjectClass("user"), null, handler, new OperationOptionsBuilder().build());
    assertEquals(95, results.size());
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));

    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user getOne", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testGetAllImportShallowBatch() {
    getConfiguration().setImportBatchSize(12);
    getConfiguration().setDeepGet(false);
    getConfiguration().setDeepImport(false);
    super.setup();
    results = new ArrayList<>();

    getConnectorFacade()
        .search(new ObjectClass("user"), null, handler, new OperationOptionsBuilder().build());
    assertEquals(101, results.size());
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
  public void testGetAllImportDeepNoBatch() {
    getConfiguration().setImportBatchSize(null);
    super.setup();
    results = new ArrayList<>();
    getConnectorFacade()
        .search(new ObjectClass("user"), null, handler, new OperationOptionsBuilder().build());
    assertEquals(100, results.size());
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user getOne", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testGetAllImportShallowNoBatch() {
    getConfiguration().setDeepGet(false);
    getConfiguration().setDeepImport(false);
    getConfiguration().setImportBatchSize(null);
    super.setup();
    results = new ArrayList<>();

    getConnectorFacade()
        .search(new ObjectClass("user"), null, handler, new OperationOptionsBuilder().build());
    assertEquals(100, results.size());
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
  public void testGetAllSinglePageDeep() {
    OperationOptions testOperationOptions =
        new OperationOptionsBuilder().setPageSize(10).setPagedResultsOffset(1).build();
    results = new ArrayList<>();

    getConnectorFacade().search(new ObjectClass("user"), null, handler, testOperationOptions);
    assertEquals(10, results.size());
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user getOne", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testGetAllSinglePageShallow() {
    getConfiguration().setDeepGet(false);
    super.setup();
    OperationOptions testOperationOptions =
        new OperationOptionsBuilder().setPageSize(8).setPagedResultsOffset(1).build();
    results = new ArrayList<>();

    getConnectorFacade().search(new ObjectClass("user"), null, handler, testOperationOptions);
    assertEquals(8, results.size());
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
  public void testUserCreateAllAttributeTypes() {
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
            .setName(StubUserAttribute.USER_TEST_BIG_DECIMAL.name())
            .addValue(BigDecimal.ONE)
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_BIG_INTEGER.name())
            .addValue(BigInteger.ONE)
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_BOOLEAN.name())
            .addValue(Boolean.TRUE)
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_BYTE.name())
            .addValue(64)
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_CHARACTER.name())
            .addValue('X')
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_DOUBLE.name())
            .addValue(12345678.12345678)
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_FLOAT.name())
            .addValue(1234.1234)
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_GUARDED_BYTE_ARRAY.name())
            .addValue(new GuardedByteArray("test".getBytes()))
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_GUARDED_STRING.name())
            .addValue(new GuardedString("test2".toCharArray()))
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_INTEGER.name())
            .addValue(7890)
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_LONG.name())
            .addValue(78907890)
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_TEST_MAP.name())
            .addValue(Collections.singletonMap("testkey", "testvalue"))
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
  public void testUserCreateHandleDuplicateErrorReturnsId() {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.USER_NAME.name())
            .addValue("duplicateId")
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.EMAIL.name())
            .addValue("duplicateId")
            .build());

    Uid existingId =
        getConnectorFacade()
            .create(new ObjectClass("user"), attributes, new OperationOptionsBuilder().build());
    assertNotNull(existingId);
    assertEquals("dupe", existingId.getUidValue());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("got existing id", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertTrue(StubInvocationChecker.getMethodParameter1() instanceof StubUser);
    IdentityModel userParameter = (IdentityModel) StubInvocationChecker.getMethodParameter1();
    assertNull(userParameter.getIdentityIdValue());
    assertNotNull(userParameter.getIdentityNameValue());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testUserCreateClub() {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(
        new AttributeBuilder()
            .setName(StubClubAttribute.CLUB_ID.name())
            .addValue("Club Ninja")
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubClubAttribute.CLUB_NAME.name())
            .addValue("club@ninja.com")
            .build());

    Uid newId =
        getConnectorFacade()
            .create(new ObjectClass("CLUB"), attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("club create", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertTrue(StubInvocationChecker.getMethodParameter1() instanceof StubClub);
    IdentityModel userParameter = (IdentityModel) StubInvocationChecker.getMethodParameter1();
    assertNull(userParameter.getIdentityIdValue());
    assertNotNull(userParameter.getIdentityNameValue());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testUserCreateSupergroup() {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(
        new AttributeBuilder()
            .setName(StubSupergroupAttribute.SUPERGROUP_ID.name())
            .addValue("Travelling Wilburys")
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubSupergroupAttribute.SUPERGROUP_NAME.name())
            .addValue("wilburys@yahoo.com")
            .build());

    Uid newId =
        getConnectorFacade()
            .create(
                new ObjectClass("SUPERGROUP"), attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("supergroup create", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertTrue(StubInvocationChecker.getMethodParameter1() instanceof StubSupergroup);
    IdentityModel userParameter = (IdentityModel) StubInvocationChecker.getMethodParameter1();
    assertNull(userParameter.getIdentityIdValue());
    assertNotNull(userParameter.getIdentityNameValue());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testUserCreateWithGroupsAndClubs() {
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
    attributes.add(
        new AttributeBuilder()
            .setName(StubUserAttribute.CLUB_IDS.name())
            .addValue(Arrays.asList("club1", "club2"))
            .build());

    Uid newId =
        getConnectorFacade()
            .create(new ObjectClass("user"), attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("user create with group and club ids", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertTrue(StubInvocationChecker.getMethodParameter1() instanceof StubUser);
    IdentityModel userParameter = (IdentityModel) StubInvocationChecker.getMethodParameter1();
    assertNull(userParameter.getIdentityIdValue());
    assertNotNull(userParameter.getIdentityNameValue());
    assertNull(StubInvocationChecker.getMethodParameter2());
  }

  @Test
  public void testGroupCreateWithSupergroups() {
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(
        new AttributeBuilder()
            .setName(StubGroupAttribute.GROUP_NAME.name())
            .addValue("Dummy")
            .build());
    attributes.add(
        new AttributeBuilder()
            .setName(StubGroupAttribute.SUPERGROUP_IDS.name())
            .addValue(Arrays.asList("id1", "id2"))
            .build());

    Uid newId =
        getConnectorFacade()
            .create(new ObjectClass("group"), attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertNotNull(newId.getUidValue());
    assertTrue(StubInvocationChecker.isInitializeInvoked());
    assertEquals("group create with supergroup ids", StubInvocationChecker.getMethodInvoked());
    assertNotNull(StubInvocationChecker.getMethodParameter1());
    assertTrue(StubInvocationChecker.getMethodParameter1() instanceof StubGroup);
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
    IdentityModel userParameter = (IdentityModel) StubInvocationChecker.getMethodParameter1();
    assertNull(userParameter.getIdentityIdValue());
    assertNotNull(userParameter.getIdentityNameValue());
    assertNull(StubInvocationChecker.getMethodParameter2());
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
  public void testConstruction() {
    ComplexStubConnector connector = new ComplexStubConnector();
    connector.init(new ComplexStubConfiguration());
    ObjectClass accountObjectClass = new ObjectClass("user");
    ObjectClass groupObjectClass = new ObjectClass("group");
    assertNotNull(connector.getConnectorFilterTranslator(accountObjectClass));
    assertTrue(
        connector.getConnectorFilterTranslator(accountObjectClass)
            instanceof DefaultFilterTranslator);

    assertNotNull(connector.getAdapter(groupObjectClass).getConnectorAttributes());
    assertNotNull(connector.getAdapter(accountObjectClass).getConnectorAttributes());

    assertNotNull(connector.getConnectorSchemaBuilder());
    assertNotNull(connector.getAuthenticator());
    assertNotNull(connector.getConnectorConfiguration());

    assertEquals("ComplexStubConnector", connector.getName());
  }

  @Test
  public void testAuthentication() {
    ComplexStubConnector connector = new ComplexStubConnector();
    assertEquals(
        "ying-yang",
        connector.getAuthenticator().authenticate(connector.getConnectorConfiguration()));
  }
}
