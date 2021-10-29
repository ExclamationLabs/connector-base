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
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationReader;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
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

import javax.validation.constraints.NotBlank;
import java.util.Collections;
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
 * In most cases, you should subclass one of these abstract classes instead of this one,
 * based on your connector's need:
 * BaseFullAccessConnector - full create/read/update/delete access to the destination system
 * BaseReadOnlyConnector - read-only access to the destination system
 * BaseWriteOnlyConnector - write-only access to the destination system
 *
 * The constructor for your concrete class should also call these setters...
 * MANDATORY:
 * setDriver();
 * setAdapters();
 *
 * OPTIONAL:
 * setAuthenticator();
 * setConnectorSchemaBuilder();
 * setEnhancedFiltering();
 * setFilterAttributes();
 *
 */
public abstract class BaseConnector<T extends ConnectorConfiguration>
        implements PoolableConnector, SchemaOp, TestOp {

    private static final Log LOG = Log.getLog(BaseConnector.class);

    public static final String FILTER_SEPARATOR = "^|^";

    @NotBlank
    protected Driver<T> driver;
    protected ConnectorSchemaBuilder<T> schemaBuilder;
    protected Authenticator<T> authenticator;
    protected T configuration;

    protected Map<ObjectClass, BaseAdapter<?, T>> adapterMap;

    protected boolean enhancedFiltering;
    protected Set<String> filterAttributes;

    protected final Class<T> configurationType;

    public BaseConnector(Class<T> configurationTypeIn) {
        adapterMap = new HashMap<>();
        filterAttributes = Collections.emptySet();
        configurationType = configurationTypeIn;
    }

    /**
     * Concrete Connector classes need to call this method to set the driver
     * for their connector implementation.
     * @param input Concrete Driver class used to communicate with the destination system.
     */
    protected void setDriver(Driver<T> input) {
        driver = input;
    }

    /**
     * Concrete Connector classes need to call this method to set the adapter(s)
     * to map attribute data from Midpoint to IdentityModel object types.
     * @param adapters Any number of concrete BaseAdapter classes, each pertaining to
     *                 a distinct IdentityModel type.
     */
    @SafeVarargs
    protected final void setAdapters(BaseAdapter<?, T>... adapters) {
        for (BaseAdapter<?, T> currentAdapter : adapters) {
            adapterMap.put(currentAdapter.getType(), currentAdapter);
        }
    }

    /**
     * Identifies whether or not this connector is able to read items on the destination system.
     * @return true if read on the destination system is allowed, false if it is prohibited.
     */
    protected abstract boolean readEnabled();

    /**
     * Identifies whether or not this connector is able to update items on the destination system.
     * @return true if update on the destination system is allowed, false if it is prohibited.
     */
    protected abstract boolean updateEnabled();

    /**
     * Identifies whether or not this connector is able to delete items on the destination system.
     * @return true if delete on the destination system is allowed, false if it is prohibited.
     */
    protected abstract boolean deleteEnabled();

    /**
     * Identifies whether or not this connector is able to create items on the destination system.
     * @return true if create on the destination system is allowed, false if it is prohibited.
     */
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
    public void init(Configuration configuration) throws ConfigurationException {
        if (configurationType.isInstance(configuration)) {
            initializeBaseConnector((T) configuration);
        } else {
            throw new ConfigurationException("Invalid configuration type for connector. Supplied type is: " +
                    configuration.getClass().getName() + ", required type is " + configurationType.getName());
        }
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
        BaseAdapter<?, T> matchedAdapter = adapterMap.get(objectClass);
        if (matchedAdapter == null) {
            throw new ConnectorException("Unsupported object class for filter translator: " + objectClass);
        } else {
            return new DefaultFilterTranslator(getFilterAttributes());
        }
    }

    public boolean isEnhancedFiltering() {
        return enhancedFiltering;
    }

    public void setEnhancedFiltering(boolean enhancedFiltering) {
        this.enhancedFiltering = enhancedFiltering;
    }

    public Set<String> getFilterAttributes() {
        return filterAttributes;
    }

    public void setFilterAttributes(Set<String> filterAttributes) {
        this.filterAttributes = filterAttributes;
    }

    protected void setConnectorSchemaBuilder(ConnectorSchemaBuilder<T> input) {
        schemaBuilder = input;
    }

    public ConnectorSchemaBuilder<T> getConnectorSchemaBuilder() {
        if (schemaBuilder == null) {
            setConnectorSchemaBuilder(new DefaultConnectorSchemaBuilder<>());
        }
        return schemaBuilder;
    }

    protected void setAuthenticator(Authenticator<T> in) {
        authenticator = in;
    }

    public Authenticator<T> getAuthenticator() {
        return authenticator;
    }

    private void setConnectorConfiguration(T in) {
        configuration = in;
    }

    public T getConnectorConfiguration() {
        return configuration;
    }

    protected Driver<T> getDriver() {
        return driver;
    }

    protected BaseAdapter<?, T> getAdapter(ObjectClass objectClass) {
        BaseAdapter<?, T> matchedAdapter = adapterMap.get(objectClass);

        if (matchedAdapter == null) {
            throw new ConnectorException("Adapter could not be resolved for connector " + getName() +
                    ", unsupported object class: " + objectClass);
        }

        return matchedAdapter;
    }

    /**
     * This initialization method performs checks to ensure the concrete connector
     * implementation is structured properly and has what it needs to successfully integrate
     * with Midpoint.  If something is not setup properly, the RuntimeException
     * ConfigurationException is thrown from this method to indicate the problem.
     * @param inputConfiguration Configuration object for this connector.
     */
    protected void initializeBaseConnector(T inputConfiguration) throws ConfigurationException {

        if (getDriver() == null) {
            throw new ConfigurationException("Driver not setup for connector " + getName());
        }

        if (getAuthenticator() == null) {
            LOG.info("No authenticator found, using default no-op Authenticator for Connector {0}, already validated", getName());
            setAuthenticator((Authenticator<T>) new DefaultAuthenticator());
        } else {
            LOG.info("Using authenticator {0} for connector {1}", getAuthenticator().getClass().getName(),
                    this.getName());
        }

        if (getConnectorConfiguration()==null) {
            LOG.info("Initializing Connector {0} ...", getName());

            if (inputConfiguration == null) {
                throw new ConfigurationException("Unable to find configuration for connector " + this.getName());
            }

            setConnectorConfiguration(inputConfiguration);
        }

        LOG.info("Using configuration class {0} for connector {1}, configuration name {2}", getConnectorConfiguration().getClass().getName(),
                this.getName(), getConnectorConfiguration().getName());

        if (getConnectorConfiguration().isTestConfiguration()) {
            LOG.info("Test configuration detected, loading configuration properties from file ...");
            ConfigurationReader.readPropertiesFromSource(getConnectorConfiguration());
        }

        getConnectorConfiguration().validate();
        LOG.info("Connector {0} configuration {1} {2} successfully validated", this.getName(),
                getConnectorConfiguration().getClass().getSimpleName(),
                getConnectorConfiguration().getName());

        LOG.info("Connector {0} attempt Authentication using {1} and load current token ...",
                this.getName(), getAuthenticator().getClass().getSimpleName());
        getConnectorConfiguration().setCurrentToken(
                getAuthenticator().authenticate(getConnectorConfiguration()));

        LOG.info("Connector {0} successfully authenticated", this.getName());

        getDriver().initialize(getConnectorConfiguration(), getAuthenticator());
        LOG.info("Connector {0} driver {1} successfully initialized", this.getName(), getDriver().getClass().getName());

        if (adapterMap == null || adapterMap.isEmpty()) {
            throw new ConfigurationException("No adapters setup for connector " + this.getName() +
                    "; must have at least one");
        }

        for (BaseAdapter<?, T> adapter : adapterMap.values()) {
            adapter.setDriver(getDriver());
            adapter.setConfiguration(configuration);
            LOG.info("Connector {0} adapter {1} successfully initialized", this.getName(),
                    adapter.getClass().getSimpleName());
        }

    }

}
