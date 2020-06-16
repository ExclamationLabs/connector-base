package com.exclamationlabs.connid.base.connector.authenticator.integration;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.JWTRS256Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenJWTAuthenticator;
import com.exclamationlabs.connid.base.connector.authenticator.keys.PEMRSAPrivateKeyLoader;
import com.exclamationlabs.connid.base.connector.configuration.*;
import org.junit.Before;
import org.junit.Test;

import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

/**
 * Integration test for both the OAuth2JWTAuthenticator,
 * JWTRS256Authenticator and PEMRSAPrivateKeyLoader working together,
 * using Dev ELabs Docusign
 */
public class OAuth2TokenJWTRS256PEMAuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    protected Authenticator oauth2Authenticator;

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(ConfigurationConnector.DOCUSIGN).build();
    }

    @Before
    public void setup() {
        super.setup();
        Map<String, String> extraData = new HashMap<>();
        extraData.put("scope", "signature impersonation");
        configuration.setExtraJWTClaimData(extraData);

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
