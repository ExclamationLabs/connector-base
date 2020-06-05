package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.junit.Test;

import java.util.Properties;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;
import static org.junit.Assert.assertNotNull;

/**
 * Test for JWTHS256Authenticator
 */
public class JWTHS256AuthenticatorTest {
    // TODO: Using Mike's development account for Zoom.  Find a way to tuck this away
    private static final BaseConnectorConfiguration configuration;

    static {
        Properties testProperties = new Properties();

        testProperties.put(CONNECTOR_BASE_AUTH_JWT_ISSUER.name(), "_FjgBa_uQv2jCp-zJFzXLg");
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_EXPIRATION_PERIOD.name(), "30000");
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_SECRET.name(), "gCL6YJwm7C5CT91bjHZu6gTEOlfLECaLQ3eF");

        configuration = new TestConnectorConfiguration(testProperties);
    }

    @Test
    public void test() {
        Authenticator jwtAuthenticator = new JWTHS256Authenticator();
        String response = jwtAuthenticator.authenticate(configuration);
        assertNotNull(response);
    }
}
