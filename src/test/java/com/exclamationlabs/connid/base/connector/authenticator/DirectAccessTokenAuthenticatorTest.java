package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.junit.Test;

import java.util.Properties;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DirectAccessTokenAuthenticatorTest {

    protected static BaseConnectorConfiguration configuration;

    static {
        Properties testProperties = new Properties();
        testProperties.put(CONNECTOR_BASE_AUTH_DIRECT_TOKEN.name(), "cooltoken");
        configuration = new TestConnectorConfiguration(testProperties);
    }

    @Test
    public void test() {
        Authenticator authenticator = new DirectAccessTokenAuthenticator();
        String response = authenticator.authenticate(configuration);
        assertNotNull(response);
        assertEquals("cooltoken", response);
    }
}
