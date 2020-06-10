package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;
import static org.junit.Assert.assertNotNull;

public class OAuth2TokenAuthorizationCodeAuthenticatorTest {

    // TODO: Using Mike's Webex dev account info to test.  Figure out a strategy
    // to tuck this away

    protected static BaseConnectorConfiguration configuration;

    static {
        Properties testProperties = new Properties();

        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL.name(), "https://webexapis.com/v1/access_token");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_AUTHORIZATION_CODE.name(),
                "ZTBlODQ5MTMtODk1Yy00ZjRmLTk4NmYtMjBiOWRiMmVhMjQxODBkNjI5NzItNjRj_PF84_consumer");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_ID.name(),
                "C45a94a2dbcf80764d1cd27e951fc5a9fde0deee23a24c68e4c2f37d66e249fdc");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_SECRET.name(),
                "ff6c04baa2c2574f94a7dd4fce77e8ee9db3a1153b39bd0860adea7f9542ebfa");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_REDIRECT_URI.name(),
                "http://localhost");
        configuration = new TestConnectorConfiguration(testProperties);
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
