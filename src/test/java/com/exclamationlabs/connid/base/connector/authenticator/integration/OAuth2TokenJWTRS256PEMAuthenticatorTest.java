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

import com.exclamationlabs.connid.base.connector.ComplexStubConnectorTest;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.JWTRS256Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenJWTAuthenticator;
import com.exclamationlabs.connid.base.connector.authenticator.keys.PEMRSAPrivateKeyLoader;
import com.exclamationlabs.connid.base.connector.configuration.*;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.PemConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.JwtRs256Configuration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2JwtConfiguration;
import org.identityconnectors.framework.common.objects.ConnectorMessages;
import org.junit.Before;
import org.junit.Ignore;
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

    protected OAuth2TokenJWTAuthenticator oauth2Authenticator;

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(() -> "DOCUSIGN").build();
    }

    @Override
    OAuth2TokenJWTAuthenticator getAuthenticator() {
        return oauth2Authenticator;
    }

    @Before
    public void setup() {
        super.setup();
        configuration = new DualConfiguration();

        JWTRS256Authenticator jwtAuthenticator = new JWTRS256Authenticator() {
            @Override
            protected RSAPrivateKey getPrivateKey() {
                return new PEMRSAPrivateKeyLoader().load((PemConfiguration) configuration);
            }

        };
        oauth2Authenticator = new OAuth2TokenJWTAuthenticator(jwtAuthenticator);


        Map<String, String> extraData = new HashMap<>();
        extraData.put("scope", "signature impersonation");
        ((JwtRs256Configuration)configuration).setExtraClaimData(extraData);
    }

    @Test
    @Ignore // TODO: implement active configuration strategy
    public void test() {
        Oauth2JwtConfiguration check = (Oauth2JwtConfiguration) configuration;
        String response = getAuthenticator().authenticate(check);
        assertNotNull(response);
        assertNotNull(check.getOauth2Information());
        assertNotNull(check.getOauth2Information().get("accessToken"));
        assertNotNull(check.getOauth2Information().get("tokenType"));
    }

    static public class DualConfiguration implements JwtRs256Configuration, Oauth2JwtConfiguration, PemConfiguration {

        @Override
        public String getIssuer() {
            return null;
        }

        @Override
        public void setIssuer(String input) {

        }

        @Override
        public String getSubject() {
            return null;
        }

        @Override
        public void setSubject(String input) {

        }

        @Override
        public Long getExpirationPeriod() {
            return 123456L;
        }

        @Override
        public void setExpirationPeriod(Long input) {

        }

        @Override
        public String getAudience() {
            return null;
        }

        @Override
        public void setAudience(String input) {

        }

        @Override
        public Boolean getUseIssuedAt() {
            return true;
        }

        @Override
        public void setUseIssuedAt(Boolean input) {

        }

        @Override
        public Map<String, String> getExtraClaimData() {
            return null;
        }

        @Override
        public void setExtraClaimData(Map<String, String> data) {

        }

        @Override
        public String getTokenUrl() {
            return "https://account-d.docusign.com/oauth/toke";
        }

        @Override
        public void setTokenUrl(String input) {

        }

        @Override
        public Map<String, String> getOauth2Information() {
            return null;
        }

        @Override
        public void setOauth2Information(Map<String, String> info) {

        }

        @Override
        public String getCurrentToken() {
            return null;
        }

        @Override
        public void setCurrentToken(String input) {

        }

        @Override
        public String getSource() {
            return null;
        }

        @Override
        public void setSource(String input) {

        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void setName(String input) {

        }

        @Override
        public Boolean getActive() {
            return null;
        }

        @Override
        public void setActive(Boolean input) {

        }

        @Override
        public ConnectorMessages getConnectorMessages() {
            return null;
        }

        @Override
        public void setConnectorMessages(ConnectorMessages messages) {

        }

        @Override
        public String getFile() {
            return "src/test/resources/test.pem";
        }

        @Override
        public void setFile(String input) {

        }
    }
}
