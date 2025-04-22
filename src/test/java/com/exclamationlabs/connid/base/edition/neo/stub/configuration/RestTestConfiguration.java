package com.exclamationlabs.connid.base.edition.neo.stub.configuration;

import com.exclamationlabs.connid.base.connector.configuration.basetypes.RestConfiguration;
import org.identityconnectors.framework.common.objects.ConnectorMessages;

public class RestTestConfiguration implements RestConfiguration {
    @Override
    public Integer getIoErrorRetries() {
        return 0;
    }

    @Override
    public void setIoErrorRetries(Integer input) {

    }

    @Override
    public String getCurrentToken() {
        return "";
    }

    @Override
    public void setCurrentToken(String input) {

    }

    @Override
    public String getSource() {
        return "";
    }

    @Override
    public void setSource(String input) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setName(String input) {

    }

    @Override
    public Boolean getActive() {
        return null;
    }

    @Override
    public void setActive(Boolean input) {

    }

    @Override
    public ConnectorMessages getConnectorMessages() {
        return null;
    }

    @Override
    public void setConnectorMessages(ConnectorMessages messages) {

    }
}
