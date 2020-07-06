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

import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.Configuration;

/**
 * Architectural interface used to wrap ConnId's Configuration interface,
 * which the BaseConnectorConfiguration implements.
 */
public interface ConnectorConfiguration extends Configuration {

    /**
     * Get the connector configuration name
     * @return String containing a name representation for this connector's configuration
     */
    String getName();

    /**
     * Get whether of not the connector has already been validated
     * @return true if validation has already been run and was successful, false if it hasn't been
     * attempted.
     */
    boolean isValidated();

    /**
     * The validate method should load all applicable configuration input (input file(s),
     * properties, etc.).
     * If any problems occur while loading a reliable configuration for this
     * connector, then ConfigurationException should be thrown.
     * @throws ConfigurationException If configuration input could not be loaded or failed validation.
     */
    void setup() throws ConfigurationException;

    /**
     * Validate all configuration input.
     * If any problems occur while validating a reliable configuration for this
     * connector, then ConfigurationException should be thrown.
     * @throws ConfigurationException If configuration input could not be loaded or failed validation.
     */
    void validateConfiguration() throws ConfigurationException;


    String getCredentialAccessToken();

    @Override
    default void validate() {
        setup();
        validateConfiguration();
    }



}