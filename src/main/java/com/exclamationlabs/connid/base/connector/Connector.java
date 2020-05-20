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
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
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

public interface Connector<T,U,G> extends PoolableConnector, SchemaOp, DeleteOp, CreateOp, UpdateOp, SearchOp<String>, TestOp {

    Log LOG = Log.getLog(Connector.class);

    String getName();

    EnumMap<?, ConnectorAttribute> getUserAttributes();

    EnumMap<?, ConnectorAttribute> getGroupAttributes();

    ConnectorSchemaBuilder getConnectorSchemaBuilder();

    FilterTranslator<String> getConnectorFilterTranslator();

    Adapter<T,U,G> getUsersAdapter();

    Adapter<T,U,G> getGroupsAdapter();

    Driver<U,G> getDriver();

    Authenticator getAuthenticator();

    ConnectorConfiguration getConnectorConfiguration();

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

    @Override
    default void init(Configuration configuration) {
        LOG.info("Initializing Connector {0}", getName());
        if (configuration == null) {
            throw new ConfigurationException("Unable to find connector configuration");
        }

        if (!(configuration instanceof ConnectorConfiguration)) {
            throw new ConfigurationException("Connector configuration does not adhere to base framework.");
        }

        ConnectorConfiguration connectorConfiguration = (ConnectorConfiguration) configuration;

        if (!connectorConfiguration.isValidated()) {
            connectorConfiguration.validate();
            LOG.info("Connector {0} successfully validated", connectorConfiguration.getName());
        } else {
            LOG.info("Connector {0} validation bypassed, already validated", connectorConfiguration.getName());
        }

        if (getAuthenticator() == null) {
            throw new ConfigurationException("Authenticator not setup for this connector");
        }
        getAuthenticator().authenticate(getConnectorConfiguration());
        LOG.info("Connector {0} successfully authenticated", connectorConfiguration.getName());

        if (getDriver() == null) {
            throw new ConfigurationException("Driver not setup for this connector");
        }
        getDriver().initialize(getConnectorConfiguration(), getAuthenticator());
        LOG.info("Connector {0} driver successfully initialized", connectorConfiguration.getName());

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

    default Adapter<T,U,G> getAdapter(ObjectClass objectClass) {
        if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
            return getUsersAdapter();
        } else if (objectClass.is(ObjectClass.GROUP_NAME)) {
            return getGroupsAdapter();
        } else {
            throw new ConnectorException("Adapter resolution, unsupported object class: " + objectClass);
        }
    }

}
