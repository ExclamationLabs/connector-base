package com.exclamationlabs.connid.base.connector.authenticator.integration;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.JWTHS256Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.*;
import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Test for JWTHS256Authenticator, using Dev ELabs Zoom configuration
 */
public class JWTHS256AuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(ConfigurationConnector.ZOOM).build();
    }

    @Test
    public void test() {
        Authenticator jwtAuthenticator = new JWTHS256Authenticator();
        String response = jwtAuthenticator.authenticate(configuration);
        assertNotNull(response);
    }

}
