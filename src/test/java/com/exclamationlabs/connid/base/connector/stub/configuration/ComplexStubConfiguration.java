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

package com.exclamationlabs.connid.base.connector.stub.configuration;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;

import java.util.Properties;

public class ComplexStubConfiguration extends BaseConnectorConfiguration {

    public ComplexStubConfiguration() {
        Properties properties = new Properties();
        properties.setProperty(
                ConnectorProperty.CONNECTOR_BASE_CONFIGURATION_ACTIVE.name(), "Y");
        properties.setProperty(
                ConnectorProperty.CONNECTOR_BASE_AUTH_PFX_FILE.name(), "abcd");
        properties.setProperty(
                ConnectorProperty.CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL.name(), "url");
        properties.setProperty(
                ConnectorProperty.CONNECTOR_BASE_AUTH_JKS_PASSWORD.name(), "pwd");
        properties.setProperty(
                ConnectorProperty.CONNECTOR_BASE_AUTH_JKS_ALIAS.name(), "alias");
        properties.setProperty(
                ConnectorProperty.CONNECTOR_BASE_AUTH_PFX_PASSWORD.name(), "pwd2");
        properties.setProperty(
                ConnectorProperty.CONNECTOR_BASE_AUTH_JKS_FILE.name(), "url2");

        setConnectorProperties(properties);
    }

    @Override
    public String getConfigurationFilePath() {
        return null;
    }
}
