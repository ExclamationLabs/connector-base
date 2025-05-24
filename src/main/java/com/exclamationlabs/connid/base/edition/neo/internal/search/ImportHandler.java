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

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.ResultsConfiguration;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.Driver;
import com.exclamationlabs.connid.base.edition.neo.driver.Invocator;
import com.exclamationlabs.connid.base.edition.neo.internal.IdentityModelAccess;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ResultsHandler;

/**
 * Handler used to import all results for a given object class from a source API. Supports
 * pagination and may make multiple requests to the source API.
 */
public class ImportHandler<T extends ConnectorConfiguration> extends GetHandler<T> {

  public static final int DEFAULT_PAGE_SIZE = 50;

  public void execute(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      ObjectClass objectClass,
      ResultsHandler resultsHandler,
      IdentityModelAccess identityModelAccess) {
    int pageSize =
        configuration instanceof ResultsConfiguration
            ? ((ResultsConfiguration) configuration).getImportBatchSize()
            : DEFAULT_PAGE_SIZE;

    if (driver.supportsNativePagination(configuration)) {

      executeMultiPageImportProcess(
          configuration,
          driver,
          invocator,
          identityModelAccess,
          objectClass,
          pageSize,
          resultsHandler);

    } else {
      // API has no pagination capability, manually paginate here
      int currentOffset = 0;
      boolean importComplete = false;
      Set<IdentityModel> fullIdentityResults =
          invokeGetAll(
              configuration,
              driver,
              invocator,
              identityModelAccess.getIdentityModelClass(),
              new ResultsFilter(),
              new ResultsPaginator(pageSize, currentOffset));
      while (!importComplete) {
        if (fullIdentityResults.size() < pageSize
            || (currentOffset + pageSize) >= fullIdentityResults.size()) {
          importComplete = true;
        }

        Set<IdentityModel> pageOfIdentityResults = new LinkedHashSet<>();
        fullIdentityResults.stream()
            .skip(currentOffset)
            .limit(pageSize)
            .forEachOrdered(pageOfIdentityResults::add);
        if (pageOfIdentityResults.size() < pageSize) {
          importComplete = true;
        } else {
          currentOffset += pageSize;
        }

        pageOfIdentityResults.forEach(
            match ->
                resultsHandler.handle(
                    constructConnectorObject(objectClass, match, identityModelAccess)));
      } // end while
    }
  }

  protected void executeMultiPageImportProcess(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      IdentityModelAccess identityModelAccess,
      ObjectClass objectClass,
      int pageSize,
      ResultsHandler resultsHandler) {
    var throttle = driver.getImportThreadCount();
    if (throttle < 2) {
      executeMultiPageImportProcessNoMultiThread(
          configuration,
          driver,
          invocator,
          identityModelAccess,
          objectClass,
          pageSize,
          resultsHandler);
      return;
    }
    boolean importComplete = false;
    int currentOffset = 0;
    while (!importComplete) {
      List<Future<Set<IdentityModel>>> pageImportExecutions = new ArrayList<>();
      for (int xx = 0; xx < throttle; xx++) {
        pageImportExecutions.add(
            importSinglePageExecution(
                configuration,
                driver,
                invocator,
                identityModelAccess,
                new ResultsPaginator(pageSize, currentOffset)));
        currentOffset += pageSize;
      }
      for (Future<Set<IdentityModel>> oneExecution : pageImportExecutions) {
        try {
          Set<IdentityModel> pageOfIdentityResults = oneExecution.get();

          pageOfIdentityResults.forEach(
              match ->
                  resultsHandler.handle(
                      constructConnectorObject(objectClass, match, identityModelAccess)));

          // Once we see the API return number of results smaller than the page size or 0, we know
          // that import is complete
          if (pageOfIdentityResults.size() < pageSize) {
            importComplete = true;
          }

          Logger.trace(
              this.getClass(),
              String.format(
                  "Imported %d identities at %d",
                  pageOfIdentityResults.size(), System.currentTimeMillis()));
        } catch (InterruptedException | ExecutionException ee) {
          throw new ConnectorException("Error occurred while executing importAll page thread", ee);
        } catch (CancellationException cancelled) {
          Logger.warn(
              this.getClass(), "Cancellation exception occurred while executing completable");
        }
      }
    }
  }

  protected void executeMultiPageImportProcessNoMultiThread(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      IdentityModelAccess identityModelAccess,
      ObjectClass objectClass,
      int pageSize,
      ResultsHandler resultsHandler) {
    int currentOffset = 0;
    boolean importComplete = false;

    while (!importComplete) {
      ResultsPaginator currentPaginator = new ResultsPaginator(pageSize, currentOffset);
      Set<IdentityModel> pageOfIdentityResults =
          invokeGetAll(
              configuration,
              driver,
              invocator,
              identityModelAccess.getIdentityModelClass(),
              new ResultsFilter(),
              currentPaginator);
      if (currentPaginator.getNoMoreResults() || pageOfIdentityResults.size() < pageSize) {
        importComplete = true;
      } else {
        currentOffset += pageSize;
      }

      pageOfIdentityResults.forEach(
          match ->
              resultsHandler.handle(
                  constructConnectorObject(objectClass, match, identityModelAccess)));
    } // end while
  }

  private void importSinglePage(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      IdentityModelAccess identityModelAccess,
      ResultsPaginator paginator,
      CompletableFuture<Set<IdentityModel>> completableFuture) {
    Set<IdentityModel> resultPage =
        invokeGetAll(
            configuration,
            driver,
            invocator,
            identityModelAccess.getIdentityModelClass(),
            new ResultsFilter(),
            paginator);
    completableFuture.complete(resultPage);
  }

  private CompletableFuture<Set<IdentityModel>> importSinglePageExecution(
      T configuration,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      IdentityModelAccess identityModelAccess,
      ResultsPaginator resultsPaginator) {
    CompletableFuture<Set<IdentityModel>> completableFuture = new CompletableFuture<>();
    Executors.newCachedThreadPool()
        .submit(
            () ->
                importSinglePage(
                    configuration,
                    driver,
                    invocator,
                    identityModelAccess,
                    resultsPaginator,
                    completableFuture));
    return completableFuture;
  }
}
