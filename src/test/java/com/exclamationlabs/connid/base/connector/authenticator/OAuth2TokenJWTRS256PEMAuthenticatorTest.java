package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.authenticator.keys.PEMRSAPrivateKeyLoader;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;
import static org.junit.Assert.assertNotNull;

/**
 * Integration test for both the OAuth2JWTAuthenticator,
 * JWTRS256Authenticator and PEMRSAPrivateKeyLoader working together.
 */
public class OAuth2TokenJWTRS256PEMAuthenticatorTest {

    // TODO: Using Mike's DocuSign dev account info to test.  Figure out a strategy
    // to tuck this away

    private static final BaseConnectorConfiguration configuration;

    private Authenticator oauth2Authenticator;

    static {
        Properties testProperties = new Properties();
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_USE_ISSUED_AT.name(), "Y");
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_EXPIRATION_PERIOD.name(), "600000");

        // DocuSign integration key
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_ISSUER.name(), "1dd3c277-0961-4338-8ee6-c39913a23806");

        // DocuSign connector user guid
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_SUBJECT.name(), "dae2661f-c1f0-4ad6-9cb2-41aab26fed41");

        // DocuSign Account domain name
        testProperties.put(CONNECTOR_BASE_AUTH_JWT_AUDIENCE.name(), "account-d.docusign.com");

        testProperties.put(CONNECTOR_BASE_AUTH_PEM_FILE.name(), "/Users/mneugebauer/Documents/DocuSign/DocuSign.privateKey.pem");

        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL.name(), "https://account-d.docusign.com/oauth/token");
        configuration = new TestConnectorConfiguration(testProperties);
        Map<String, String> extraData = new HashMap<>();
        extraData.put("scope", "signature impersonation");
        configuration.setExtraJWTClaimData(extraData);
    }

    @Before
    public void setup() {
        Authenticator jwtAuthenticator = new JWTRS256Authenticator() {
            @Override
            protected RSAPrivateKey getPrivateKey() {
                return new PEMRSAPrivateKeyLoader().load(configuration);
            }

            @Override
            protected Set<ConnectorProperty> getPrivateKeyLoaderPropertyNames() {
                return new PEMRSAPrivateKeyLoader().getRequiredPropertyNames();
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
        assertNotNull(configuration.getOauth2Information().getTokenType());
    }
}
