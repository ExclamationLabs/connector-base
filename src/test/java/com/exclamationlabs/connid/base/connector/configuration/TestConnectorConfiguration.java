package com.exclamationlabs.connid.base.connector.configuration;

import java.util.Properties;

public class TestConnectorConfiguration extends BaseConnectorConfiguration {

    private String configurationFileLocation;

    public TestConnectorConfiguration(Properties testProperties) {
        setConnectorProperties(testProperties);
    }

    public TestConnectorConfiguration(String configurationName) {
        if (System.getenv(configurationName) != null) {
            configurationFileLocation = System.getenv(configurationName);
        } else {
            configurationFileLocation = "src/test/resources/" +
                    configurationName + ".properties";
        }
    }

    @Override
    public String getConfigurationFilePath() {
        return configurationFileLocation;
    }
}
