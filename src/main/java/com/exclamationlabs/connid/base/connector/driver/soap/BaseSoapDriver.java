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

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TrustStoreConfiguration;
import com.exclamationlabs.connid.base.connector.driver.BaseDriver;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

/**
 * Abstract class for drivers that need to make calls to SOAP web services
 * to manage identity access information.
 * NOTE: It could be possible/required for a BaseSoapDriver subclass to call
 * more than one SOAP web service.  Subclasses will be responsible for
 * organizing the services and port types for those.  The default here
 * reflects the single common one to be used.
 * @param <S> Default SOAP Web Service to call
 * @param <P> Port type for Default Service
 */
public abstract class BaseSoapDriver<S extends Service,P> extends BaseDriver {

    private static final Log LOG = Log.getLog(BaseSoapDriver.class);

    protected ConnectorConfiguration configuration;
    protected Authenticator authenticator;

    @Override
    public void initialize(ConnectorConfiguration config, Authenticator auth)
            throws ConnectorException {
        TrustStoreConfiguration.clearJdkProperties();
        authenticator = auth;
        configuration = config;
    }

    abstract protected S getService();

    abstract protected P getServicePort(S service);

    /**
     * Return the fault processor that will be used to analyze and respond
     * to HTTP error responses.
     * @return The SoapFaultProcessor subclass to be used when SOAP invocation
     * receives response fault(s).
     */
    abstract protected SoapFaultProcessor getFaultProcessor();

    protected Object executeAndHandleFault(SoapExecution se) {
        Object result = null;
        try {
            result = se.execute(se);
        } catch (SOAPFaultException sfe) {
            LOG.error("SOAPFault received", sfe);
            getFaultProcessor().process(sfe);
        }
        return result;
    }

    public ConnectorConfiguration getConfiguration() {
        return configuration;
    }

}
