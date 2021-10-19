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
import com.exclamationlabs.connid.base.connector.authenticator.keys.JKSRSAPrivateKeyLoader;
import com.exclamationlabs.connid.base.connector.configuration.*;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.JksConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2JwtConfiguration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.interfaces.RSAPrivateKey;

import static org.junit.Assert.assertNotNull;

/**
 * Integration test for both the OAuth2JWTAuthenticator,
 * JWTRS256Authenticator and JKSRSAPrivateKeyLoader working together,
 * using Dev ELabs Salesforce configuration
 */
public class OAuth2TokenJWTRS256JKSAuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(() -> "SALESFORCE").build();
    }

    protected OAuth2TokenJWTAuthenticator oauth2Authenticator;

    @Override
    OAuth2TokenJWTAuthenticator getAuthenticator() {
        return oauth2Authenticator;
    }

    @Before
    public void setup() {

        JWTRS256Authenticator jwtAuthenticator = new JWTRS256Authenticator() {
            @Override
            protected RSAPrivateKey getPrivateKey() {
                return new JKSRSAPrivateKeyLoader().load((JksConfiguration) configuration);
            }
        };
        oauth2Authenticator = new OAuth2TokenJWTAuthenticator(jwtAuthenticator);
        super.setup();
    }

    @Test
    @Ignore // TODO: implement active configuration strategy
    public void test() {
        Oauth2JwtConfiguration check = (Oauth2JwtConfiguration) configuration;
        String response = getAuthenticator().authenticate(check);
        assertNotNull(response);
        assertNotNull(check.getOauth2Information());
        assertNotNull(check.getOauth2Information().get("accessToken"));
        assertNotNull(check.getOauth2Information().get("id"));
        assertNotNull(check.getOauth2Information().get("instanceUrl"));
        assertNotNull(check.getOauth2Information().get("scope"));
        assertNotNull(check.getOauth2Information().get("tokenType"));
    }


}
