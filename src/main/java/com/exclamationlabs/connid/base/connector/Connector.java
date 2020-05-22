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
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.*;

import java.util.Set;

public interface Connector<U extends UserIdentityModel,G extends GroupIdentityModel> extends PoolableConnector, SchemaOp, DeleteOp, CreateOp, UpdateOp, SearchOp<String>, TestOp {


    /**
     * Convenience method needed for unit testing connector initialization.
     * MidPoint will normally invoke init(Configuration).
     */
    void init();

    default FilterTranslator<String> getConnectorFilterTranslator() {
        return new DefaultFilterTranslator();
    }

    default String getName() {
        return getClass().getSimpleName();
    }

    Adapter<U,G> getAdapter(ObjectClass objectClass);

    @Override
    default FilterTranslator<String> createFilterTranslator(ObjectClass objectClass, OperationOptions operationOptions) {
        if (objectClass.is(ObjectClass.ACCOUNT_NAME) || objectClass.is(ObjectClass.GROUP_NAME)) {
            return getConnectorFilterTranslator();
        } else {
            throw new ConnectorException("Unsupported object class for filter translator: " + objectClass);
        }
    }

    @Override
    default void checkAlive() {
        test();
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

    void initializeBaseConnector(Configuration configuration);

    /**
     * MidPoint calls this method to initialize a connector.  It has
     * some way of looking up the Configuration class (probably by inspecting the
     * package).  So need to be very careful to have 1 (and exactly 1) concrete
     * Configuration class per Connector project.
     * @param configuration Configuration concrete class found by Midpoint at runtime.
     */
    @Override
    default void init(Configuration configuration) {
        initializeBaseConnector(configuration);
    }


}
