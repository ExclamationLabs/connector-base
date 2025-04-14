package com.exclamationlabs.connid.base.edition.neo.internal.search;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.Driver;
import com.exclamationlabs.connid.base.edition.neo.driver.Invocator;
import com.exclamationlabs.connid.base.edition.neo.internal.IdentityModelAccess;
import com.exclamationlabs.connid.base.edition.neo.internal.model.ModelReader;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import java.util.Collections;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

public class GetHandler<T extends ConnectorConfiguration> {

  public void get(
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

    GetStrategy getStrategy =
        deduceStrategy(getType, configuration, identityModelClass, queryFilter);
    switch (getStrategy.getType()) {
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
        // getOne by NAME
        break;
      case GET_ALL:
        getAll(
            configuration,
            driver,
            invocator,
            objectClass,
            getStrategy,
            resultsHandler,
            identityModelAccess);
        break;
      case IMPORT_ALL:
        // importAll
        break;
    }
  }

  private GetStrategy deduceStrategy(
      GetType getType,
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      Filter queryFilter) {
    GetStrategy strategy = new GetStrategy();
    strategy.setIdentityModelClass(identityModelClass);
    if (queryFilter == null) {
      strategy.setType(GetStrategyType.GET_ALL);
    } else {
      if (queryFilter instanceof EqualsFilter) {
        var filter = (EqualsFilter) queryFilter;
        if (filter.getAttribute().getName().equals(Uid.NAME)) {
          strategy.setType(GetStrategyType.GET_ONE_BY_ID);
          strategy.setMatchValue(filter.getAttribute().getValue().get(0).toString());
        } else {
        }
      }
    }
    return strategy;
  }

  private void getOneById(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      ObjectClass objectClass,
      GetStrategy strategy,
      ResultsHandler resultsHandler,
      IdentityModelAccess identityModelAccess) {
    IdentityModel match = null;
    if (invocator == null) {
      match =
          driver.getOne(
              configuration,
              strategy.getIdentityModelClass(),
              strategy.getMatchValue(),
              Collections.emptyMap());
    } else {
      match =
          invocator.getOne(driver, configuration, strategy.getMatchValue(), Collections.emptyMap());
    }
    if (match != null) {
      resultsHandler.handle(constructConnectorObject(objectClass, match, identityModelAccess));
    }
  }

  @SuppressWarnings("unchecked")
  private void getAll(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      ObjectClass objectClass,
      GetStrategy strategy,
      ResultsHandler resultsHandler,
      IdentityModelAccess identityModelAccess) {
    Set<IdentityModel> results;
    var resultsFilter = new ResultsFilter();
    var resultPaginator = new ResultsPaginator();
    if (invocator == null) {
      results =
          driver.getAll(
              configuration,
              strategy.getIdentityModelClass(),
              resultsFilter,
              resultPaginator,
              -1,
              Collections.emptyMap());
    } else {
      results =
          (Set<IdentityModel>)
              invocator.getAll(
                  driver,
                  configuration,
                  resultsFilter,
                  resultPaginator,
                  -1,
                  Collections.emptyMap());
    }
    if (results != null) {
      results.forEach(
          match ->
              resultsHandler.handle(
                  constructConnectorObject(objectClass, match, identityModelAccess)));
    }
  }

  private static ConnectorObject constructConnectorObject(
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
