package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.junit.Test;

import java.util.Properties;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;
import static org.junit.Assert.assertNotNull;

public class OAuth2TokenPasswordAuthenticatorTest {

    // TODO: Using Mike's GotoMeeting dev account info to test.  Figure out a strategy
    // to tuck this away

    private static final BaseConnectorConfiguration configuration;

    static {
        Properties testProperties = new Properties();

        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL.name(), "https://api.getgo.com/oauth/v2/token");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_USERNAME.name(), "mneugebauer@exclamationlabs.com");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_PASSWORD.name(), "Bling5Me!");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_ENCODED_SECRET.name(),
                "TXNibmRDamdtWGhDb1dRNkxiVDFTUEhBdk80bll2TUw6VFRBbnVWajBwMkpYdmpNOA==");
        configuration = new TestConnectorConfiguration(testProperties);
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
