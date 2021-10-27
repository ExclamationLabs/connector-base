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

import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenPasswordAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.*;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2PasswordConfiguration;
import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Integration test for both the OAuth2TokenPasswordAuthenticator,
 * using Dev ELabs GotoMeeting
 */
public class OAuth2TokenPasswordAuthenticatorTest extends IntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(() -> "GOTOMEETING").build();
    }

    @Test
    public void test() {
        TestConfiguration configuration = new TestConfiguration(getConfigurationName());
        ConfigurationReader.setupTestConfiguration(configuration);
        setup(configuration);
        String response = new OAuth2TokenPasswordAuthenticator().authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().get("accessToken"));
        assertNotNull(configuration.getOauth2Information().get("tokenType"));
    }

    protected static class TestConfiguration extends DefaultConnectorConfiguration
            implements Oauth2PasswordConfiguration {

        @ConfigurationInfo(path = "security.authenticator.oauth2Password.tokenUrl")
        private String tokenUrl;

        @ConfigurationInfo(path = "security.authenticator.oauth2Password.encodedSecret")
        private String encodedSecret;

        @ConfigurationInfo(path = "security.authenticator.oauth2Password.username")
        private String username;

        @ConfigurationInfo(path = "security.authenticator.oauth2Password.password")
        private String password;

        @ConfigurationInfo(path = "security.authenticator.oauth2Password.oauth2Information")
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
        public String getEncodedSecret() {
            return encodedSecret;
        }

        @Override
        public void setEncodedSecret(String input) {
            encodedSecret = input;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public void setUsername(String input) {
            username = input;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public void setPassword(String input) {
            password = input;
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
