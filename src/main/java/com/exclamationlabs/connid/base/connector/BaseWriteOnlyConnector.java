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
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.UpdateDeltaOp;

import java.util.Set;

/**
 * Connector that allows write-only access on
 * a destination system.  This class should be subclassed if your primary desire is to have
 * just write-only access to the data types on the destination system.
 *
 * Note that only the ConnId SearchOp<String> interface is not included for this class;
 * therefore this connector cannot take in get/search requests for data from Midpoint, and only
 * create/update/delete requests are recognized.
 */
public abstract class BaseWriteOnlyConnector<T extends ConnectorConfiguration> extends BaseConnector<T>
    implements DeleteOp, CreateOp, UpdateDeltaOp {

    public BaseWriteOnlyConnector(Class<T> configurationTypeIn) {
        super(configurationTypeIn);
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
    protected boolean readEnabled() {
        return false;
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

}
