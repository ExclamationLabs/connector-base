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

import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenRefreshTokenAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationInfo;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationNameBuilder;

import com.exclamationlabs.connid.base.connector.configuration.ConfigurationReader;
import com.exclamationlabs.connid.base.connector.configuration.DefaultConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2RefreshTokenConfiguration;
import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Test for OAuth2TokenAuthorizationCodeAuthenticator, using Dev ELabs Webex configuration
 */
@Ignore // TODO: put back in place once Webex is upgraded to 2.0 base
public class OAuth2TokenRefreshTokenAuthenticatorTest extends IntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(() -> "WEBEX").build();
    }

    @Test
    public void test() {
        TestConfiguration configuration = new TestConfiguration(getConfigurationName());
        ConfigurationReader.setupTestConfiguration(configuration);
        setup(configuration);
        String response = new OAuth2TokenRefreshTokenAuthenticator().authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().get("accessToken"));
        assertNotNull(configuration.getOauth2Information().get("refreshToken"));
        assertNotNull(configuration.getOauth2Information().get("expiresIn"));
    }


    protected static class TestConfiguration extends DefaultConnectorConfiguration
            implements Oauth2RefreshTokenConfiguration {

        @ConfigurationInfo(path = "security.authenticator.oauth2RefreshToken.refreshToken")
        private String refreshToken;

        @ConfigurationInfo(path = "security.authenticator.oauth2RefreshToken.tokenUrl")
        private String tokenUrl;

        @ConfigurationInfo(path = "security.authenticator.oauth2RefreshToken.clientId")
        private String clientId;

        @ConfigurationInfo(path = "security.authenticator.oauth2RefreshToken.clientSecret")
        private String clientSecret;

        @ConfigurationInfo(path = "security.authenticator.oauth2AuthorizationCode.oauth2Information")
        private Map<String, String> oauth2Information;

        public TestConfiguration(String nameIn) {
            name = nameIn;
        }

        @Override
        public String getTokenUrl() {
            return tokenUrl;
        }

        @Override
        public void setTokenUrl(String input) {
            tokenUrl = input;
        }

        @Override
        public String getRefreshToken() {
            return refreshToken;
        }

        @Override
        public void setRefreshToken(String input) {
            refreshToken = input;
        }

        @Override
        public String getClientId() {
            return clientId;
        }

        @Override
        public void setClientId(String input) {
            clientId = input;
        }

        @Override
        public String getClientSecret() {
            return clientSecret;
        }

        @Override
        public void setClientSecret(String input) {
            clientSecret = input;
        }

        @Override
        public Map<String, String> getOauth2Information() {
            return oauth2Information;
        }

        @Override
        public void setOauth2Information(Map<String, String> info) {
            oauth2Information = info;
        }
    }


}
