package com.exclamationlabs.connid.base.connector.authenticator.integration;

import com.exclamationlabs.connid.base.connector.IntegrationTest;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.junit.Assume;
import org.junit.Before;

abstract public class BaseAuthenticatorIntegrationTest implements IntegrationTest {

    protected static final Log LOG = Log.getLog(BaseAuthenticatorIntegrationTest.class);

    protected static BaseConnectorConfiguration configuration;

    @Before
    public void setup() {
        configuration = new TestConnectorConfiguration(getConfigurationName());
        try {
            configuration.setup();
            if ("Y".equalsIgnoreCase(configuration.getProperty(
                    ConnectorProperty.CONNECTOR_BASE_CONFIGURATION_ACTIVE))) {
                configuration.setValidated();
            }

        } catch (ConfigurationException ce) {
            LOG.info("Configuration not found: " + ce.getMessage());
        }
        Assume.assumeTrue(configuration.isValidated());
    }
}
