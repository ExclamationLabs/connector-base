package com.exclamationlabs.connid.base.connector.configuration;

import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.ConnectorMessages;

public class DefaultConnectorConfiguration implements ConnectorConfiguration {
    private boolean validated;
    private ConnectorMessages connectorMessages;

    @Override
    public String getName() {
        return "DefaultConnectorConfiguration";
    }

    @Override
    public boolean isValidated() {
        return validated;
    }

    @Override
    public void setupAndValidate() throws ConfigurationException {
        validated = true;
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
