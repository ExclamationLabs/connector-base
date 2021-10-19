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
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Test for OAuth2TokenClientCredentialsAuthenticator, using Dev 1U FIS configuration
 */
public class OAuth2TokenClientCredentialsAuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().
                withConnector(() -> "FIS").
                withOwner(() -> "FIRST_UNITED").build();
    }

    protected OAuth2TokenClientCredentialsAuthenticator oauth2Authenticator;

    @Override
    OAuth2TokenClientCredentialsAuthenticator getAuthenticator() {
        return oauth2Authenticator;
    }

    @Before
    public void setup() {
        KeyStoreLoader<PfxConfiguration> keyStoreLoader = new PFXKeyStoreLoader();
        SecureClientLoader<PfxConfiguration> clientLoader = new HttpsKeystoreCertificateClientLoader();

        oauth2Authenticator = new OAuth2TokenClientCredentialsAuthenticator() {
            @Override
            public HttpClient getHttpClient() {
                return clientLoader.load((PfxConfiguration) configuration,
                        keyStoreLoader.load((PfxConfiguration) configuration));
            }
        };
        super.setup();
    }

    @Test
    @Ignore // TODO: implement active configuration strategy
    public void test() {
        Oauth2ClientCredentialsConfiguration check = (Oauth2ClientCredentialsConfiguration) configuration;
        String response = getAuthenticator().authenticate(check);
        assertNotNull(response);
        assertNotNull(check.getOauth2Information());
        assertNotNull(check.getOauth2Information().get("accessToken"));
        assertNotNull(check.getOauth2Information().get("tokenType"));
    }
}
