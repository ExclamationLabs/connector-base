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

import com.exclamationlabs.connid.base.connector.adapter.Adapter;
import com.exclamationlabs.connid.base.connector.adapter.GroupsAdapter;
import com.exclamationlabs.connid.base.connector.adapter.UsersAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import com.exclamationlabs.connid.base.connector.schema.ConnectorSchemaBuilder;
import com.exclamationlabs.connid.base.connector.driver.Driver;
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

public interface Connector<U extends UserIdentityModel,G extends GroupIdentityModel> extends PoolableConnector, SchemaOp, DeleteOp, CreateOp, UpdateOp, SearchOp<String>, TestOp {

    Log LOG = Log.getLog(Connector.class);

    EnumMap<?, ConnectorAttribute> getUserAttributes();

    EnumMap<?, ConnectorAttribute> getGroupAttributes();

    ConnectorSchemaBuilder<U,G> getConnectorSchemaBuilder();
    void setConnectorSchemaBuilder(ConnectorSchemaBuilder<U,G> input);

    UsersAdapter<U,G> getUsersAdapter();

    GroupsAdapter<U,G> getGroupsAdapter();

    Driver<U,G> getDriver();

    Authenticator getAuthenticator();
    void setAuthenticator(Authenticator in);

    ConnectorConfiguration getConnectorConfiguration();

    default FilterTranslator<String> getConnectorFilterTranslator() {
        return new DefaultFilterTranslator();
    }

    default String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Convenience method for unit testing connector initialization.
     */
    default void init() {
        init(getConnectorConfiguration());
    }

    @Override
    default Configuration getConfiguration() {
        return getConnectorConfiguration();
    }

    @Override
    default Schema schema() {
        if (getConnectorSchemaBuilder() == null) {
            throw new ConfigurationException("SchemaBuilder not setup for this connector");
        }
        return getConnectorSchemaBuilder().build(this);
    }

    @Override
    default FilterTranslator<String> createFilterTranslator(ObjectClass objectClass, OperationOptions operationOptions) {
        if (objectClass.is(ObjectClass.ACCOUNT_NAME) || objectClass.is(ObjectClass.GROUP_NAME)) {
            return getConnectorFilterTranslator();
        } else {
            throw new ConnectorException("Unsupported object class for filter translator: " + objectClass);
        }
    }

    /**
     * MidPoint calls this method to initialize a connector.  It has
     * some way of looking up the Configuration class (probably by inspecting the
     * package).  So need to be very careful to have 1 (and exactly 1) concrete
     * Configuration class per Connector project.
     * @param configuration Configuration concrete class found by Midpoint at runtime.
     */
    @Override
    default void init(Configuration configuration) {
        LOG.info("Initializing Connector {0} ...", getName());
        if (! (this instanceof BaseConnector)) {
            throw new ConfigurationException("Connector does not inherit BaseConnector: " + this.getName());
        }

        if (configuration == null) {
            throw new ConfigurationException("Unable to find configuration for connector " + this.getName());
        }

        if (!(configuration instanceof BaseConnectorConfiguration)) {
            throw new ConfigurationException("Connector configuration does not use base framework " +
                    "for connector " + this.getName());
        }

        ConnectorConfiguration connectorConfiguration = (ConnectorConfiguration) configuration;
        LOG.info("Using configuration {0} for connector {1}", connectorConfiguration.getClass().getName(),
                this.getName());

        if (!connectorConfiguration.isValidated()) {
            connectorConfiguration.validate();
            LOG.info("Connector {0} successfully validated", this.getName());
        } else {
            LOG.info("Connector {0} validation bypassed, already validated", this.getName());
        }

        if (getAuthenticator() == null) {
            LOG.info("No authenticator found, using default no-op Authenticator for Connector {0}ed, already validated", connectorConfiguration.getName());
            setAuthenticator(authenticatorConfiguration -> "NA");
        } else {
            LOG.info("Using authenticator {0} for connector {1}", getAuthenticator().getClass().getName(),
                    this.getName());
        }
        getAuthenticator().authenticate(getConnectorConfiguration());
        LOG.info("Connector {0} successfully authenticated", this.getName());

        if (getDriver() == null) {
            throw new ConfigurationException("Driver not setup for connector " + this.getName());
        }
        getDriver().initialize(getConnectorConfiguration(), getAuthenticator());
        LOG.info("Connector {0} driver successfully initialized", this.getName());

        if (getUsersAdapter() == null) {
            throw new ConfigurationException("UsersAdapter not setup for connector " + this.getName());
        }
        getUsersAdapter().setDriver(getDriver());

        if (getGroupsAdapter() == null) {
            throw new ConfigurationException("GroupsAdapter not setup for connector " + this.getName());
        }
        getGroupsAdapter().setDriver(getDriver());
    }

    @Override
    default void checkAlive() {
        test();
    }

    @Override
    default void test() {
        getDriver().test();
    }

    @Override
    default void dispose() {
        getDriver().close();
    }

    @Override
    default void executeQuery(final ObjectClass objectClass, final String query, final ResultsHandler resultsHandler, final OperationOptions operationOptions) {
        getAdapter(objectClass).get(query, resultsHandler);
    }

    @Override
    default Uid create(final ObjectClass objectClass, final Set<Attribute> attributes, final OperationOptions options) {
        return getAdapter(objectClass).create(attributes);
    }

    @Override
    default Uid update(final ObjectClass objectClass, final Uid uid, final Set<Attribute> attributes, final OperationOptions options) {
         return getAdapter(objectClass).update(uid, attributes);
    }

    @Override
    default void delete(final ObjectClass objectClass, final Uid uid, final OperationOptions options) {
         getAdapter(objectClass).delete(uid);
    }

    default Adapter<U,G> getAdapter(ObjectClass objectClass) {
        if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
            return getUsersAdapter();
        } else if (objectClass.is(ObjectClass.GROUP_NAME)) {
            return getGroupsAdapter();
        } else {
            throw new ConnectorException("Adapter resolution, unsupported object class: " + objectClass);
        }
    }

}
