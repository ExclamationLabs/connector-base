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
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import org.junit.Before;

/**
 * Abstract base class for running integration tests for various Authenticator
 * implementations in conjuction with existing developer connector credentials.
 */
abstract public class BaseAuthenticatorIntegrationTest implements IntegrationTest {

    protected static BaseConnectorConfiguration configuration;

    abstract Authenticator getAuthenticator();

    @Before
    public void setup() {
        configuration = new TestConnectorConfiguration(getConfigurationName());
        configuration.setRequiredPropertyNames(getAuthenticator().getRequiredPropertyNames());
        setup(configuration);
    }
}
