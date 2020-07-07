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

package com.exclamationlabs.connid.base.connector.configuration;

import com.exclamationlabs.connid.base.connector.stub.StubConnector;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class BaseConnectorConfigurationTest {

    @Test(expected=ConfigurationException.class)
    public void testDisabledConfiguration() {
        ConnectorConfiguration configuration = new BaseConnectorConfiguration() {
            @Override
            public String getConfigurationFilePath() {
                return null;
            }

            @Override
            public void setup() {
                Properties properties = new Properties();
                properties.setProperty(
                        ConnectorProperty.CONNECTOR_BASE_CONFIGURATION_ACTIVE.name(), "N");
                setConnectorProperties(properties);
                super.setup();
            }
        };
        configuration.setup();
        new StubConnector().init(configuration);

    }

    @Test(expected=ConfigurationException.class)
    public void testAdditionalConfigurationMissingProperty() {
        ConnectorConfiguration configuration = new BaseConnectorConfiguration() {
            @Override
            public List<String> getAdditionalPropertyNames() {
                return Collections.singletonList("ANOTHER_ONE");
            }

            @Override
            public String getConfigurationFilePath() {
                return null;
            }

            @Override
            public void setup() {
                Properties properties = new Properties();
                properties.setProperty(
                        ConnectorProperty.CONNECTOR_BASE_CONFIGURATION_ACTIVE.name(), "Y");
                setConnectorProperties(properties);
                super.setup();
            }
        };
        configuration.setup();
        new StubConnector().init(configuration);

    }

    @Test
    public void testAdditionalConfigurationValidProperty() {
        ConnectorConfiguration configuration = new BaseConnectorConfiguration() {
            @Override
            public List<String> getAdditionalPropertyNames() {
                return Collections.singletonList("ANOTHER_ONE");
            }

            @Override
            public String getConfigurationFilePath() {
                return null;
            }

            @Override
            public void setup() {
                Properties properties = new Properties();
                properties.setProperty(
                        ConnectorProperty.CONNECTOR_BASE_CONFIGURATION_ACTIVE.name(), "Y");
                setConnectorProperties(properties);
                setProperty("ANOTHER_ONE", "GOOD");
                super.setup();
            }
        };
        configuration.setup();
        new StubConnector().init(configuration);
    }

    @Test(expected=ConfigurationException.class)
    public void testMissingConfiguration() {
        ConnectorConfiguration configuration = new BaseConnectorConfiguration() {
            @Override
            public String getConfigurationFilePath() {
                return null;
            }
        };
        configuration.setup();
        new StubConnector().init(configuration);

    }

}
