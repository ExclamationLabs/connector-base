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

package com.exclamationlabs.connid.base.connector.driver.exception;

import org.identityconnectors.framework.common.exceptions.ConnectorException;

/**
 * This exception can be thrown/caught/handled by Drivers and FaultProcessors
 * as needed to gracefully handle not found exceptions.
 */
public class DriverDataNotFoundException extends ConnectorException {

    public DriverDataNotFoundException() {
        super();
    }

    public DriverDataNotFoundException(String message) {
        super(message);
    }

    public DriverDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
