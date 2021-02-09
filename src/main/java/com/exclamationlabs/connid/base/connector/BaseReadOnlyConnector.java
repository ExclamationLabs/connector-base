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

import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.operations.SearchOp;

/**
 * Connector that allows read-only access on
 * a destination system.  This class should be subclassed if your primary desire is to have
 * just read-only access to the data types on the destination system.
 *
 * Note that only the ConnId SearchOp<String> interface is implemented for this class;
 * therefore this connector cannot take in create/update/delete requests from Midpoint.
 */
public abstract class BaseReadOnlyConnector extends BaseConnector
    implements SearchOp<String> {

    @Override
    public FilterTranslator<String> createFilterTranslator(ObjectClass objectClass, OperationOptions operationOptions) {
        return getConnectorFilterTranslator(objectClass);
    }

    @Override
    public void executeQuery(final ObjectClass objectClass, final String query, final ResultsHandler resultsHandler, final OperationOptions operationOptions) {
        getAdapter(objectClass).get(query, resultsHandler, operationOptions);
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
