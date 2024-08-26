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
import com.exclamationlabs.connid.base.connector.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.api.operations.GetApiOp;
import org.identityconnectors.framework.api.operations.SearchApiOp;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.AttributeFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.operations.SearchOp;

/**
 * Connector that allows read-only access on a destination system. This class should be subclassed
 * if your primary desire is to have just read-only access to the data types on the destination
 * system.
 *
 * <p>Note that only the ConnId SearchOp&lt;String&gt; interface is implemented for this class;
 * therefore this connector cannot take in create/update/delete requests from Midpoint.
 */
public abstract class BaseReadOnlyConnector<T extends ConnectorConfiguration>
    extends BaseConnector<T> implements SearchOp<Filter>, SearchApiOp, GetApiOp {

  public BaseReadOnlyConnector(Class<T> configurationTypeIn) {
    super(configurationTypeIn);
  }

  public BaseReadOnlyConnector(Class<T> configurationTypeIn, boolean commonsLogging) {
    super(configurationTypeIn, commonsLogging);
  }

  @Override
  public FilterTranslator<Filter> createFilterTranslator(
      ObjectClass objectClass, OperationOptions operationOptions) {
    return getConnectorFilterTranslator(objectClass);
  }

  @Override
  public ConnectorObject getObject(
      ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
    return getAdapter(objectClass).getObject(uid, operationOptions);
  }

  @Override
  public void executeQuery(
      final ObjectClass objectClass,
      final Filter queryFilter,
      final ResultsHandler resultsHandler,
      final OperationOptions operationOptions) {
    getAdapter(objectClass)
        .get(queryFilter, resultsHandler, operationOptions, isEnhancedFiltering());
  }

  @Override
  public void executeQuery(
      final ObjectClass objectClass,
      String itemId,
      final ResultsHandler resultsHandler,
      final OperationOptions operationOptions) {
    if (StringUtils.isEmpty(itemId)) {
      getAdapter(objectClass).get(null, resultsHandler, operationOptions, isEnhancedFiltering());
    } else {
      Attribute attribute = new AttributeBuilder().setName(Uid.NAME).addValue(itemId).build();
      AttributeFilter queryFilter = new EqualsFilter(attribute);
      getAdapter(objectClass)
          .get(queryFilter, resultsHandler, operationOptions, isEnhancedFiltering());
    }
  }

  @Override
  public SearchResult search(
      final ObjectClass objectClass,
      final Filter filter,
      final ResultsHandler handler,
      final OperationOptions options) {
    return getAdapter(objectClass).get(filter, handler, options, isEnhancedFiltering());
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

  /**
   * Method to manually invoke the connector and assure that we can successfully obtain a small
   * number of records from the destination API, without any authentication or general integration
   * errors occurring.
   *
   * @param objectClass - The object class for quick lookup search.
   * @return true if the look-up is successful, false if look-up fails.
   */
  public boolean quickTest(final ObjectClass objectClass) {
    try {
      getAdapter(objectClass)
          .get(
              null,
              (ConnectorObject connectorObject) -> true,
              new OperationOptionsBuilder().setPageSize(3).setPagedResultsOffset(1).build(),
              true);
      return true;
    } catch (ConnectorException ce) {
      Logger.warn(this, "Quick test for connector failed", ce);
      return false;
    }
  }
}
