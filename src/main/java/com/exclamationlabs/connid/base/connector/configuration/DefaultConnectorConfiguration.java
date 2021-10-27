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

import org.identityconnectors.framework.common.objects.ConnectorMessages;
import org.identityconnectors.framework.spi.ConfigurationClass;

@ConfigurationClass(skipUnsupported = true)
public class DefaultConnectorConfiguration implements ConnectorConfiguration {

    @ConfigurationInfo(path="currentToken", internal=true)
    protected String currentToken;

    @ConfigurationInfo(path="source", internal=true)
    protected String source;

    @ConfigurationInfo(path="name", internal=true)
    protected String name;

    @ConfigurationInfo(path="active", internal=true)
    protected Boolean active;

    protected ConnectorMessages connectorMessages;

    public DefaultConnectorConfiguration() {
        source = "default";
        name = "default";
        active = true;
    }

    public DefaultConnectorConfiguration(String configurationName) {
        name = configurationName;
    }

    @Override
    public ConnectorMessages getConnectorMessages() {
        return connectorMessages;
    }

    @Override
    public void setConnectorMessages(ConnectorMessages messages) {
        connectorMessages = messages;
    }

    @Override
    public String getCurrentToken() {
        return currentToken;
    }

    @Override
    public void setCurrentToken(String input) {
        currentToken = input;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String input) {
        source = input;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String input) {
        name = input;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(Boolean input) {
        active = input;
    }
}
