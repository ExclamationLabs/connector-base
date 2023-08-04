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

package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.configuration.basetypes.ResultsConfiguration;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import java.util.*;
import java.util.concurrent.*;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.ResultsHandler;

/**
 * Executor used by SearchExecutor to Import all Identity records for a given Object Class from a
 * source API. This may require many requests and take a long time to complete. Results are passed
 * to ResultHandler in batches based upon importBatchSize configuration value.
 */
public class ImportAllExecutor {

  private ImportAllExecutor() {}

  // Check for importAll scenarios
  // Scenario 1: OperationOptions pagination values are NOT submitted AND filter is NULL AND
  // Adapter DOES implement PaginationCapableSource
  // - Loop through item index values in groups of X (where X is importBatchSize value)
  // - Invoke getAll with new ResultsPagination based on current index of group and dummy Filter.
  // - Receive set of items and call getOne for each record UNLESS
  // getSearchResultsContainAllAttributes is true
  // - pass items in that group to the results handler
  // - continue with next set of index values until end is reached or no more results remain.
  // Scenario 2: OperationOptions pagination values are NOT submitted AND filter is NULL AND
  // Adapter DOES NOT implement PaginationCapableSource
  // - Invoke getAll with dummy Pagination and dummy Filter.
  // - Loop through Set of records in groups of X (where X is importBatchSize value)
  // - For each group, call getOne for each record UNLESS getSearchResultsContainAllAttributes is
  // true
  // - pass items in that group to the results handler
  // - continue until all groups have been processed.
  // For both Scenario 1 and 2, HAPPY OUTCOME 4: getAll perform full import is invoked
  protected static boolean execute(SearchExecutor executor, ResultsHandler resultsHandler) {
    Map<String, Object> prefetchData =
        executor
            .getAdapter()
            .getDriver()
            .getPrefetch(executor.getAdapter().getIdentityModelClass());
    if (executor.getAdapter() instanceof PaginationCapableSource) {
      PaginationCapableSource paginationCheck = (PaginationCapableSource) executor.getAdapter();
      int pageSize =
          ((ResultsConfiguration) executor.getAdapter().getConfiguration()).getImportBatchSize();
      if (paginationCheck.hasSearchResultsMaximum()
          && paginationCheck.getSearchResultsMaximum() < pageSize) {
        pageSize = paginationCheck.getSearchResultsMaximum();
      }
      executeMultiPageImportProcess(executor, pageSize, prefetchData, resultsHandler);

    } else {
      // API has no pagination capability, manually paginate here
      int currentOffset = 0;
      int pageSize =
          ((ResultsConfiguration) executor.getAdapter().getConfiguration()).getImportBatchSize();
      boolean importComplete = false;
      Set<IdentityModel> fullIdentityResults =
          executor
              .getAdapter()
              .getDriver()
              .getAll(
                  executor.getAdapter().getIdentityModelClass(),
                  new ResultsFilter(),
                  new ResultsPaginator(),
                  null,
                  prefetchData);
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

        SearchExecutor.processResultsPage(
            executor.getAdapter(),
            executor.getEnhancedAdapter(),
            pageOfIdentityResults,
            resultsHandler,
            prefetchData);
      } // end while
    }
    return true;
  }

  protected static Set<IdentityModel> executeMultiPageImportProcess(
      SearchExecutor executor,
      int pageSize,
      Map<String, Object> prefetchData,
      ResultsHandler resultsHandler) {
    int throttle = executor.getEnhancedAdapter().getImportUsingPaginationThreadCount();
    if (throttle < 2) {
      return executeMultiPageImportProcessNoMultiThread(
          executor, pageSize, prefetchData, resultsHandler);
    }
    boolean importComplete = false;
    int currentOffset = 0;
    Set<IdentityModel> fullCollectedResults = new LinkedHashSet<>();
    while (!importComplete) {
      List<Future<Set<IdentityModel>>> pageImportExecutions = new ArrayList<>();
      for (int xx = 0; xx < throttle; xx++) {
        pageImportExecutions.add(
            importSinglePageExecution(
                executor, new ResultsPaginator(pageSize, currentOffset), prefetchData));
        currentOffset += pageSize;
      }
      for (Future<Set<IdentityModel>> oneExecution : pageImportExecutions) {
        try {
          Set<IdentityModel> pageOfIdentityResults = oneExecution.get();
          if (resultsHandler != null) {
            SearchExecutor.processResultsPage(
                executor.getAdapter(),
                executor.getEnhancedAdapter(),
                pageOfIdentityResults,
                resultsHandler,
                prefetchData);
          } else {
            fullCollectedResults.addAll(pageOfIdentityResults);
          }
          // Once we see the API return number of results smaller than the page size or 0, we know
          // that import is complete
          if (pageOfIdentityResults.size() < pageSize) {
            importComplete = true;
          }

          Logger.trace(
              ImportAllExecutor.class,
              String.format(
                  "Imported %d identities at %d",
                  pageOfIdentityResults.size(), System.currentTimeMillis()));
        } catch (InterruptedException | ExecutionException ee) {
          throw new ConnectorException("Error occurred while executing importAll page thread", ee);
        } catch (CancellationException cancelled) {
          throw new ConnectorException(
              "Error occurred while executing Completable importAll page thread", cancelled);
        }
      }
    }
    return fullCollectedResults;
  }

  private static CompletableFuture<Set<IdentityModel>> importSinglePageExecution(
      SearchExecutor executor,
      ResultsPaginator resultsPaginator,
      Map<String, Object> prefetchData) {
    CompletableFuture<Set<IdentityModel>> completableFuture = new CompletableFuture<>();
    Executors.newCachedThreadPool()
        .submit(
            () -> importSinglePage(executor, resultsPaginator, prefetchData, completableFuture));
    return completableFuture;
  }

  private static void importSinglePage(
      SearchExecutor executor,
      ResultsPaginator paginator,
      Map<String, Object> prefetchData,
      CompletableFuture<Set<IdentityModel>> completableFuture) {
    Set<IdentityModel> resultPage =
        executor
            .getAdapter()
            .getDriver()
            .getAll(
                executor.getAdapter().getIdentityModelClass(),
                new ResultsFilter(),
                paginator,
                null,
                prefetchData);
    completableFuture.complete(resultPage);
  }

  protected static Set<IdentityModel> executeMultiPageImportProcessNoMultiThread(
      SearchExecutor executor,
      int pageSize,
      Map<String, Object> prefetchData,
      ResultsHandler resultsHandler) {
    int currentOffset = 0;
    boolean importComplete = false;
    Set<IdentityModel> collectedResults = new LinkedHashSet<>();

    while (!importComplete) {
      ResultsPaginator currentPaginator = new ResultsPaginator(pageSize, currentOffset);
      Set<IdentityModel> pageOfIdentityResults =
          executor
              .getAdapter()
              .getDriver()
              .getAll(
                  executor.getAdapter().getIdentityModelClass(),
                  new ResultsFilter(),
                  currentPaginator,
                  null,
                  prefetchData);
      if (currentPaginator.getNoMoreResults() || pageOfIdentityResults.size() < pageSize) {
        importComplete = true;
      } else {
        currentOffset += pageSize;
      }

      if (resultsHandler != null) {
        SearchExecutor.processResultsPage(
            executor.getAdapter(),
            executor.getEnhancedAdapter(),
            pageOfIdentityResults,
            resultsHandler,
            prefetchData);
      } else {
        collectedResults.addAll(pageOfIdentityResults);
      }
    } // end while
    return collectedResults;
  }
}
