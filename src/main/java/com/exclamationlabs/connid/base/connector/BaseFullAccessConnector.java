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

import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;

import java.util.Set;

/**
 * Connector that allows full access (create, read, update and delete) on
 * a destination system.  This class should be subclassed if your primary desire is to have
 * full access on most/all of the data types on the destination system.
 *
 * Note that ConnId interfaces DeleteOp, CreateOp, UpdateOp and SearchOp<String>
 * are implemented.  This means that this connector will be able to
 * receive create, update, delete and get/search requests from Midpoint.
 */
public abstract class BaseFullAccessConnector extends BaseConnector
    implements DeleteOp, CreateOp, UpdateOp, SearchOp<String> {

    public BaseFullAccessConnector(Class<?> configurationTypeIn) {
        super(configurationTypeIn);
    }

    @Override
    protected boolean readEnabled() {
        return true;
    }

    @Override
    protected boolean updateEnabled() {
        return true;
    }

    @Override
    protected boolean deleteEnabled() {
        return true;
    }

    @Override
    protected boolean createEnabled() {
        return true;
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

    @Override
    public FilterTranslator<String> createFilterTranslator(ObjectClass objectClass, OperationOptions operationOptions) {
        return getConnectorFilterTranslator(objectClass);
    }

    @Override
    public void executeQuery(final ObjectClass objectClass, final String query, final ResultsHandler resultsHandler, final OperationOptions operationOptions) {
        getAdapter(objectClass).get(query, resultsHandler, operationOptions, isEnhancedFiltering());
    }
}
