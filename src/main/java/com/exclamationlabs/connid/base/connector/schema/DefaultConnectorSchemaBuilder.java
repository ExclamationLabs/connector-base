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
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.*;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;

public class DefaultConnectorSchemaBuilder<U extends UserIdentityModel, G extends GroupIdentityModel>
        implements ConnectorSchemaBuilder<U,G> {

    @Override
    public Schema build(Connector<U,G> connector) throws ConfigurationException {
        SchemaBuilder schemaBuilder = new SchemaBuilder(connector.getClass());

        schemaBuilder.defineObjectClass(buildObjectClassInfo(ObjectClass.ACCOUNT_NAME,
                connector.getUserAttributes()));

        schemaBuilder.defineObjectClass(buildObjectClassInfo(ObjectClass.GROUP_NAME,
                connector.getGroupAttributes()));

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
        }
        builder.addAllAttributeInfo(attributeInfoSet);

        return builder.build();
    }
}
