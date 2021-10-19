/*
    Copyright 2021 Exclamation Labs

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

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;

public class ConfigurationReader {

    private static final Log LOG = Log.getLog(ConfigurationReader.class);

    private ConfigurationReader() {}

    public static void prepareTestConfiguration(String configurationName, ConnectorConfiguration configuration) {
        LOG.info("Test Configuration with location {0} specified", configurationName);
        configuration.setName(configurationName);
        if (System.getenv(configurationName) != null) {
            // Found environment variable match for configuration name,
            // indicating this must be a Jenkins build.  Read configuration file path from
            // environment variable in Jenkins with this configurationName
            configuration.setSource(System.getenv(configurationName));
        } else {
            // This appears to be an integration test run from local workstation
            // Use project test properties file path matching this configuration name
            configuration.setSource("src/test/resources/" +
                    configurationName + ".properties");
        }
    }

    public static void readPropertiesFromSource(ConnectorConfiguration configuration) {
        LOG.info("invoking configuration setup for {0}", configuration.getClass().getSimpleName());
        if (configuration.getSource() != null) {
            LOG.info("using configuration path {0}", configuration.getSource());
            Properties connectorProperties = new Properties();
            try {
                connectorProperties.load(new FileReader(configuration.getSource()));
                LOG.info("configuration properties loaded from {0} for configuration {1}",
                        configuration.getSource(), configuration.getClass().getSimpleName());
            } catch (IOException ex) {
                throw new ConfigurationException("Failed to read configuration file from location " +
                        configuration.getSource(), ex);
            }
            // TODO: load Properties into configuration fields, based on matching @ConfigurationInfo.path info

            LOG.info("configuration properties loaded into configuration object from properties " +
                            "for configuration {0}", configuration.getClass().getSimpleName());
            // Note: validation is done later via BaseConnector init()
        }
    }
}
