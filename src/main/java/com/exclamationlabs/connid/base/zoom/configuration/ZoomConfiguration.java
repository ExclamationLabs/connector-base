package com.exclamationlabs.connid.base.zoom.configuration;

import com.exclamationlabs.connid.base.connector.authenticator.JWTAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class ZoomConfiguration extends BaseConnectorConfiguration {

    public ZoomConfiguration() {
        super(JWTAuthenticator.getRequiredProperties());
    }

    @Override
    @ConfigurationProperty(
            displayMessageKey = "Zoom Configuration File Path",
            helpMessageKey = "File path for the Zoom Configuration File",
            required = true)
    public String getConfigurationFilePath() {
        return getMidPointConfigurationFilePath();
    }
}
