package com.exclamationlabs.connid.base.connector.schema;

import com.exclamationlabs.connid.base.connector.Connector;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.*;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;

public class DefaultConnectorSchemaBuilder implements ConnectorSchemaBuilder {

    @Override
    public Schema build(Connector connector) throws ConfigurationException {
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
