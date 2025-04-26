package com.exclamationlabs.connid.base.edition.neo;

import static org.junit.jupiter.api.Assertions.*;

import com.exclamationlabs.connid.base.connector.test.util.ConnectorTestUtils;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.RestTestConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.rest.RestConnector;
import com.exclamationlabs.connid.base.edition.neo.util.ConnectivityTester;
import com.exclamationlabs.connid.base.edition.neo.util.ConnectorMockNativeRestTest;
import com.exclamationlabs.connid.base.edition.neo.util.TestPoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeDelta;
import org.identityconnectors.framework.common.objects.AttributeDeltaBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RestConnectorTest extends ConnectorMockNativeRestTest {

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

  private static final String TEST_RESPONSE = "{customTestResponse:\"Test was good\"}";

  private static final String SINGLE_USER_RESPONSE =
      "{userId:\""
          + USER_ID
          + "\", "
          + " userName:\""
          + USER_NAME
          + "\", "
          + " email:\""
          + USER_EMAIL
          + "\", "
          + " firstName:\"Bob\", "
          + " lastName:\"Test\""
          + "}";
  private static final String SECOND_USER_RESPONSE =
      "{userId:\""
          + USER_ID2
          + "\", "
          + " userName:\""
          + USER_NAME2
          + "\", "
          + " email:\""
          + USER_EMAIL2
          + "\", "
          + " firstName:\"Bob2\", "
          + " lastName:\"Test2\""
          + "}";
  private static final String MULTI_USER_RESPONSE =
      "{people: [ " + SINGLE_USER_RESPONSE + "," + SECOND_USER_RESPONSE + " ]}";

  private static final String CREATE_USER_RESPONSE = SINGLE_USER_RESPONSE;

  private RestConnector restConnector;

  @BeforeEach
  public void setup() {
    RestTestConfiguration configuration = new RestTestConfiguration();
    restConnector = new RestConnector();
    restConnector.init(configuration);
    ConnectivityTester.reset();
  }

  @Test
  void test() {
    prepareMockResponse(Collections.emptyMap(), TEST_RESPONSE);
    ConnectivityTester.setMockClient(stubClient);
    restConnector.test();
    assertEquals("Test was good", ConnectivityTester.getPoint(TestPoint.DRIVER_TEST));
  }

  @Test
  void getOneUserObject() {
    ConnectivityTester.setMockClient(stubClient);
    prepareMockResponse(Collections.emptyMap(), SINGLE_USER_RESPONSE);
    ObjectClass oClass = new ObjectClass("RestUser");
    ConnectorObject response =
        restConnector.getObject(oClass, new Uid("1234"), new OperationOptionsBuilder().build());
    assertNotNull(response);
    assertEquals("123", response.getUid().getUidValue());
    assertEquals("Bob", response.getAttributeByName("firstName").getValue().get(0));
  }

  @Test
  void getOneUser() {
    Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("1234").build();
    ConnectivityTester.setMockClient(stubClient);
    prepareMockResponse(Collections.emptyMap(), SINGLE_USER_RESPONSE);
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);
    ObjectClass oClass = new ObjectClass("RestUser");
    restConnector.executeQuery(
        oClass,
        new EqualsFilter(idAttribute),
        resultsHandler,
        new OperationOptionsBuilder().build());
    assertEquals(1, idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
  }

  @Test
  void getAllUsers() {
    ConnectivityTester.setMockClient(stubClient);
    prepareMockResponse(Collections.emptyMap(), MULTI_USER_RESPONSE);
    List<String> idValues = new ArrayList<>();
    List<String> nameValues = new ArrayList<>();
    ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);
    ObjectClass oClass = new ObjectClass("RestUser");
    restConnector.executeQuery(oClass, null, resultsHandler, new OperationOptionsBuilder().build());
    assertEquals(2, idValues.size());
    assertTrue(StringUtils.isNotBlank(idValues.get(0)));
  }

  @Test
  void createUser() {
    ConnectivityTester.setMockClient(stubClient);
    prepareMockResponse(Collections.emptyMap(), CREATE_USER_RESPONSE);

    Set<Attribute> attributes = new HashSet<>();
    attributes.add(new AttributeBuilder().setName("email").addValue("someone@tester.com").build());
    attributes.add(new AttributeBuilder().setName("USER_NAME").addValue("SOMEONE").build());
    attributes.add(new AttributeBuilder().setName("firstName").addValue("Some").build());
    attributes.add(new AttributeBuilder().setName("lastName").addValue("One").build());
    ObjectClass oClass = new ObjectClass("RestUser");
    Uid newId = restConnector.create(oClass, attributes, new OperationOptionsBuilder().build());
    assertEquals("123", newId.getUidValue());
  }

  @Test
  void modifyUser() {
    ConnectivityTester.setMockClient(stubClient);
    prepareMockResponse(Collections.emptyMap(), CREATE_USER_RESPONSE);

    Set<AttributeDelta> attributes = new HashSet<>();
    attributes.add(
        new AttributeDeltaBuilder()
            .setName("email")
            .addValueToReplace("someone2@tester.com")
            .build());
    attributes.add(
        new AttributeDeltaBuilder().setName("firstName").addValueToReplace("Some2").build());
    attributes.add(
        new AttributeDeltaBuilder().setName("lastName").addValueToReplace("One2").build());
    ObjectClass oClass = new ObjectClass("RestUser");
    Set<AttributeDelta> response =
        restConnector.updateDelta(
            oClass, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
    assertNotNull(response);
    assertTrue(response.isEmpty());
  }

  @Test
  void deleteUser() {
    ConnectivityTester.setMockClient(stubClient);
    prepareMockResponse(Collections.emptyMap(), "");
    ObjectClass oClass = new ObjectClass("RestUser");
    restConnector.delete(oClass, new Uid("1234"), new OperationOptionsBuilder().build());
  }
}
