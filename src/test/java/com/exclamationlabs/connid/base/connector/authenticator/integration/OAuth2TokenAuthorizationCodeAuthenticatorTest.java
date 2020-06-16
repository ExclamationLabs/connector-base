package com.exclamationlabs.connid.base.connector.authenticator.integration;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenAuthorizationCodeAuthenticator;

import com.exclamationlabs.connid.base.connector.configuration.ConfigurationConnector;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationNameBuilder;import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;

/**
 * Test for OAuth2TokenAuthorizationCodeAuthenticator, using Dev ELabs Webex configuration
 */
public class OAuth2TokenAuthorizationCodeAuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(ConfigurationConnector.WEBEX).build();
    }

    @Test
    @Ignore
    public void test() {
        Authenticator authenticator = new OAuth2TokenAuthorizationCodeAuthenticator();
        String response = authenticator.authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().getAccessToken());
        assertNotNull(configuration.getOauth2Information().getRefreshToken());
        assertNotNull(configuration.getOauth2Information().getExpiresIn());
    }

}
