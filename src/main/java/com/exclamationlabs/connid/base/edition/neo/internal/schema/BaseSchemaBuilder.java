package com.exclamationlabs.connid.base.edition.neo.internal.schema;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.ResultsConfiguration;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.internal.BaseConnectorTypeFactory;
import com.exclamationlabs.connid.base.edition.neo.internal.IdentityModelAccess;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.SyncOp;

public class BaseSchemaBuilder<T extends ConnectorConfiguration> {

  public Schema build(
          final BaseConnector<T> connector,
          final T configuration,
          final BaseConnectorTypeFactory<T> connectorTypeFactory
  ) {
    Logger.info(this, String.format("Building schema for connector %s ...", connector.getName()));
    var accessMap =
            connectorTypeFactory.getIdentityModelAccessMap();

    var connIdSchemaBuilder = new SchemaBuilder(connector.getClass());

    for (var identityObjectClass : accessMap.keySet()) {
      var identityModelAccess = accessMap.get(identityObjectClass);
      Logger.info(
              this,
              String.format(
                      "Begin scanning schema elements for model %s, object class %s ...",
                      identityModelAccess.getIdentityModelClass().getSimpleName(), identityObjectClass.getObjectClassValue()));
      connIdSchemaBuilder.defineObjectClass(buildObjectClassInfo(identityObjectClass, identityModelAccess));
    }
    if (configuration instanceof ResultsConfiguration) {
      Logger.trace(
              this,
              String.format(
                      "Setup Schema OperationOptions for paging capability for %s ...",
                      connector.getName()));
      setupOperationOptionsPagingDefinitions(connIdSchemaBuilder);
    }

    return connIdSchemaBuilder.build();
  }
  private static ObjectClassInfo buildObjectClassInfo(
          final ObjectClass objectClass, final IdentityModelAccess identityModelAccess) {
    var builder = new ObjectClassInfoBuilder();
    builder.setType(objectClass.getObjectClassValue());

    int total = scanClassForModelAttributes(identityModelAccess, builder);
    // TODO: support list/collection child types and assignment identifiers for model!

    Logger.info(
        BaseSchemaBuilder.class,
        String.format(
            "Completed scanning for object class %s. %d attributes registered.",
                objectClass.getObjectClassValue(), total));

    return builder.build();
  }

  private static int scanClassForModelAttributes(
      IdentityModelAccess identityModelAccess, ObjectClassInfoBuilder builder) {
    // scan model for all attribute info for schema
    var attributeInfoSet = new HashSet<AttributeInfo>(); // use set to avoid duplicates

    for (var attributeName : identityModelAccess.getFieldAccessInfoMap().keySet()) {
        var fieldAccessInfo = identityModelAccess.getFieldAccessInfoMap().get(attributeName);
        String nativeName;
        switch (fieldAccessInfo.getIdentifier()) {
          case UID:
            attributeName = Uid.NAME;
            nativeName = attributeName;
            break;
          case NAME:
            attributeName = Name.NAME;
            nativeName = attributeName;
            break;
          default:
            nativeName =
                StringUtils.defaultIfBlank(fieldAccessInfo.getNativeName(), attributeName);
            break;
        }
        var attributeInfo =
            new AttributeInfoBuilder(attributeName)
                .setNativeName(nativeName)
                .setType(fieldAccessInfo.getDataType().getClassType())
                .setSubtype(
                    StringUtils.trimToNull(fieldAccessInfo.getMetaInfoJson()))
                .setFlags(
                        fieldAccessInfo.getFlags().length > 0
                        ? Set.of(fieldAccessInfo.getFlags())
                        : Collections.emptySet())
                .build();
        Logger.info(
            BaseSchemaBuilder.class,
            String.format(
                "Attribute %s successfully registered for object class %s.",
                attributeInfo.getName(), identityModelAccess.getIdentityModelClass().getSimpleName()));
        Logger.trace(
            BaseSchemaBuilder.class,
            String.format(
                "Attribute %s attribute schema (XML form) is: %s",
                attributeInfo.getName(), attributeInfo));
        attributeInfoSet.add(attributeInfo);
    }

    if (!attributeInfoSet.isEmpty()) {
      builder.addAllAttributeInfo(attributeInfoSet);
    }

    return attributeInfoSet.size();
  }

  private static void setupOperationOptionsPagingDefinitions(SchemaBuilder schemaBuilder) {
    schemaBuilder.defineOperationOption(
        OperationOptionInfoBuilder.buildPageSize(), SyncOp.class, SearchOp.class);
    schemaBuilder.defineOperationOption(
        OperationOptionInfoBuilder.buildAttributesToGet(), SyncOp.class, SearchOp.class);
    schemaBuilder.defineOperationOption(
        OperationOptionInfoBuilder.buildReturnDefaultAttributes(), SearchOp.class, SyncOp.class);

    // more operation options support for Paging
    schemaBuilder.defineOperationOption(
        OperationOptionInfoBuilder.buildPagedResultsOffset(), SearchOp.class);
    schemaBuilder.defineOperationOption(
        OperationOptionInfoBuilder.buildPagedResultsCookie(), SearchOp.class);
    schemaBuilder.defineOperationOption(
        OperationOptionInfoBuilder.buildAllowPartialResults(), SearchOp.class);
    schemaBuilder.defineOperationOption(
        OperationOptionInfoBuilder.buildAllowPartialAttributeValues(), SearchOp.class);
  }
}
