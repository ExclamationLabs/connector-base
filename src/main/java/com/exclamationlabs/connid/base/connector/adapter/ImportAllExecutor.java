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
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.identityconnectors.framework.common.objects.ResultsHandler;

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
    if (executor.getAdapter() instanceof PaginationCapableSource) {
      PaginationCapableSource paginationCheck = (PaginationCapableSource) executor.getAdapter();
      int currentOffset = 0;
      int pageSize =
          ((ResultsConfiguration) executor.getAdapter().getConfiguration()).getImportBatchSize();
      if (paginationCheck.hasSearchResultsMaximum()
          && paginationCheck.getSearchResultsMaximum() < pageSize) {
        pageSize = paginationCheck.getSearchResultsMaximum();
      }
      boolean importComplete = false;
      ResultsPaginator currentPaginator = new ResultsPaginator(pageSize, currentOffset);
      while (!importComplete) {
        Set<IdentityModel> pageOfIdentityResults =
            executor
                .getAdapter()
                .getDriver()
                .getAll(
                    executor.getAdapter().getIdentityModelClass(),
                    new ResultsFilter(),
                    currentPaginator,
                    null);
        if (currentPaginator.getNoMoreResults() || pageOfIdentityResults.size() < pageSize) {
          importComplete = true;
        } else {
          currentOffset += pageSize;
        }

        executor.processResultsPage(
            executor.getAdapter(),
            executor.getEnhancedAdapter(),
            pageOfIdentityResults,
            resultsHandler);
      } // end while

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
                  null);
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

        executor.processResultsPage(
            executor.getAdapter(),
            executor.getEnhancedAdapter(),
            pageOfIdentityResults,
            resultsHandler);
      } // end while
    }
    return true;
  }
}
