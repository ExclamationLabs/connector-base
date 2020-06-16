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
import com.exclamationlabs.connid.base.connector.authenticator.JWTRS256Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenJWTAuthenticator;
import com.exclamationlabs.connid.base.connector.authenticator.keys.JKSRSAPrivateKeyLoader;
import com.exclamationlabs.connid.base.connector.configuration.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.interfaces.RSAPrivateKey;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

/**
 * Integration test for both the OAuth2JWTAuthenticator,
 * JWTRS256Authenticator and JKSRSAPrivateKeyLoader working together,
 * using Dev ELabs Salesforce configuration
 */
public class OAuth2TokenJWTRS256JKSAuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(ConfigurationConnector.SALESFORCE).build();
    }

    protected Authenticator oauth2Authenticator;
    @Before
    public void setup() {
        super.setup();
        Authenticator jwtAuthenticator = new JWTRS256Authenticator() {
            @Override
            protected RSAPrivateKey getPrivateKey() {
                return new JKSRSAPrivateKeyLoader().load(configuration);
            }

            @Override
            protected Set<ConnectorProperty> getPrivateKeyLoaderPropertyNames() {
                return new JKSRSAPrivateKeyLoader().getRequiredPropertyNames();
            }
        };
        oauth2Authenticator = new OAuth2TokenJWTAuthenticator(jwtAuthenticator);
    }

    @Test
    @Ignore
    public void test() {
        String response = oauth2Authenticator.authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().getAccessToken());
        assertNotNull(configuration.getOauth2Information().getId());
        assertNotNull(configuration.getOauth2Information().getInstanceUrl());
        assertNotNull(configuration.getOauth2Information().getScope());
        assertNotNull(configuration.getOauth2Information().getTokenType());
    }


}
