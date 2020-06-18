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
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.DefaultAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
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

import java.util.EnumMap;
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
 * @param <U> UserIdentityModel implementation relating to your User model type
 * @param <G> GroupIdentityModel implementation relating to your Group model type
 */
public abstract class BaseConnector<U extends UserIdentityModel, G extends GroupIdentityModel>
        implements PoolableConnector, SchemaOp, DeleteOp, CreateOp, UpdateOp, SearchOp<String>, TestOp {

    private static final Log LOG = Log.getLog(BaseConnector.class);

    protected Driver<U,G> driver;
    protected ConnectorSchemaBuilder<U,G> schemaBuilder;
    protected BaseUsersAdapter<U,G> usersAdapter;
    protected BaseGroupsAdapter<U,G> groupsAdapter;
    protected Authenticator authenticator;
    protected BaseConnectorConfiguration configuration;

    protected EnumMap<?, ConnectorAttribute> userAttributes;
    protected EnumMap<?, ConnectorAttribute> groupAttributes;

    @Override
    public FilterTranslator<String> createFilterTranslator(ObjectClass objectClass, OperationOptions operationOptions) {
        if (objectClass.is(ObjectClass.ACCOUNT_NAME) || objectClass.is(ObjectClass.GROUP_NAME)) {
            return getConnectorFilterTranslator();
        } else {
            throw new ConnectorException("Unsupported object class for filter translator: " + objectClass);
        }
    }

    @Override
    public void checkAlive() {
        test();
    }

    @Override
    public void executeQuery(final ObjectClass objectClass, final String query, final ResultsHandler resultsHandler, final OperationOptions operationOptions) {
        getAdapter(objectClass).get(query, resultsHandler);
    }

    @Override
    public Uid create(final ObjectClass objectClass, final Set<Attribute> attributes, final OperationOptions options) {
        return getAdapter(objectClass).create(attributes);
    }

    @Override
    public Uid update(final ObjectClass objectClass, final Uid uid, final Set<Attribute> attributes, final OperationOptions options) {
        return getAdapter(objectClass).update(uid, attributes);
    }

    @Override
    public void delete(final ObjectClass objectClass, final Uid uid, final OperationOptions options) {
        getAdapter(objectClass).delete(uid);
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
        return getConnectorSchemaBuilder().build(this, userAttributes, groupAttributes);
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    protected FilterTranslator<String> getConnectorFilterTranslator() {
        return new DefaultFilterTranslator();
    }

    protected void setConnectorSchemaBuilder(ConnectorSchemaBuilder<U,G> input) {
        schemaBuilder = input;
    }

    ConnectorSchemaBuilder<U,G> getConnectorSchemaBuilder() {
        if (schemaBuilder == null) {
            setConnectorSchemaBuilder(new DefaultConnectorSchemaBuilder<>());
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

    protected void setUserAttributes(EnumMap<?, ConnectorAttribute> input) {
        userAttributes = input;
    }

    EnumMap<?, ConnectorAttribute> getUserAttributes() {
        return userAttributes;
    }

    protected void setGroupAttributes(EnumMap<?, ConnectorAttribute> input) {
        groupAttributes = input;
    }

    EnumMap<?, ConnectorAttribute> getGroupAttributes() {
        return groupAttributes;
    }

    protected void setDriver(Driver<U, G> input) {
        driver = input;
    }

    Driver<U, G> getDriver() {
        return driver;
    }

    protected void setUsersAdapter(BaseUsersAdapter<U, G> input) {
        usersAdapter = input;
    }

    BaseUsersAdapter<U, G> getUsersAdapter() {
        return usersAdapter;
    }

    protected void setGroupsAdapter(BaseGroupsAdapter<U, G> input) {
        groupsAdapter = input;
    }

    BaseGroupsAdapter<U, G> getGroupsAdapter() {
        return groupsAdapter;
    }

    protected BaseAdapter<U,G> getAdapter(ObjectClass objectClass) {
        if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
            return getUsersAdapter();
        } else if (objectClass.is(ObjectClass.GROUP_NAME)) {
            return getGroupsAdapter();
        } else {
            throw new ConnectorException("Adapter could not be resolved for connector " + getName() +
                    ", unsupported object class: " + objectClass);
        }
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

        if (getUsersAdapter() == null) {
            throw new ConfigurationException("UsersAdapter not setup for connector " + this.getName());
        }
        getUsersAdapter().setDriver(getDriver());
        LOG.info("Connector {0} usersAdapter {1} successfully initialized", this.getName(), getUsersAdapter().getClass().getName());

        if (getGroupsAdapter() == null) {
            throw new ConfigurationException("GroupsAdapter not setup for connector " + this.getName());
        }
        getGroupsAdapter().setDriver(getDriver());

        LOG.info("Connector {0} groupsAdapter {1} successfully initialized", this.getName(), getGroupsAdapter().getClass().getName());

    }

}
