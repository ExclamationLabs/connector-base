package com.exclamationlabs.connid.base.connector.driver;

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
