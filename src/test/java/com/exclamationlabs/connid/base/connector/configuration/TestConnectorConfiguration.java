package com.exclamationlabs.connid.base.connector.configuration;

import java.util.Properties;

public class TestConnectorConfiguration extends BaseConnectorConfiguration {

    public TestConnectorConfiguration(Properties testProperties) {
        setConnectorProperties(testProperties);
    }

    @Override
    public String getConfigurationFilePath() {
        return null;
    }
}
