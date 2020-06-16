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

package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import org.junit.Test;

import java.util.Properties;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DirectAccessTokenAuthenticatorTest {

    protected static BaseConnectorConfiguration configuration;

    static {
        Properties testProperties = new Properties();
        testProperties.put(CONNECTOR_BASE_AUTH_DIRECT_TOKEN.name(), "cooltoken");
        configuration = new TestConnectorConfiguration(testProperties);
    }

    @Test
    public void test() {
        Authenticator authenticator = new DirectAccessTokenAuthenticator();
        String response = authenticator.authenticate(configuration);
        assertNotNull(response);
        assertEquals("cooltoken", response);
    }
}
