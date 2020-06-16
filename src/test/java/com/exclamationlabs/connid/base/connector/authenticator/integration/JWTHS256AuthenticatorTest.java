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
import com.exclamationlabs.connid.base.connector.authenticator.JWTHS256Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.*;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Test for JWTHS256Authenticator, using Dev ELabs Zoom configuration
 */
public class JWTHS256AuthenticatorTest extends BaseAuthenticatorIntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(ConfigurationConnector.ZOOM).build();
    }

    @Test
    public void test() {
        Authenticator jwtAuthenticator = new JWTHS256Authenticator();
        String response = jwtAuthenticator.authenticate(configuration);
        assertNotNull(response);
    }

}
