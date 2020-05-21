package com.exclamationlabs.connid.base.connector.configuration;

import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.ConnectorMessages;

/**
 * All configuration classes in the Base connector framework need to subclass this
 * abstract type.  This is an overlay for ConnId's configuration class, which
 * can be confusing and uninviting to use, and we needed something more streamlined.
 */
public abstract class BaseConnectorConfiguration implements ConnectorConfiguration {
    private boolean validated;
    private ConnectorMessages connectorMessages;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    protected void setValidated(boolean in) {
        validated = in;
    }

    @Override
    public boolean isValidated() {
        return validated;
    }

    @Override
    public void setupAndValidate() throws ConfigurationException {
        setValidated(true);
    }

    @Override
    public ConnectorMessages getConnectorMessages() {
        return connectorMessages;
    }

    @Override
    public void setConnectorMessages(ConnectorMessages messages) {
        connectorMessages = messages;
    }

}
