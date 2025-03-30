package com.exclamationlabs.connid.base.edition.neo.internal;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.ResultsConfiguration;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttributeHolder;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelObjectClass;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import java.util.Collection;
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
      final Collection<Class<? extends IdentityModel>> identityModelSet) {
    Logger.info(this, String.format("Building schema for connector %s ...", connector.getName()));

    var connIdSchemaBuilder = new SchemaBuilder(connector.getClass());

    for (var identityModelClass : identityModelSet) {
      var objectClass =
          new ObjectClass(identityModelClass.getAnnotation(ModelObjectClass.class).value());
      Logger.info(
          this,
          String.format(
              "Begin scanning schema elements for model %s, object class %s ...",
              identityModelSet.getClass().getSimpleName(), objectClass.getObjectClassValue()));
      connIdSchemaBuilder.defineObjectClass(buildObjectClassInfo(objectClass, identityModelClass));
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
      final ObjectClass objectClass, final Class<? extends IdentityModel> identityModelClass) {
    var builder = new ObjectClassInfoBuilder();
    builder.setType(objectClass.getObjectClassValue());

    int total = scanClassForModelAttributes(identityModelClass, builder);
    // TODO: support collections and nesting for model!

    Logger.info(
        BaseSchemaBuilder.class,
        String.format(
            "Completed scanning for object class %s. %d attributes registered.",
            identityModelClass.getSimpleName(), total));

    return builder.build();
  }

  private static int scanClassForModelAttributes(
      Class<?> identityModelClass, ObjectClassInfoBuilder builder) {
    // scan model for all attribute info for schema
    int attributeCount = 0;
    var attributeInfoSet = new HashSet<AttributeInfo>();
    for (var field : identityModelClass.getDeclaredFields()) {
      var modelAttribute = field.getAnnotation(ModelAttribute.class);
      if (modelAttribute != null) {
        final var definedName =
            StringUtils.isNoneBlank(modelAttribute.value())
                ? modelAttribute.value()
                : field.getName();
        String attributeName, nativeName;
        switch (modelAttribute.identifier()) {
          case UID:
            attributeName = Uid.NAME;
            nativeName = definedName;
            break;
          case NAME:
            attributeName = Name.NAME;
            nativeName = definedName;
            break;
          default:
            attributeName = definedName;
            nativeName =
                StringUtils.isNoneBlank(modelAttribute.nativeName())
                    ? modelAttribute.nativeName()
                    : attributeName;
            break;
        }
        var attributeInfo =
            new AttributeInfoBuilder(attributeName)
                .setNativeName(nativeName)
                .setType(modelAttribute.type().getClassType())
                .setSubtype(
                    StringUtils.isNotBlank(modelAttribute.metaInfoJson())
                        ? modelAttribute.metaInfoJson()
                        : null)
                .setFlags(
                    modelAttribute.flags().length > 0
                        ? Set.of(modelAttribute.flags())
                        : Collections.emptySet())
                .build();
        Logger.info(
            BaseSchemaBuilder.class,
            String.format(
                "Attribute %s successfully registered for object class %s.",
                attributeInfo.getName(), identityModelClass.getSimpleName()));
        Logger.trace(
            BaseSchemaBuilder.class,
            String.format(
                "Attribute %s attribute schema (XML form) is: %s",
                attributeInfo.getName(), attributeInfo));
        attributeInfoSet.add(attributeInfo);
      }

      var holderAttribute = field.getAnnotation(ModelAttributeHolder.class);
      if (holderAttribute != null) {
        attributeCount += scanClassForModelAttributes(field.getType(), builder);
      }
    }

    if (!attributeInfoSet.isEmpty()) {
      builder.addAllAttributeInfo(attributeInfoSet);
      attributeCount += attributeInfoSet.size();
    }

    return attributeCount;
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
