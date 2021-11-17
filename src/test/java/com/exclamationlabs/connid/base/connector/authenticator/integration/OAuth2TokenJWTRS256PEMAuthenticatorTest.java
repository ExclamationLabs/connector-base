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

import com.exclamationlabs.connid.base.connector.authenticator.JWTRS256Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenJWTAuthenticator;
import com.exclamationlabs.connid.base.connector.authenticator.keys.PEMRSAPrivateKeyLoader;
import com.exclamationlabs.connid.base.connector.configuration.*;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.PemConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.JwtRs256Configuration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2JwtConfiguration;
import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Integration test for both the OAuth2JWTAuthenticator,
 * JWTRS256Authenticator and PEMRSAPrivateKeyLoader working together,
 * using Dev ELabs Docusign
 */
@Ignore // TODO: put back in place once Docusign is upgraded to 2.0 base
public class OAuth2TokenJWTRS256PEMAuthenticatorTest extends IntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(() -> "DOCUSIGN").build();
    }

    @Test
    public void test() {
        Oauth2JwtConfiguration configuration = new TestConfiguration(getConfigurationName());
        ConfigurationReader.setupTestConfiguration(configuration);
        setup(configuration);

        JWTRS256Authenticator jwtAuthenticator = new JWTRS256Authenticator() {
            @Override
            protected Map<String, String> getExtraClaimData() {
                Map<String, String> extraData = new HashMap<>();
                extraData.put("scope", "signature impersonation");
                return extraData;
            }

            @Override
            protected RSAPrivateKey getPrivateKey() {
                return new PEMRSAPrivateKeyLoader().load((PemConfiguration) configuration);
            }

        };
        OAuth2TokenJWTAuthenticator oauth2Authenticator = new OAuth2TokenJWTAuthenticator(jwtAuthenticator);

        String response = oauth2Authenticator.authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().get("accessToken"));
        assertNotNull(configuration.getOauth2Information().get("tokenType"));
    }

    protected static class TestConfiguration extends DefaultConnectorConfiguration
            implements JwtRs256Configuration, PemConfiguration, Oauth2JwtConfiguration {

        @ConfigurationInfo(path = "security.pem.pemFile")
        private String pemFile;

        @ConfigurationInfo(path = "security.authenticator.jwtRs256.issuer")
        private String issuer;

        @ConfigurationInfo(path = "security.authenticator.jwtRs256.subject")
        private String subject;

        @ConfigurationInfo(path = "security.authenticator.jwtRs256.expirationPeriod")
        private Long expirationPeriod;

        @ConfigurationInfo(path = "security.authenticator.jwtRs256.audience")
        private String audience;

        @ConfigurationInfo(path = "security.authenticator.jwtRs256.useIssuedAt")
        private Boolean useIssuedAt;

        @ConfigurationInfo(path = "security.authenticator.oauth2Jwt.tokenUrl")
        private String tokenUrl;

        @ConfigurationInfo(path = "security.authenticator.oauth2Jwt.oauth2Information")
        private Map<String, String> oauth2Information;

        public TestConfiguration(String nameIn) {
            name = nameIn;
        }

        @Override
        public String getPemFile() {
            return pemFile;
        }

        @Override
        public void setPemFile(String input) {
            pemFile = input;
        }

        @Override
        public String getIssuer() {
            return issuer;
        }

        @Override
        public void setIssuer(String input) {
            issuer = input;
        }

        @Override
        public String getSubject() {
            return subject;
        }

        @Override
        public void setSubject(String input) {
            subject = input;
        }

        @Override
        public Long getExpirationPeriod() {
            return expirationPeriod;
        }

        @Override
        public void setExpirationPeriod(Long input) {
            expirationPeriod = input;
        }

        @Override
        public String getAudience() {
            return audience;
        }

        @Override
        public void setAudience(String input) {
            audience = input;
        }

        @Override
        public Boolean getUseIssuedAt() {
            return useIssuedAt;
        }

        @Override
        public void setUseIssuedAt(Boolean input) {
            useIssuedAt = input;
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
        public Map<String, String> getOauth2Information() {
            return oauth2Information;
        }

        @Override
        public void setOauth2Information(Map<String, String> info) {
            oauth2Information = info;
        }
    }
}
