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
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenPasswordAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationConnector;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationNameBuilder;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2PasswordConfiguration;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Integration test for both the OAuth2TokenPasswordAuthenticator,
 * using Dev ELabs GotoMeeting
 */
public class OAuth2TokenPasswordAuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    Authenticator getAuthenticator() {
        return new OAuth2TokenPasswordAuthenticator();
    }

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(() -> "GOTOMEETING").build();
    }

    @Test
    @Ignore // TODO: implement active configuration strategy
    public void test() {
        Oauth2PasswordConfiguration check = (Oauth2PasswordConfiguration) configuration;
        String response = getAuthenticator().authenticate(check);
        assertNotNull(response);
        assertNotNull(check.getOauth2Information());
        assertNotNull(check.getOauth2Information().get("accessToken"));
        assertNotNull(check.getOauth2Information().get("tokenType"));
    }


}
