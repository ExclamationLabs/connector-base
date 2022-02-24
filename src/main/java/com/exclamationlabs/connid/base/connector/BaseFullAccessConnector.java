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

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.AttributeFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.operations.*;

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
public abstract class BaseFullAccessConnector<T extends ConnectorConfiguration> extends BaseConnector<T>
    implements DeleteOp, CreateOp, UpdateDeltaOp, SearchOp<AttributeFilter> {

    public BaseFullAccessConnector(Class<T> configurationTypeIn) {
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
    public Set<AttributeDelta> updateDelta(final ObjectClass objectClass, final Uid uid,
                           final Set<AttributeDelta> attributeModifications,
                                           final OperationOptions options) {
        return getAdapter(objectClass).updateDelta(uid, attributeModifications);
    }

    @Override
    public void delete(final ObjectClass objectClass, final Uid uid, final OperationOptions options) {
        getAdapter(objectClass).delete(uid);
    }

    @Override
    public FilterTranslator<AttributeFilter> createFilterTranslator(ObjectClass objectClass, OperationOptions operationOptions) {
        return getConnectorFilterTranslator(objectClass);
    }

    @Override
    public void executeQuery(final ObjectClass objectClass, final AttributeFilter queryFilter, final ResultsHandler resultsHandler, final OperationOptions operationOptions) {
        getAdapter(objectClass).get(queryFilter, resultsHandler, operationOptions, isEnhancedFiltering());
    }

    @Override
    public void executeQuery(final ObjectClass objectClass, String itemId, final ResultsHandler resultsHandler, final OperationOptions operationOptions) {
        if (StringUtils.isEmpty(itemId)) {
            getAdapter(objectClass).get(null, resultsHandler, operationOptions, isEnhancedFiltering());
        } else {
            Attribute attribute = new AttributeBuilder().setName(Uid.NAME).addValue(itemId).build();
            AttributeFilter queryFilter = new EqualsFilter(attribute);
            getAdapter(objectClass).get(queryFilter, resultsHandler, operationOptions, isEnhancedFiltering());
        }
   }

}
