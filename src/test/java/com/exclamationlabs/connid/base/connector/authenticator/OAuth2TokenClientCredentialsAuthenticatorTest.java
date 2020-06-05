package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.authenticator.client.HttpsKeystoreCertificateClientLoader;
import com.exclamationlabs.connid.base.connector.authenticator.client.SecureClientLoader;
import com.exclamationlabs.connid.base.connector.authenticator.keys.KeyStoreLoader;
import com.exclamationlabs.connid.base.connector.authenticator.keys.PFXKeyStoreLoader;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;
import static org.junit.Assert.assertNotNull;

public class OAuth2TokenClientCredentialsAuthenticatorTest {

    // TODO: Using ExclamationLabs FIS dev account info to test.  Figure out a strategy
    // to tuck this away

    private static final BaseConnectorConfiguration configuration;

    private Authenticator oauth2Authenticator;

    static {
        Properties testProperties = new Properties();
        testProperties.put(CONNECTOR_BASE_AUTH_PFX_FILE.name(),
                "/Users/mneugebauer/Documents/fis/ibsopenapi-api-gw1-prod1.fisglobal.com.pfx");
        testProperties.put(CONNECTOR_BASE_AUTH_PFX_PASSWORD.name(), "N6uEaqcrYeTG7Pqp");

        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_ID.name(),
                "o_n8oXF7znZWWDvTNcTWHQNsx1ga");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_SECRET.name(),
                "UbfmzTOwBCtS2EAgQv_whWPhvI8a");

        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL.name(),
                "https://api-gw1-prod1.fisglobal.com/token");
        configuration = new TestConnectorConfiguration(testProperties);
    }

    @Before
    public void setup() {
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
