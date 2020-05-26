package com.exclamationlabs.connid.base.connector.configuration;

import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.ConnectorMessages;
import org.identityconnectors.framework.spi.Configuration;

/**
 * All configuration classes in the Base connector framework need to subclass this
 * abstract type.  This is an overlay for ConnId's configuration class, which
 * can be confusing and uninviting to use, and we needed something more streamlined.
 */
public abstract class BaseConnectorConfiguration implements Configuration {
    private boolean validated;
    private ConnectorMessages connectorMessages;

    @Override
    public ConnectorMessages getConnectorMessages() {
        return connectorMessages;
    }

    @Override
    public void setConnectorMessages(ConnectorMessages messages) {
        connectorMessages = messages;
    }

    @Override
    public void validate() {
        setup();
        validateConfiguration();
    }

    /**
     * Get the connector configuration name
     * @return String containing a name representation for this connector's configuration
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Get whether of not the connector has already been validated
     * @return true if validation has already been run and was successful, false if it hasn't been
     * attempted.
     */
    public boolean isValidated() {
        return validated;
    }

    protected void setValidated(boolean in) {
        validated = in;
    }

    /**
     * The validate method should load all applicable configuration input (input file(s),
     * properties, etc.).
     * If any problems occur while loading a reliable configuration for this
     * connector, then ConfigurationException should be thrown.
     * @throws ConfigurationException If configuration input could not be loaded or failed validation.
     */
    public void setup() throws ConfigurationException {
    }

    /**
     * Validate all configuration input.
     * If any problems occur while validating a reliable configuration for this
     * connector, then ConfigurationException should be thrown.
     * @throws ConfigurationException If configuration input could not be loaded or failed validation.
     */
    public void validateConfiguration() throws ConfigurationException {
        setValidated(true);
    }


}
