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

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.configuration.TestConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.junit.Assume;
import org.junit.Before;

abstract public class BaseAuthenticatorIntegrationTest implements IntegrationTest {

    protected static final Log LOG = Log.getLog(BaseAuthenticatorIntegrationTest.class);

    protected static BaseConnectorConfiguration configuration;

    @Before
    public void setup() {
        configuration = new TestConnectorConfiguration(getConfigurationName());
        try {
            configuration.setup();
            if ("Y".equalsIgnoreCase(configuration.getProperty(
                    ConnectorProperty.CONNECTOR_BASE_CONFIGURATION_ACTIVE))) {
                configuration.setValidated();
            }

        } catch (ConfigurationException ce) {
            LOG.info("Configuration not found: " + ce.getMessage());
        }
        Assume.assumeTrue(configuration.isValidated());
    }
}
