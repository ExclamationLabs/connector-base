package com.exclamationlabs.connid.base.edition.neo;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.happy.HappyConnector;
import com.exclamationlabs.connid.base.edition.neo.util.ConnectivityTester;
import com.exclamationlabs.connid.base.edition.neo.util.TestPoint;
import org.apache.commons.lang3.StringUtils;
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
  public void schema() {
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
    //        var constrainedLookup =
    //                userInfo.getAttributeInfo().stream()
    //                        .filter(
    //                                it ->
    //                                        StringUtils.equalsIgnoreCase(
    //
    // StubUserAttribute.USER_TEST_MAX_CONSTRAINT.name(), it.getName()))
    //                        .findFirst();
    //        assertTrue(constrainedLookup.isPresent());
    //        assertTrue(
    //                StringUtils.equalsIgnoreCase(
    //
    // "{\"constraints\":[{\"outbound\":true,\"inbound\":false,\"rule\":\"MAX_LENGTH\",\"ruleData\":\"12\"}]}",
    //                        constrainedLookup.get().getSubtype()));
  }
}
