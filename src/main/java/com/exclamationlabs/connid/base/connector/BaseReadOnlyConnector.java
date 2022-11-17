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
import org.identityconnectors.framework.spi.operations.SearchOp;

/**
 * Connector that allows read-only access on
 * a destination system.  This class should be subclassed if your primary desire is to have
 * just read-only access to the data types on the destination system.
 *
 * Note that only the ConnId SearchOp&lt;String&gt; interface is implemented for this class;
 * therefore this connector cannot take in create/update/delete requests from Midpoint.
 */
public abstract class BaseReadOnlyConnector<T extends ConnectorConfiguration> extends BaseConnector<T>
    implements SearchOp<AttributeFilter> {

    public BaseReadOnlyConnector(Class<T> configurationTypeIn) {
        super(configurationTypeIn);
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

    @Override
    protected boolean readEnabled() {
        return true;
    }

    @Override
    protected boolean updateEnabled() {
        return false;
    }

    @Override
    protected boolean deleteEnabled() {
        return false;
    }

    @Override
    protected boolean createEnabled() {
        return false;
    }

}
