/*
    Copyright 2020 Exclamation Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.exclamationlabs.connid.base.connector.authenticator.integration;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenRefreshTokenAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationConnector;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationNameBuilder;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;
import static org.junit.Assert.assertNotNull;

public class OAuth2TokenRefreshTokenAuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(ConfigurationConnector.WEBEX).build();
    }
/*
    static {
        Properties testProperties = new Properties();

        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL.name(), "https://webexapis.com/v1/access_token");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_REFRESH_TOKEN.name(),
                "YjNlMzRjZWMtZjRmNC00YWNkLThiM2YtYzFhMGJjZmYxOTY2MjRhNWVhOTgtY2Q2_P0A1_176a65f5-8855-4a07-a005-1ab2959c9962");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_ID.name(),
                "C2502cef016ccf4fe573e0614b9d075d1008ebfc73085849b95a9820886e2db16");
        testProperties.put(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_SECRET.name(),
                "b0ecd9ebe028383bbe380823603a4801c97368cf26393c25f2a5543094c250db");
        configuration = new TestConnectorConfiguration(testProperties);
    }
*/
    @Test
    @Ignore
    public void test() {
        Authenticator authenticator = new OAuth2TokenRefreshTokenAuthenticator();
        String response = authenticator.authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().getAccessToken());
        assertNotNull(configuration.getOauth2Information().getRefreshToken());
        assertNotNull(configuration.getOauth2Information().getExpiresIn());
    }
}
