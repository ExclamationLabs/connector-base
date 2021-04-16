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

package com.exclamationlabs.connid.base.connector.driver.soap;

import org.identityconnectors.framework.common.exceptions.ConnectorException;

import javax.xml.ws.soap.SOAPFaultException;

/**
 * Default SOAPFault processor implementation.  Simply returns the fault information
 * that can be found in the SOAPFault structure and throws ConnectorException.
 */
public class DefaultSoapFaultProcessor implements SoapFaultProcessor {

    public void process(SOAPFaultException faultException) throws ConnectorException {

        if (faultException.getFault() == null) {
            throw new ConnectorException("SOAPFault encountered, no fault detail", faultException);
        }

        String details = "SOAPFault: "
            + "[FaultString: " + faultException.getFault().getFaultString() + "],"
            + "[FaultActor: " + faultException.getFault().getFaultActor() + "],"
            + "[FaultCode: " + faultException.getFault().getFaultCode() + "],"
            + "[FaultDetail: " + ((faultException.getFault().getDetail() == null)
                ? "" : faultException.getFault().getDetail().getValue()) + "]";
        throw new ConnectorException(details, faultException);
    }
}
