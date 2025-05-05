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

package com.exclamationlabs.connid.base.edition.neo.internal.search;

import static com.exclamationlabs.connid.base.edition.neo.internal.search.GetType.GET_OBJECT;

import com.exclamationlabs.connid.base.connector.adapter.AdapterValueTypeConverter;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.filter.FilterType;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.connector.util.OperationOptionsDataFinder;
import com.exclamationlabs.connid.base.edition.neo.driver.Driver;
import com.exclamationlabs.connid.base.edition.neo.driver.Invocator;
import com.exclamationlabs.connid.base.edition.neo.internal.IdentityModelAccess;
import com.exclamationlabs.connid.base.edition.neo.internal.model.ModelReader;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import java.util.Set;
import java.util.stream.Collectors;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.AttributeFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

public class GetHandler<T extends ConnectorConfiguration> {

  public ConnectorObject get(
      GetType getType,
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      ObjectClass objectClass,
      Filter queryFilter,
      IdentityModelAccess identityModelAccess,
      ResultsHandler resultsHandler,
      OperationOptions operationOptions) {

    ConnectorObject result = null;

    GetStrategy getStrategy =
        deduceStrategy(getType, configuration, identityModelClass, queryFilter, operationOptions);
    switch (getStrategy.getType()) {
      case GET_OBJECT:
        result =
            getObject(
                configuration, driver, invocator, objectClass, getStrategy, identityModelAccess);
        break;
      case GET_ONE_BY_ID:
        getOneById(
            configuration,
            driver,
            invocator,
            objectClass,
            getStrategy,
            resultsHandler,
            identityModelAccess);
        break;
      case GET_ONE_BY_NAME:
        getOneByName(
            configuration,
            driver,
            invocator,
            objectClass,
            getStrategy,
            resultsHandler,
            identityModelAccess);
        break;
      case GET_ALL:
        getAll(
            configuration,
            driver,
            invocator,
            objectClass,
            getStrategy,
            resultsHandler,
            identityModelAccess,
            operationOptions,
            queryFilter);
        break;
      case IMPORT_ALL:
        var importHandler = new ImportHandler<T>();
        importHandler.execute(
            configuration, driver, invocator, objectClass, resultsHandler, identityModelAccess);
        break;
    }

    return result;
  }

  private GetStrategy deduceStrategy(
      GetType getType,
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      Filter queryFilter,
      OperationOptions operationOptions) {
    GetStrategy strategy = new GetStrategy();
    strategy.setIdentityModelClass(identityModelClass);
    if (getType == GET_OBJECT) {
      strategy.setType(GetStrategyType.GET_OBJECT);
    } else if (queryFilter == null) {
      if (operationOptions != null
          && OperationOptionsDataFinder.hasValidPagingOptions(operationOptions.getOptions())) {
        strategy.setType(GetStrategyType.GET_ALL);
      } else {
        // No pagination and no filter - must be a request to import all records
        strategy.setType(GetStrategyType.IMPORT_ALL);
      }

    } else {
      if (queryFilter instanceof EqualsFilter) {
        var filter = (EqualsFilter) queryFilter;
        if (filter.getAttribute().getName().equals(Uid.NAME)) {
          strategy.setType(GetStrategyType.GET_ONE_BY_ID);
          strategy.setMatchValue(filter.getAttribute().getValue().get(0).toString());
        } else if (filter.getAttribute().getName().equals(Name.NAME)) {
          strategy.setType(GetStrategyType.GET_ONE_BY_NAME);
          strategy.setMatchValue(filter.getAttribute().getValue().get(0).toString());
        } else {
          strategy.setType(GetStrategyType.GET_ALL);
        }
      }
    }
    return strategy;
  }

