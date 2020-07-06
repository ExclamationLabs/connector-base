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

package com.exclamationlabs.connid.base.connector.schema;

import com.exclamationlabs.connid.base.connector.BaseConnector;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.Schema;

import java.util.EnumMap;

/**
 * Interface for building a ConnId schema using the base connector framework.
 */
public interface ConnectorSchemaBuilder<U extends UserIdentityModel,G extends GroupIdentityModel> {

    /**
     * Build and define the schema, capturing all the Attribute definitions for user
     * and group types for this connector.
     * @return Schema object needed for ConnId identity management system (normally MidPoint)
     * @throws ConfigurationException if exception or failure occurred while trying
     * to read or construct the schema.
     */
    Schema build(BaseConnector<U,G> connector, EnumMap<?, ConnectorAttribute> userAttributes,
                 EnumMap<?, ConnectorAttribute> groupAttributes) throws ConfigurationException;
}
