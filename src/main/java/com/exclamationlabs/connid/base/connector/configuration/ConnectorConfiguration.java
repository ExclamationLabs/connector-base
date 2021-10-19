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

import com.exclamationlabs.connid.base.connector.authenticator.model.OAuth2AccessTokenContainer;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.Configuration;

import javax.validation.*;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

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
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ConnectorConfiguration>> violations =
                validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConfigurationException("Validation of Connector configuration " +
                    this.getClass().getSimpleName() + " failed", new ConstraintViolationException(violations));
        }
    }

    default void read(ConnectorConfiguration configuration) {
        ConfigurationReader.readPropertiesFromSource(configuration);
    }

    default String write() {
        return ConfigurationWriter.writeToString(this);
    }


}