  private ConnectorObject getObject(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      ObjectClass objectClass,
      GetStrategy strategy,
      IdentityModelAccess identityModelAccess) {
    ConnectorObject result = null;
    IdentityModel match;
    if (invocator == null) {
      match =
          driver.getOne(
              configuration,
              strategy.getIdentityModelClass(),
              strategy.getMatchValue(),
              driver.getPrefetch(configuration, identityModelAccess.getIdentityModelClass()));
    } else {
      match =
          invocator.getOne(
              driver,
              configuration,
              strategy.getMatchValue(),
              driver.getPrefetch(configuration, identityModelAccess.getIdentityModelClass()));
    }
    if (match != null) {
      result = constructConnectorObject(objectClass, match, identityModelAccess);
    }

    return result;
  }

  private void getOneById(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      ObjectClass objectClass,
      GetStrategy strategy,
      ResultsHandler resultsHandler,
      IdentityModelAccess identityModelAccess) {
    IdentityModel match;
    if (invocator == null) {
      match =
          driver.getOne(
              configuration,
              strategy.getIdentityModelClass(),
              strategy.getMatchValue(),
              driver.getPrefetch(configuration, identityModelAccess.getIdentityModelClass()));
    } else {
      match =
          invocator.getOne(
              driver,
              configuration,
              strategy.getMatchValue(),
              driver.getPrefetch(configuration, identityModelAccess.getIdentityModelClass()));
    }
    if (match != null) {
      resultsHandler.handle(constructConnectorObject(objectClass, match, identityModelAccess));
    }
  }

  private void getOneByName(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      ObjectClass objectClass,
      GetStrategy strategy,
      ResultsHandler resultsHandler,
      IdentityModelAccess identityModelAccess) {
    IdentityModel match;
    if (invocator == null) {
      match =
          driver.getOneByName(
              configuration,
              strategy.getIdentityModelClass(),
              strategy.getMatchValue(),
              driver.getPrefetch(configuration, identityModelAccess.getIdentityModelClass()));
    } else {
      match =
          invocator.getOneByName(
              driver,
              configuration,
              strategy.getMatchValue(),
              driver.getPrefetch(configuration, identityModelAccess.getIdentityModelClass()));
    }
    if (match != null) {
      resultsHandler.handle(constructConnectorObject(objectClass, match, identityModelAccess));
    }
  }

  private void getAll(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      ObjectClass objectClass,
      GetStrategy strategy,
      ResultsHandler resultsHandler,
      IdentityModelAccess identityModelAccess,
      OperationOptions operationOptions,
      Filter filter) {
    var paginator =
        operationOptions.getPageSize() == null
            ? new ResultsPaginator()
            : operationOptions.getPagedResultsOffset() == null
                ? new ResultsPaginator(operationOptions.getPageSize(), 1)
                : new ResultsPaginator(
                    operationOptions.getPageSize(), operationOptions.getPagedResultsOffset());
    var resultsFilter = determineResultsFilter(filter);

    Set<IdentityModel> results =
        invokeGetAll(
            configuration,
            driver,
            invocator,
            identityModelAccess.getIdentityModelClass(),
            resultsFilter,
            paginator);
    if (results != null) {
      var responseResults =
          performInternalFilteringAndPagination(
              results,
              identityModelAccess,
              resultsFilter,
              paginator,
              driver,
              invocator,
              configuration);

      responseResults.forEach(
          match ->
              resultsHandler.handle(
                  constructConnectorObject(objectClass, match, identityModelAccess)));
    }
  }

  @SuppressWarnings("unchecked")
  protected Set<IdentityModel> invokeGetAll(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      Class<? extends IdentityModel> identityModelClass,
      ResultsFilter resultsFilter,
      ResultsPaginator resultsPaginator) {

    if (invocator == null) {
      return driver.getAll(
          configuration,
          identityModelClass,
          resultsFilter,
          resultsPaginator,
          -1,
          driver.getPrefetch(configuration, identityModelClass));
    } else {
      return (Set<IdentityModel>)
          invocator.getAll(
              driver,
              configuration,
              resultsFilter,
              resultsPaginator,
              -1,
              driver.getPrefetch(configuration, identityModelClass));
    }
  }

