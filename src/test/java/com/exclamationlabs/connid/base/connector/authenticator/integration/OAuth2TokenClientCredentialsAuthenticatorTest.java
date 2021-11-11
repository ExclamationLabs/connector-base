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

import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenClientCredentialsAuthenticator;
import com.exclamationlabs.connid.base.connector.authenticator.client.HttpsKeystoreCertificateClientLoader;
import com.exclamationlabs.connid.base.connector.authenticator.client.SecureClientLoader;
import com.exclamationlabs.connid.base.connector.authenticator.keys.KeyStoreLoader;
import com.exclamationlabs.connid.base.connector.authenticator.keys.PFXKeyStoreLoader;
import com.exclamationlabs.connid.base.connector.configuration.*;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.PfxConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2ClientCredentialsConfiguration;
import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import org.apache.http.client.HttpClient;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Test for OAuth2TokenClientCredentialsAuthenticator, using Dev 1U FIS configuration
 */
@Ignore // TODO: update later; cannot test without current valid FIS password
public class OAuth2TokenClientCredentialsAuthenticatorTest extends IntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().
                withConnector(() -> "FIS").
                withOwner(() -> "FIRST_UNITED").build();
    }

    @Test
    @Ignore // TODO: implement active configuration strategy
    public void test() {
        Oauth2ClientCredentialsConfiguration configuration =
                new TestConfiguration(getConfigurationName());

        KeyStoreLoader<PfxConfiguration> keyStoreLoader = new PFXKeyStoreLoader();
        SecureClientLoader<PfxConfiguration> clientLoader = new HttpsKeystoreCertificateClientLoader();

        OAuth2TokenClientCredentialsAuthenticator oauth2Authenticator = new OAuth2TokenClientCredentialsAuthenticator() {
            @Override
            public HttpClient getHttpClient() {
                return clientLoader.load((PfxConfiguration) configuration,
                        keyStoreLoader.load((PfxConfiguration) configuration));
            }
        };
        setup(configuration);

        String response = oauth2Authenticator.authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().get("accessToken"));
        assertNotNull(configuration.getOauth2Information().get("tokenType"));
    }

    static class TestConfiguration extends DefaultConnectorConfiguration
            implements Oauth2ClientCredentialsConfiguration, PfxConfiguration {

        @ConfigurationInfo(path = "security.authenticator.oauth2ClientCredentials.tokenUrl")
        private String tokenUrl;

        @ConfigurationInfo(path = "security.authenticator.oauth2ClientCredentials.clientId")
        private String clientId;

        @ConfigurationInfo(path = "security.authenticator.oauth2ClientCredentials.clientSecret")
        private String clientSecret;

        @ConfigurationInfo(path = "security.authenticator.oauth2ClientCredentials.scope")
        private String scope;

        @ConfigurationInfo(path = "security.authenticator.oauth2ClientCredentials.oauth2Information")
        private Map<String, String> oauth2Information;

        @ConfigurationInfo(path = "security.pfx.pfxFile")
        private String pfxFile;

        @ConfigurationInfo(path = "security.pfx.pfxPassword")
        private String pfxPassword;

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
        public String getScope() {
            return scope;
        }

        @Override
        public void setScope(String input) {
            scope = input;
        }

        @Override
        public Map<String, String> getOauth2Information() {
            return oauth2Information;
        }

        @Override
        public void setOauth2Information(Map<String, String> info) {
            oauth2Information = info;
        }

        @Override
        public String getPfxFile() {
            return pfxFile;
        }

        @Override
        public void setPfxFile(String input) {
            pfxFile = input;
        }

        @Override
        public String getPfxPassword() {
            return pfxPassword;
        }

        @Override
        public void setPfxPassword(String input) {
            pfxPassword = input;
        }
    }
}
