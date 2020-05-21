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

import com.exclamationlabs.connid.base.connector.Connector;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.*;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;

/**
 * Default Connector Schema builder for Base Connector framework.  This will likely
 * fit the needs for all future connectors developed; but if a custom solution is needed,
 * use BaseConnector setConnectorSchemaBuilder()
 */
public class DefaultConnectorSchemaBuilder<U extends UserIdentityModel, G extends GroupIdentityModel>
        implements ConnectorSchemaBuilder<U,G> {

    private static final Log LOG = Log.getLog(Connector.class);

    @Override
    public Schema build(Connector<U,G> connector) throws ConfigurationException {
        LOG.info("Building schema for connector {0} ...", connector.getName());
        SchemaBuilder schemaBuilder = new SchemaBuilder(connector.getClass());

        LOG.info("Determining user schema elements for connector {0} ...", connector.getName());
        schemaBuilder.defineObjectClass(buildObjectClassInfo(ObjectClass.ACCOUNT_NAME,
                connector.getUserAttributes()));

        LOG.info("Determining group schema elements for connector {0} ...", connector.getName());
        schemaBuilder.defineObjectClass(buildObjectClassInfo(ObjectClass.GROUP_NAME,
                connector.getGroupAttributes()));

        LOG.info("Finished building schema for connector {0}", connector.getName());

        return schemaBuilder.build();
    }

    protected static ObjectClassInfo buildObjectClassInfo(String classType,
                                                          EnumMap<?, ConnectorAttribute> attributes) {
        ObjectClassInfoBuilder builder = new ObjectClassInfoBuilder();
        builder.setType(classType);

        Collection<AttributeInfo> attributeInfoSet = new HashSet<>();

        for (ConnectorAttribute current : attributes.values()) {
            AttributeInfoBuilder attributeInfo = new AttributeInfoBuilder(current.getName());
            attributeInfo.setType(current.getDataType().getClassType());
            attributeInfo.setFlags(current.getFlags());
            attributeInfoSet.add(attributeInfo.build());
            LOG.info("Current schema element for attribute: {0}; {1}; {2}", current.getName(),
                    current.getDataType().getClassType(),current.getFlags());
        }
        builder.addAllAttributeInfo(attributeInfoSet);
        LOG.info("Added {0} schema elements", attributeInfoSet.size());

        return builder.build();
    }
}
