package com.exclamationlabs.connid.base.connector.driver.soap;

import javax.xml.ws.soap.SOAPFaultException;

/**
 * Simple interface to allow lambda execution of SOAP
 * service method, with automatic catching/handling for SOAPFaultException.
 */
public interface SoapExecution {
    Object execute(Object input) throws SOAPFaultException;
}
