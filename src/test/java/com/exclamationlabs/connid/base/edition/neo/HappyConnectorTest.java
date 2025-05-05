package com.exclamationlabs.connid.base.edition.neo;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.happy.HappyConnector;
import com.exclamationlabs.connid.base.edition.neo.util.ConnectivityTester;
import com.exclamationlabs.connid.base.edition.neo.util.TestPoint;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HappyConnectorTest extends ApiIntegrationTest<StubConfiguration, HappyConnector> {

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
            .filter(it -> StringUtils.equalsIgnoreCase("HappyUser", it.getType()))
            .findFirst();
    assertTrue(userInfoLookup.isPresent());
    var userInfo = userInfoLookup.get();
    assertNotNull(userInfo.getAttributeInfo());
    assertEquals(12, userInfo.getAttributeInfo().size());

    var groupInfoLookup =
        infos.stream()
            .filter(it -> StringUtils.equalsIgnoreCase("HappyGroup", it.getType()))
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
            new ObjectClass("HappyUser"),
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
  }

  @Test
  void testUserGetNoMatch() {
    results = new ArrayList<>();
    Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("5678").build();

    getConnectorFacade()
        .search(
            new ObjectClass("HappyUser"),
            new EqualsFilter(idAttribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(0, results.size());
  }

  @Test
  void testUserGetAllFilterWorking() {
    results = new ArrayList<>();
    var filter =
        new EqualsFilter(new AttributeBuilder().setName("firstName").addValue("Grumpy").build());
    getConnectorFacade()
        .search(
            new ObjectClass("HappyUser"), filter, handler, new OperationOptionsBuilder().build());
    assertEquals(1, results.size());
  }

  @Test
  void testUserGetAllPaginationWorking() {
    results = new ArrayList<>();
    getConnectorFacade()
        .search(
            new ObjectClass("HappyUser"),
            null,
            handler,
            new OperationOptionsBuilder().setPageSize(2).setPagedResultsOffset(1).build());
    assertEquals(2, results.size());
  }

  @Test
  void testCreateFailsForNonFullAccess() {
    ConnectorException exception =
        assertThrows(
            ConnectorException.class,
            () ->
                getConnectorFacade()
                    .create(
                        new ObjectClass("HappyUser"),
                        Collections.emptySet(),
                        new OperationOptionsBuilder().build()));
    assertTrue(
        exception
            .getMessage()
            .startsWith(
                "Invocator HappyUserInvocator does not support full access for Create to HappyUserModel"));
  }

  @Test
  void testUpdateFailsForNonFullAccess() {
    ConnectorException exception =
        assertThrows(
            ConnectorException.class,
            () ->
                getConnectorFacade()
                    .updateDelta(
                        new ObjectClass("HappyUser"),
                        new Uid("1234"),
                        Collections.emptySet(),
                        new OperationOptionsBuilder().build()));
    assertTrue(
        exception
            .getMessage()
            .startsWith(
                "Invocator HappyUserInvocator does not support full access for Update to HappyUserModel"));
  }

  @Test
  void testDeleteFailsForNonFullAccess() {
    ConnectorException exception =
        assertThrows(
            ConnectorException.class,
            () ->
                getConnectorFacade()
                    .delete(
                        new ObjectClass("HappyUser"),
                        new Uid("1234"),
                        new OperationOptionsBuilder().build()));
    assertTrue(
        exception
            .getMessage()
            .startsWith(
                "Invocator HappyUserInvocator does not support full access for Delete to HappyUserModel"));
  }
}
