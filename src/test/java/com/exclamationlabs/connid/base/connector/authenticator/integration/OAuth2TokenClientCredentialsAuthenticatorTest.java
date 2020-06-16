package com.exclamationlabs.connid.base.connector.authenticator.integration;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenClientCredentialsAuthenticator;
import com.exclamationlabs.connid.base.connector.authenticator.client.HttpsKeystoreCertificateClientLoader;
import com.exclamationlabs.connid.base.connector.authenticator.client.SecureClientLoader;
import com.exclamationlabs.connid.base.connector.authenticator.keys.KeyStoreLoader;
import com.exclamationlabs.connid.base.connector.authenticator.keys.PFXKeyStoreLoader;
import com.exclamationlabs.connid.base.connector.configuration.*;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Test for OAuth2TokenClientCredentialsAuthenticator, using Dev 1U FIS configuration
 */
public class OAuth2TokenClientCredentialsAuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().
                withConnector(ConfigurationConnector.FIS).
                withOwner(ConfigurationOwner.FIRST_UNITED).build();
    }

    // TODO: Using ExclamationLabs FIS dev account info to test.  Figure out a strategy
    // to tuck this away

    protected Authenticator oauth2Authenticator;

    @Before
    public void setup() {
        super.setup();
        KeyStoreLoader keyStoreLoader = new PFXKeyStoreLoader();
        SecureClientLoader clientLoader = new HttpsKeystoreCertificateClientLoader();

        oauth2Authenticator = new OAuth2TokenClientCredentialsAuthenticator() {
            @Override
            protected HttpClient getHttpClient() {
                return clientLoader.load(configuration,
                        keyStoreLoader.load(configuration));
            }
        };
    }

    @Test
    public void test() {
        String response = oauth2Authenticator.authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().getAccessToken());
        assertNotNull(configuration.getOauth2Information().getTokenType());
    }
}
