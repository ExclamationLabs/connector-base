package com.exclamationlabs.connid.base.edition.neo;

import static org.junit.jupiter.api.Assertions.*;

import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.happyfa.HappyConnector;
import com.exclamationlabs.connid.base.edition.neo.util.ConnectivityTester;
import com.exclamationlabs.connid.base.edition.neo.util.TestPoint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeDelta;
import org.identityconnectors.framework.common.objects.AttributeDeltaBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HappyFAConnectorTest extends ApiIntegrationTest<StubConfiguration, HappyConnector> {

  @Override
  protected StubConfiguration getConfiguration() {
    return new StubConfiguration();
  }

  @Override
  protected Class<HappyConnector> getConnectorClass() {
    return HappyConnector.class;
  }

  @Override
  protected void readConfiguration(StubConfiguration stubConfiguration) {}

  @BeforeEach
  public void setup() {
    super.setup();
    ConnectivityTester.reset();
  }

  @Test
  void connectorTest() {
    getConnectorFacade().test();
    assertEquals("HappyDriver:test", ConnectivityTester.getPoint(TestPoint.DRIVER_TEST));
  }

  @Test
  void schema() {
    var schema = getConnectorFacade().schema();
    assertNotNull(schema);
    var infos = schema.getObjectClassInfo();
    assertNotNull(infos);
    var userInfoLookup =
        infos.stream()
            .filter(it -> StringUtils.equalsIgnoreCase("HappyFAUser", it.getType()))
            .findFirst();
    assertTrue(userInfoLookup.isPresent());
    var userInfo = userInfoLookup.get();
    assertNotNull(userInfo.getAttributeInfo());
    assertEquals(13, userInfo.getAttributeInfo().size());

    var groupInfoLookup =
        infos.stream()
            .filter(it -> StringUtils.equalsIgnoreCase("HappyFAGroup", it.getType()))
            .findFirst();
    assertTrue(groupInfoLookup.isPresent());
    var groupInfo = groupInfoLookup.get();
    assertNotNull(groupInfo.getAttributeInfo());
    assertEquals(3, groupInfo.getAttributeInfo().size());
  }

  @Test
  void testUserGet() {
    results = new ArrayList<>();
    Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("1234").build();

    getConnectorFacade()
        .search(
            new ObjectClass("HappyFAUser"),
            new EqualsFilter(idAttribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(1, results.size());
    assertEquals("1234", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString());
    assertEquals(
        "happyuser", results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString());
    assertEquals(
        "Happy", results.get(0).getAttributeByName("firstName").getValue().get(0).toString());
    assertEquals(
        "User", results.get(0).getAttributeByName("lastName").getValue().get(0).toString());
    assertEquals(true, results.get(0).getAttributeByName("IS_ACTIVE").getValue().get(0));
    assertEquals(3, results.get(0).getAttributeByName("USER_YEARS").getValue().get(0));
    assertNull(results.get(0).getAttributeByName("email").getValue());
    assertEquals(
        "123 Happy St", results.get(0).getAttributeByName("ADDRESS_STREET").getValue().get(0));
    assertEquals("Happyville", results.get(0).getAttributeByName("ADDRESS_CITY").getValue().get(0));
    assertEquals("CA", results.get(0).getAttributeByName("ADDRESS_STATE").getValue().get(0));
    assertEquals("12345", results.get(0).getAttributeByName("ADDRESS_ZIP").getValue().get(0));
    assertEquals(2, results.get(0).getAttributeByName("GROUP_IDS").getValue().size());
  }

  @Test
  void testUserGetNoMatch() {
    results = new ArrayList<>();
    Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("5678").build();

    getConnectorFacade()
        .search(
            new ObjectClass("HappyFAUser"),
            new EqualsFilter(idAttribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(0, results.size());
  }

  @Test
  void testUserGetAll() {
    results = new ArrayList<>();
    getConnectorFacade()
        .search(
            new ObjectClass("HappyFAUser"), null, handler, new OperationOptionsBuilder().build());
    assertEquals(3, results.size());
  }

  @Test
  void testUserCreate() {
    ObjectClass oClass = new ObjectClass("HappyFAUser");
    Set<Attribute> attributes = new HashSet<>();
    attributes.add(new AttributeBuilder().setName("USER_NAME").addValue("JohnSmith123").build());
    attributes.add(new AttributeBuilder().setName("firstName").addValue("John").build());
    attributes.add(new AttributeBuilder().setName("lastName").addValue("Smith").build());
    attributes.add(new AttributeBuilder().setName("email").addValue("johnsmith@test.com").build());
    attributes.add(new AttributeBuilder().setName("IS_ACTIVE").addValue(true).build());
    attributes.add(
        new AttributeBuilder().setName("ADDRESS_STREET").addValue("101 Main St").build());
    attributes.add(new AttributeBuilder().setName("ADDRESS_CITY").addValue("Omaha").build());
    attributes.add(new AttributeBuilder().setName("ADDRESS_STATE").addValue("NE").build());
    attributes.add(new AttributeBuilder().setName("ADDRESS_ZIP").addValue("68104").build());
    attributes.add(new AttributeBuilder().setName("ADDRESS_TYPE").addValue("Home").build());
    attributes.add(
        new AttributeBuilder()
            .setName("GROUP_IDS")
            .addValue(List.of("1001", "1002", "1003", "1004", "1005"))
            .build());
    Uid newId =
        getConnectorFacade().create(oClass, attributes, new OperationOptionsBuilder().build());
    assertNotNull(newId);
    assertEquals("123456", newId.getUidValue());
  }

  @Test
  void testUserUpdate() {
    // modify the existing user
    ObjectClass oClass = new ObjectClass("HappyFAUser");
    Set<AttributeDelta> attributes = new HashSet<>();
    attributes.add(
        new AttributeDeltaBuilder()
            .setName("email")
            .addValueToReplace("johnsmith@yahoo.com")
            .build());
    attributes.add(
        new AttributeDeltaBuilder().setName("ADDRESS_TYPE").addValueToReplace("Temporary").build());
    attributes.add(
        new AttributeDeltaBuilder()
            .setName("GROUP_IDS")
            .addValueToRemove(List.of("1002", "1003"))
            .build());
    attributes.add(
        new AttributeDeltaBuilder()
            .setName("GROUP_IDS")
            .addValueToAdd(List.of("1006", "1007"))
            .build());
    Set<AttributeDelta> response =
        getConnectorFacade()
            .updateDelta(
                oClass, new Uid("123456"), attributes, new OperationOptionsBuilder().build());
    assertNotNull(response);
    assertFalse(response.isEmpty());
  }

  @Test
  void testUserDelete() {
    ObjectClass oClass = new ObjectClass("HappyFAUser");
    getConnectorFacade().delete(oClass, new Uid("123456"), new OperationOptionsBuilder().build());
  }
}
