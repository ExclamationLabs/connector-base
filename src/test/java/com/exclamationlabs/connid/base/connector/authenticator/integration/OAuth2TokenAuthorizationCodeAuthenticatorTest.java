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
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenAuthorizationCodeAuthenticator;

import com.exclamationlabs.connid.base.connector.configuration.ConfigurationConnector;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationNameBuilder;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;

/**
 * Test for OAuth2TokenAuthorizationCodeAuthenticator, using Dev ELabs Webex configuration
 */
public class OAuth2TokenAuthorizationCodeAuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(ConfigurationConnector.WEBEX).build();
    }

    // This is an intermediary step for Webex; Webex should be able to use the Refres
    // token long-term.  So it's best not to run this test as part of integration
    // because we can't rely on it working because of manual browser authentication step
    // this needs for Webex
    @Test
    @Ignore
    public void test() {
        String response = getAuthenticator().authenticate(configuration);
        assertNotNull(response);
        assertNotNull(configuration.getOauth2Information());
        assertNotNull(configuration.getOauth2Information().getAccessToken());
        assertNotNull(configuration.getOauth2Information().getRefreshToken());
        assertNotNull(configuration.getOauth2Information().getExpiresIn());
    }

    @Override
    Authenticator getAuthenticator() {
        return new OAuth2TokenAuthorizationCodeAuthenticator();
    }
}