  private Set<IdentityModel> performInternalFilteringAndPagination(
      Set<IdentityModel> results,
      IdentityModelAccess identityModelAccess,
      ResultsFilter resultsFilter,
      ResultsPaginator paginator,
      Driver<T> driver,
      Invocator<T, ?, ?> invocator,
      T configuration) {
    Set<IdentityModel> filteredResults = null;
    if (resultsFilter.hasFilter()) {
      var filterAttributeName = resultsFilter.getAttribute();
      var fieldAccessInfo = identityModelAccess.getFieldAccessInfoMap().get(filterAttributeName);
      if (fieldAccessInfo != null) {
        var nativeSupports =
            resultsFilter.getFilterType().equals(FilterType.EqualsFilter)
                ? fieldAccessInfo.isSupportsNativeEqualsFilter()
                : fieldAccessInfo.isSupportsNativeContainsFilter();
        if (!nativeSupports) {
          filteredResults =
              results.stream()
                  .filter(
                      identityModel -> {
                        // Iterate over fields and get Field for matching attribute
                        // Iterate over results and check each Field to see if it matches filter
                        // if it matches, add to new filteredResults
                        // NOTE: cannot filter on nested data
                        try {
                          var matchingField =
                              identityModel
                                  .getClass()
                                  .getDeclaredField(fieldAccessInfo.getField().getName());
                          var attributeValue = matchingField.get(identityModel);
                          return resultsFilter.getValue().equals(attributeValue.toString());

                        } catch (NoSuchFieldException | IllegalAccessException e) {
                          return false;
                        }
                      })
                  .collect(Collectors.toSet());
        }
      }
    }

    var resultsForPagination = filteredResults == null ? results : filteredResults;
    if (paginator.hasPagination()) {
      var supportsNativePagination =
          invocator == null
              ? driver.supportsNativePagination(configuration)
              : invocator.supportsNativePagination(configuration);

      if (!supportsNativePagination) {
        filteredResults =
            resultsForPagination.stream()
                .skip(paginator.getCurrentOffset())
                .limit(paginator.getPageSize())
                .collect(Collectors.toSet());
      }
    }

    return filteredResults == null ? results : filteredResults;
  }

  private ResultsFilter determineResultsFilter(Filter filter) {
    if (filter == null) {
      return new ResultsFilter();
    }
    if (filter instanceof AttributeFilter) {
      return new ResultsFilter(
          ((AttributeFilter) filter).getName(),
          AdapterValueTypeConverter.readSingleAttributeValueAsString(
              ((AttributeFilter) filter).getAttribute()),
          FilterType.EqualsFilter);
    }
    throw new ConnectorException("Unsupported filter type: " + filter.getClass().getName());
  }

  protected ConnectorObject constructConnectorObject(
      ObjectClass objectClass, IdentityModel model, IdentityModelAccess identityModelAccess) {
    ConnectorObjectBuilder builder =
        getConnectorObjectBuilder(objectClass, model, identityModelAccess);
    Set<Attribute> connectorAttributes = ModelReader.execute(model, identityModelAccess);
    connectorAttributes.forEach(builder::addAttribute);
    return builder.build();
  }

  private static ConnectorObjectBuilder getConnectorObjectBuilder(
      ObjectClass objectClass, IdentityModel identity, IdentityModelAccess identityModelAccess) {
    try {
      Object uidValue = identityModelAccess.getGetUidMethod().invoke(identity);
      if (uidValue == null) {
        throw new ConnectorException(
            "UID value is null for identity model "
                + identityModelAccess.getIdentityModelClass().getSimpleName());
      }

      Object nameValue = identityModelAccess.getGetNameMethod().invoke(identity);
      if (nameValue == null) {
        throw new ConnectorException(
            "Name value is null for identity model "
                + identityModelAccess.getIdentityModelClass().getSimpleName());
      }
      return new ConnectorObjectBuilder()
          .setObjectClass(objectClass)
          .setUid(uidValue.toString())
          .setName(nameValue.toString());

    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Reflection error while invoking method for UID or Name", e);
    }
  }
}
