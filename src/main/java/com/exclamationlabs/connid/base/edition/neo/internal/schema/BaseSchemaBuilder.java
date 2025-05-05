/*
    Copyright 2025 Exclamation Labs

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

package com.exclamationlabs.connid.base.edition.neo.internal.schema;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.ResultsConfiguration;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.internal.BaseConnectorTypeFactory;
import com.exclamationlabs.connid.base.edition.neo.internal.IdentityModelAccess;
import com.exclamationlabs.connid.base.edition.neo.model.AssignmentType;
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
      final BaseConnectorTypeFactory<T> connectorTypeFactory) {
    Logger.info(this, String.format("Building schema for connector %s ...", connector.getName()));
    var accessMap = connectorTypeFactory.getIdentityModelAccessMap();

    var connIdSchemaBuilder = new SchemaBuilder(connector.getClass());

    for (var identityObjectClass : accessMap.keySet()) {
      var identityModelAccess = accessMap.get(identityObjectClass);
      Logger.info(
          this,
          String.format(
              "Begin scanning schema elements for model %s, object class %s ...",
              identityModelAccess.getIdentityModelClass().getSimpleName(),
              identityObjectClass.getObjectClassValue()));
      connIdSchemaBuilder.defineObjectClass(
          buildObjectClassInfo(identityObjectClass, identityModelAccess));
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
          nativeName = StringUtils.defaultIfBlank(fieldAccessInfo.getNativeName(), attributeName);
          break;
      }
      var dataType =
          fieldAccessInfo.getDataType().getClassType().equals(AssignmentType.class)
              ? ConnectorAttributeDataType.STRING
              : fieldAccessInfo.getDataType();
      var attributeInfo =
          new AttributeInfoBuilder(attributeName)
              .setNativeName(nativeName)
              .setType(dataType.getClassType())
              .setSubtype(StringUtils.trimToNull(fieldAccessInfo.getMetaInfoJson()))
              .setFlags(
                  fieldAccessInfo.getFlags().length > 0
                      ? Set.of(fieldAccessInfo.getFlags())
                      : Collections.emptySet())
              .build();
      Logger.info(
          BaseSchemaBuilder.class,
          String.format(
              "Attribute %s successfully registered for object class %s.",
              attributeInfo.getName(),
              identityModelAccess.getIdentityModelClass().getSimpleName()));
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
