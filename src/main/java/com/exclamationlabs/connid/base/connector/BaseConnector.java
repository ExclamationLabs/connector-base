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

package com.exclamationlabs.connid.base.connector;

import com.exclamationlabs.connid.base.connector.adapter.*;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.DefaultAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.schema.ConnectorSchemaBuilder;
import com.exclamationlabs.connid.base.connector.schema.DefaultConnectorSchemaBuilder;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class for defining Identity Access Management connectors.
 * In order to with MidPoint or ConnId system, this class MUST be annotated
 * with org.identityconnectors.framework.spi.ConnectorClass, supplying
 * a displayNameKey and configurationClass. Example:
 *
 * {@literal @}ConnectorClass(displayNameKey = "test.display", configurationClass = StubConfiguration.class)
 *
 * The constructor for your concrete class should also call these setters...
 * MANDATORY:
 * setDriver();
 * setUsersAdapter();
 * setGroupsAdapter();
 * setUserAttributes();
 * setGroupAttributes();
 *
 * OPTIONAL:
 * setAuthenticator();
 * setConnectorSchemaBuilder();
 *
 */
public abstract class BaseConnector
        implements PoolableConnector, SchemaOp, TestOp {

    private static final Log LOG = Log.getLog(BaseConnector.class);

    protected Driver driver;
    protected ConnectorSchemaBuilder schemaBuilder;
    protected Authenticator authenticator;
    protected BaseConnectorConfiguration configuration;

    protected Map<ObjectClass, BaseAdapter<?>> adapterMap;

    public BaseConnector() {
        adapterMap = new HashMap<>();
    }

    protected abstract boolean readEnabled();

    protected abstract boolean updateEnabled();

    protected abstract boolean deleteEnabled();

    protected abstract boolean createEnabled();

    protected boolean isReadOnly() {
        return readEnabled() && !updateEnabled() && !deleteEnabled() && !createEnabled();
    }

    protected boolean isWriteOnly() {
        return !readEnabled() && updateEnabled() && deleteEnabled() && createEnabled();
    }

    protected boolean isCrud() {
        return readEnabled() && updateEnabled() && deleteEnabled() && createEnabled();
    }

    public void setAdapters(BaseAdapter<?>... adapters) {
        for (BaseAdapter<?> currentAdapter : adapters) {
            adapterMap.put(currentAdapter.getType(), currentAdapter);
        }
    }

    @Override
    public void checkAlive() {
        test();
    }


    /**
     * MidPoint calls this method to initialize a connector on startup.
     * @param configuration Configuration concrete class (Midpoint determines
     *                      this by looking at configurationClass of
     *                      {@literal @}ConnectorClass annotation on your
     *                      concrete connector class)
     */
    @Override
    public void init(Configuration configuration) {
        initializeBaseConnector(configuration);
    }

    /**
     * Required for ConnId Connector interface
     */
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void test() {
        driver.test();
    }

    @Override
    public void dispose() {
        driver.close();
    }

    @Override
    public Schema schema() {
        if (getConnectorSchemaBuilder() == null) {
            throw new ConfigurationException("SchemaBuilder not setup for this connector");
        }
        return getConnectorSchemaBuilder().build(this, adapterMap);
    }

    public String getName() {
        return getClass().getSimpleName();
    }



    protected FilterTranslator<String> getConnectorFilterTranslator(ObjectClass objectClass) {
        BaseAdapter<?> matchedAdapter = adapterMap.get(objectClass);
        if (matchedAdapter == null) {
            throw new ConnectorException("Unsupported object class for filter translator: " + objectClass);
        } else {
            return new DefaultFilterTranslator();
        }
    }

    protected void setConnectorSchemaBuilder(ConnectorSchemaBuilder input) {
        schemaBuilder = input;
    }

    ConnectorSchemaBuilder getConnectorSchemaBuilder() {
        if (schemaBuilder == null) {
            setConnectorSchemaBuilder(new DefaultConnectorSchemaBuilder());
        }
        return schemaBuilder;
    }

    protected void setAuthenticator(Authenticator in) {
        authenticator = in;
    }

    Authenticator getAuthenticator() {
        return authenticator;
    }

    private void setConnectorConfiguration(BaseConnectorConfiguration in) {
        configuration = in;
    }

    BaseConnectorConfiguration getConnectorConfiguration() {
        return configuration;
    }

    protected void setDriver(Driver input) {
        driver = input;
    }

    protected Driver getDriver() {
        return driver;
    }

    protected BaseAdapter<?> getAdapter(ObjectClass objectClass) {
        BaseAdapter<?> matchedAdapter = adapterMap.get(objectClass);

        if (matchedAdapter == null) {
            throw new ConnectorException("Adapter could not be resolved for connector " + getName() +
                    ", unsupported object class: " + objectClass);
        }

        return matchedAdapter;
    }

    protected void initializeBaseConnector(Configuration inputConfiguration) {

        if (getDriver() == null) {
            throw new ConfigurationException("Driver not setup for connector " + getName());
        }
        Set<ConnectorProperty> driverProperties = getDriver().getRequiredPropertyNames();

        if (getAuthenticator() == null) {
            LOG.info("No authenticator found, using default no-op Authenticator for Connector {0}, already validated", getName());
            setAuthenticator(new DefaultAuthenticator());
        } else {
            LOG.info("Using authenticator {0} for connector {1}", getAuthenticator().getClass().getName(),
                    this.getName());
        }
        Set<ConnectorProperty> authenticatorProperties = getAuthenticator().getRequiredPropertyNames();

        if (getConnectorConfiguration()==null) {
            LOG.info("Initializing Connector {0} ...", getName());

            if (inputConfiguration == null) {
                throw new ConfigurationException("Unable to find configuration for connector " + this.getName());
            }

            if (!(inputConfiguration instanceof BaseConnectorConfiguration)) {
                throw new ConfigurationException("Connector configuration does not use base framework " +
                        "for connector " + this.getName());
            }

            setConnectorConfiguration((BaseConnectorConfiguration) inputConfiguration);
            getConnectorConfiguration().setRequiredPropertyNames(authenticatorProperties, driverProperties);
        }

        LOG.info("Using configuration {0} for connector {1}", getConnectorConfiguration().getClass().getName(),
                this.getName());

        if (!getConnectorConfiguration().isValidated()) {
            getConnectorConfiguration().validate();
            LOG.info("Connector {0} successfully validated", this.getName());
        } else {
            LOG.info("Connector {0} validation bypassed, already validated", this.getName());
        }

        getConnectorConfiguration().setCredentialAccessToken(
                getAuthenticator().authenticate(getConnectorConfiguration()));
        LOG.info("Connector {0} successfully authenticated", this.getName());


        getDriver().initialize(getConnectorConfiguration(), getAuthenticator());
        LOG.info("Connector {0} driver {1} successfully initialized", this.getName(), getDriver().getClass().getName());

        if (adapterMap == null || adapterMap.isEmpty()) {
            throw new ConfigurationException("No adapters setup for connector " + this.getName() +
                    "; must have at least one");
        }

        for (BaseAdapter<?> adapter : adapterMap.values()) {
            adapter.setDriver(getDriver());
            LOG.info("Connector {0} adapter {1} successfully initialized", this.getName(),
                    adapter.getClass().getSimpleName());
        }

    }

}
