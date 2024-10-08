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
import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.ResultsConfiguration;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.SyncOp;

/**
 * Default Connector Schema builder for Base Connector framework. This will likely fit the needs for
 * all future connectors developed; but if a custom solution is needed, use {@link
 * com.exclamationlabs.connid.base.connector.BaseConnector#setConnectorSchemaBuilder}
 */
public class DefaultConnectorSchemaBuilder<T extends ConnectorConfiguration>
    implements ConnectorSchemaBuilder<T> {

  @Override
  public Schema build(BaseConnector<T> connector, Map<ObjectClass, BaseAdapter<?, T>> adapterMap)
      throws ConfigurationException {
    Logger.info(this, String.format("Building schema for connector %s ...", connector.getName()));
    SchemaBuilder schemaBuilder = new SchemaBuilder(connector.getClass());

    for (BaseAdapter<?, T> currentAdapter : adapterMap.values()) {
      Set<ConnectorAttribute> currentAttributes = currentAdapter.getConnectorAttributes();

      Logger.info(
          this,
          String.format(
              "Determining schema elements for connector %s, object class %s ...",
              connector.getName(), currentAdapter.getType()));
      schemaBuilder.defineObjectClass(
          buildObjectClassInfo(currentAdapter.getType(), currentAttributes));
    }

    T configuration = connector.getConnectorConfiguration();
    if (configuration instanceof ResultsConfiguration) {
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

    Logger.info(
        this, String.format("Finished building schema for connector %s", connector.getName()));

    return schemaBuilder.build();
  }

  protected static ObjectClassInfo buildObjectClassInfo(
      ObjectClass classType, Set<ConnectorAttribute> attributes) {
    ObjectClassInfoBuilder builder = new ObjectClassInfoBuilder();
    builder.setType(classType.getObjectClassValue());

    Collection<AttributeInfo> attributeInfoSet =
        attributes.stream()
            .map(DefaultConnectorSchemaBuilder::buildAttributeInfo)
            .collect(Collectors.toSet());
    builder.addAllAttributeInfo(attributeInfoSet);
    Logger.debug(
        DefaultConnectorSchemaBuilder.class,
        String.format("Added %s schema elements for type %s", attributeInfoSet.size(), classType));

    return builder.build();
  }

  private static AttributeInfo buildAttributeInfo(ConnectorAttribute current) {

    Logger.debug(
        DefaultConnectorSchemaBuilder.class,
        String.format(
            "Current schema element for attribute: %s; %s; %s",
            current.getName(), current.getDataType().getClassType(), current.getFlags()));

    return new AttributeInfoBuilder(current.getName())
        .setNativeName(current.getNativeName())
        .setType(current.getDataType().getClassType())
        .setSubtype(
            StringUtils.isNotBlank(current.getMetaInfoJson()) ? current.getMetaInfoJson() : null)
        .setFlags(current.getFlags())
        .build();
  }
}
