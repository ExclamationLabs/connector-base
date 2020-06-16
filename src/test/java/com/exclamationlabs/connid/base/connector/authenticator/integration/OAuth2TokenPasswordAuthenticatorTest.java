package com.exclamationlabs.connid.base.connector.authenticator.integration;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenPasswordAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationConnector;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationNameBuilder;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Integration test for both the OAuth2TokenPasswordAuthenticator,
 * using Dev ELabs GotoMeeting
 */
public class OAuth2TokenPasswordAuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(ConfigurationConnector.GOTOMEETING).build();
    }

    @Test
    public void test() {
        Authenticator authenticator = new OAuth2TokenPasswordAuthenticator();
        String response = authenticator.authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().getAccessToken());
        assertNotNull(configuration.getOauth2Information().getTokenType());
    }
}
