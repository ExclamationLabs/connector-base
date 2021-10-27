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

package com.exclamationlabs.connid.base.connector.configuration;

import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.spi.Configuration;


/**
 * Architectural interface used to wrap ConnId's Configuration interface,
 * which the BaseConnectorConfiguration implements.
 */
public interface ConnectorConfiguration extends Configuration {

    String getCurrentToken();
    void setCurrentToken(String input);

    String getSource();
    void setSource(String input);

    String getName();
    void setName(String input);

    Boolean getActive();
    void setActive(Boolean input);

    @Override
    default void validate() {
        ConfigurationValidator.validate(this);
    }

    default void read(ConnectorConfiguration configuration) {
        ConfigurationReader.readPropertiesFromSource(configuration);
    }

    default String write() {
        return ConfigurationWriter.writeToString(this);
    }

    default boolean isTestConfiguration() {
        return (!StringUtils.equalsIgnoreCase("default", getName()));
    }

}