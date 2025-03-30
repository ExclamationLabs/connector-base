package com.exclamationlabs.connid.base.edition.neo;

import static org.junit.jupiter.api.Assertions.*;

import com.exclamationlabs.connid.base.edition.neo.stub.configuration.OtherConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.happy.HappyConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.sad.authenticator.multi.MultiAuthenticatorConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.sad.authenticator.wrongconfig.AuthWrongConfigConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.sad.driver.multi.MultiDriverConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.sad.driver.none.NoDriverConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.sad.driver.wrongconfig.WrongConfigConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.sad.model.nomodels.NoModelsConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.sad.model.ocmissing.NoOcModelsConnector;
import com.exclamationlabs.connid.base.edition.neo.util.ConnectivityTester;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Also covers conditions in BaseConnectorTypeFactory. */
public class BaseConnectorInitializationTest {

  @BeforeEach
  void setUp() {
    ConnectivityTester.reset();
  }

  @Test
  void plain() {
    var connector = new HappyConnector();

    // Initialize and verify construction of components
    connector.init(new StubConfiguration());
    var expectedJson =
        "{\"Driver\":\"HappyDriver\",\"Authenticator\":\"HappyAuthenticator\",\"Models\":[\"HappyUserModel\",\"HappyGroupModel\"],\"Invocators\":[\"HappyUserModel\",\"HappyGroupModel\"],\"DriverFaultProcessor\":\"HappyFaultProcessor\",\"InvocatorFaultProcessors\":[\"HappyUserFaultProcessor\"]}";
    assertEquals(expectedJson, connector.getConstruction());
  }

  @Test
  void rest() {}

  @Test
  void soap() {}

  @Test
  void wrongConfigSuppliedToInit() {
    var connector = new NoDriverConnector();
    ConfigurationException exception =
        assertThrows(ConfigurationException.class, () -> connector.init(new OtherConfiguration()));
    assertTrue(exception.getMessage().startsWith("Invalid configuration object supplied"));
  }

  @Test
  void noDriver() {
    var connector = new NoDriverConnector();
    ConfigurationException exception =
        assertThrows(ConfigurationException.class, () -> connector.init(new StubConfiguration()));
    assertTrue(exception.getMessage().startsWith("Driver not found"));
  }

  @Test
  void multipleDrivers() {
    var connector = new MultiDriverConnector();
    ConfigurationException exception =
        assertThrows(ConfigurationException.class, () -> connector.init(new StubConfiguration()));
    assertTrue(exception.getMessage().startsWith("Multiple drivers found"));
  }

  @Test
  void wrongConfigOnDriver() {
    var connector = new WrongConfigConnector();
    ConfigurationException exception =
        assertThrows(ConfigurationException.class, () -> connector.init(new StubConfiguration()));
    assertTrue(
        exception
            .getMessage()
            .startsWith("Driver WrongConfigDriver does not have expected configuration"));
  }

  @Test
  void noModels() {
    var connector = new NoModelsConnector();
    ConfigurationException exception =
        assertThrows(ConfigurationException.class, () -> connector.init(new StubConfiguration()));
    assertTrue(
        exception.getMessage().startsWith("No models found for connector NoModelsConnector"));
  }

  @Test
  void noObjectClassForModel() {
    var connector = new NoOcModelsConnector();
    ConfigurationException exception =
        assertThrows(ConfigurationException.class, () -> connector.init(new StubConfiguration()));
    assertTrue(
        exception
            .getMessage()
            .startsWith("Model class NoOcTestModel does not have a ModelObjectClass annotation"));
  }

  @Test
  void multipleAuthenticator() {
    var connector = new MultiAuthenticatorConnector();
    ConfigurationException exception =
        assertThrows(ConfigurationException.class, () -> connector.init(new StubConfiguration()));
    assertTrue(exception.getMessage().startsWith("Multiple authenticators found"));
  }

  @Test
  void wrongConfigOnAuthenticator() {
    var connector = new AuthWrongConfigConnector();
    ConfigurationException exception =
        assertThrows(ConfigurationException.class, () -> connector.init(new StubConfiguration()));
    assertTrue(
        exception
            .getMessage()
            .startsWith(
                "Authenticator class AuthWrongConfigAuthenticator does not have expected configuration"));
  }
}
