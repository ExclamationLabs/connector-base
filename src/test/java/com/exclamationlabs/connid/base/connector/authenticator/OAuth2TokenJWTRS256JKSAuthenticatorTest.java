package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.authenticator.keys.JKSRSAPrivateKeyLoader;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.security.interfaces.RSAPrivateKey;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

/**
 * Integration test for both the OAuth2JWTAuthenticator,
 * JWTRS256Authenticator and JKSRSAPrivateKeyLoader working together.
 */
public class OAuth2TokenJWTRS256JKSAuthenticatorTest {

    // TODO: Using Mike's salesforce dev account info to test.  Figure out a strategy
    // to tuck this away

    protected static BaseConnectorConfiguration configuration;

    protected Authenticator oauth2Authenticator;

    static {
        Properties testProperties = new Properties();
        // Salesforce Client Id
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_USE_ISSUED_AT.name(), "N");
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_ISSUER.name(),
                "3MVG9_XwsqeYoueJqeZ6P3WcD.FLkTrIJfWkEgypJUiSOf4Jg25v6Wvou1Ovyqlu9ZahEUKypnawUFafsHelc");
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_EXPIRATION_PERIOD.name(), "30000");
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_SUBJECT.name(), "mneugebauer@exclamationlabs.com");
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_AUDIENCE.name(), "https://login.salesforce.com");
        testProperties.put(CONNECTOR_BASE_AUTH_JKS_ALIAS.name(), "concrete_dev_mike");
        testProperties.put(CONNECTOR_BASE_AUTH_JKS_FILE.name(), "/Users/mneugebauer/Documents/test/salesforce/00D3k000000vAA8.jks");
        testProperties.put(CONNECTOR_BASE_AUTH_JKS_PASSWORD.name(), "Kob24Leb#");

        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL.name(), "https://login.salesforce.com/services/oauth2/token");
        configuration = new TestConnectorConfiguration(testProperties);
    }

    @Before
    public void setup() {
        Authenticator jwtAuthenticator = new JWTRS256Authenticator() {
            @Override
            protected RSAPrivateKey getPrivateKey() {
                return new JKSRSAPrivateKeyLoader().load(configuration);
            }

            @Override
            protected Set<ConnectorProperty> getPrivateKeyLoaderPropertyNames() {
                return new JKSRSAPrivateKeyLoader().getRequiredPropertyNames();
            }
        };
        oauth2Authenticator = new OAuth2TokenJWTAuthenticator(jwtAuthenticator);
    }

    @Test
    public void test() {
        String response = oauth2Authenticator.authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().getAccessToken());
        assertNotNull(configuration.getOauth2Information().getId());
        assertNotNull(configuration.getOauth2Information().getInstanceUrl());
        assertNotNull(configuration.getOauth2Information().getScope());
        assertNotNull(configuration.getOauth2Information().getTokenType());
    }
}
