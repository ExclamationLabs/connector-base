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
import com.exclamationlabs.connid.base.connector.filter.FilterType;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.connector.util.OperationOptionsDataFinder;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;

/**
 * Executor used by SearchExecutor to perform Contains string searches on attribute values of
 * Identities.
 */
class ContainsFilterExecutor {

  private ContainsFilterExecutor() {}

  protected static SearchResult execute(
      SearchExecutor executor,
      ContainsFilter containsFilter,
      ResultsHandler resultsHandler,
      OperationOptions options)
      throws InvalidAttributeValueException {
    ResultsPaginator resultsPaginator =
        OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
            ? new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset())
            : new ResultsPaginator(SearchExecutor.DEFAULT_FILTER_PAGE_SIZE, 0);
    if ((!executor
            .getEnhancedAdapter()
            .getSearchResultsAttributesPresent()
            .contains(containsFilter.getName()))
        && !(executor.getEnhancedAdapter().getSearchResultsContainsAllAttributes())) {
      if (executor.getEnhancedAdapter() instanceof FilterCapableSource) {
        FilterCapableSource filterCapable = (FilterCapableSource) executor.getEnhancedAdapter();
        if (filterCapable.getContainsFilterAttributes().contains(containsFilter.getName())) {
          return SearchExecutor.executeAPIFilter(
              executor.getAdapter(),
              executor.getEnhancedAdapter(),
              containsFilter,
              resultsHandler,
              options,
              FilterType.ContainsFilter);
        } else if (filterCapable.getEqualsFilterAttributes().contains(containsFilter.getName())) {
          Map<String, Object> prefetchData =
              executor
                  .getAdapter()
                  .getDriver()
                  .getPrefetch(executor.getAdapter().getIdentityModelClass());
          Set<IdentityModel> matchingResults =
              executor
                  .getAdapter()
                  .getDriver()
                  .getAll(
                      executor.getAdapter().getIdentityModelClass(),
                      new ResultsFilter(
                          containsFilter.getName(),
                          AdapterValueTypeConverter.readSingleAttributeValueAsString(
                              containsFilter.getAttribute()),
                          FilterType.EqualsFilter),
                      resultsPaginator,
                      null,
                      prefetchData);
          matchingResults =
              SearchExecutor.performManualPaginationIfNeeded(
                  executor.getEnhancedAdapter(), matchingResults, resultsPaginator);
          SearchExecutor.processResultsPage(
              executor.getAdapter(),
              executor.getEnhancedAdapter(),
              matchingResults,
              resultsHandler,
              prefetchData);
          return new SearchResult(
              resultsPaginator.getTokenAsString(), -1, resultsPaginator.getNoMoreResults());
        } else {
          if (StringUtils.equals(Uid.NAME, containsFilter.getName())) {
            return performManualUidSearch(
                executor.getAdapter(),
                executor.getEnhancedAdapter(),
                containsFilter,
                resultsPaginator,
                resultsHandler);
          } else if (executor.getEnhancedAdapter().getSearchResultsContainsNameAttribute()
              && StringUtils.equals(Name.NAME, containsFilter.getName())) {
            return performManualNameSearch(
                executor.getAdapter(),
                executor.getEnhancedAdapter(),
                containsFilter,
                resultsPaginator,
                resultsHandler);
          }
          throw new InvalidAttributeValueException(
              String.format(
                  "%s source API has limited filter capabilities and is not able to filter based on attribute %s",
                  executor.getEnhancedAdapter().getClass().getSimpleName(),
                  containsFilter.getName()));
        }
      } else {
        if (StringUtils.equals(Uid.NAME, containsFilter.getName())) {
          return performManualUidSearch(
              executor.getAdapter(),
              executor.getEnhancedAdapter(),
              containsFilter,
              resultsPaginator,
              resultsHandler);
        } else if (executor.getEnhancedAdapter().getSearchResultsContainsNameAttribute()
            && StringUtils.equals(Name.NAME, containsFilter.getName())) {
          return performManualNameSearch(
              executor.getAdapter(),
              executor.getEnhancedAdapter(),
              containsFilter,
              resultsPaginator,
              resultsHandler);
        } else {
          throw new InvalidAttributeValueException(
              String.format(
                  "%s source API is not able to filter based on attribute %s",
                  executor.getEnhancedAdapter().getClass().getSimpleName(),
                  containsFilter.getName()));
        }
      }
    } else {
      if (executor.getEnhancedAdapter() instanceof FilterCapableSource) {
        FilterCapableSource filterCapable = (FilterCapableSource) executor.getEnhancedAdapter();
        if (filterCapable.getContainsFilterAttributes().contains(containsFilter.getName())) {
          return SearchExecutor.executeAPIFilter(
              executor.getAdapter(),
              executor.getEnhancedAdapter(),
              containsFilter,
              resultsHandler,
              options,
              FilterType.ContainsFilter);
        }
      }
      // No direct API filter, get all results and return matches
      Map<String, Object> prefetchData =
          executor
              .getAdapter()
              .getDriver()
              .getPrefetch(executor.getAdapter().getIdentityModelClass());
      int offset =
          OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
              ? resultsPaginator.getCurrentOffset()
              : 0;
      Set<IdentityModel> allResults;
      if (executor.getEnhancedAdapter().getFilteringRequiresFullImport()) {
        // Perform paginated full import in order to perform contains filter
        int importBatchSize =
            ((ResultsConfiguration) executor.getAdapter().getConfiguration()).getImportBatchSize();
        allResults =
            ImportAllExecutor.executeMultiPageImportProcess(
                executor, importBatchSize, prefetchData, null);
      } else {
        // get all results up to API max and return matches
        allResults =
            executor
                .getAdapter()
                .getDriver()
                .getAll(
                    executor.getAdapter().getIdentityModelClass(),
                    new ResultsFilter(),
                    SearchExecutor.getMaximumPageSizePaginator(executor.getAdapter()),
                    null,
                    prefetchData);
      }
      final String filterValue =
          AdapterValueTypeConverter.readSingleAttributeValueAsString(containsFilter.getAttribute());
      Set<IdentityModel> filteredResults = new LinkedHashSet<>();
      allResults.stream()
          .filter(
              identity ->
                  StringUtils.containsIgnoreCase(
                      identity.getValueBySearchableAttributeName(containsFilter.getName()),
                      filterValue))
          .skip(SearchExecutor.correctConnIdOffset(offset))
          .limit(resultsPaginator.getPageSize())
          .forEachOrdered(filteredResults::add);
      SearchExecutor.processResultsPage(
          executor.getAdapter(),
          executor.getEnhancedAdapter(),
          filteredResults,
          resultsHandler,
          prefetchData);
      return new SearchResult(null, -1, false);
    }
  }

  private static SearchResult performManualNameSearch(
      BaseAdapter<?, ?> adapter,
      EnhancedPaginationAndFiltering enhancedAdapter,
      ContainsFilter containsFilter,
      ResultsPaginator resultsPaginator,
      ResultsHandler resultsHandler) {
    Map<String, Object> prefetchData =
        adapter.getDriver().getPrefetch(adapter.getIdentityModelClass());
    Set<IdentityModel> matchingResults =
        adapter
            .getDriver()
            .getAll(
                adapter.getIdentityModelClass(),
                new ResultsFilter(),
                SearchExecutor.getMaximumPageSizePaginator(adapter),
                null,
                prefetchData);
    Set<IdentityModel> filteredResults = new LinkedHashSet<>();
    matchingResults.stream()
        .filter(
            identity ->
                StringUtils.containsIgnoreCase(
                    identity.getIdentityNameValue(), containsFilter.getValue()))
        .skip(SearchExecutor.correctConnIdOffset(resultsPaginator.getCurrentOffset()))
        .limit(resultsPaginator.getPageSize())
        .forEachOrdered(filteredResults::add);
    SearchExecutor.processResultsPage(
        adapter, enhancedAdapter, filteredResults, resultsHandler, prefetchData);
    return new SearchResult(null, -1, false);
  }

  private static SearchResult performManualUidSearch(
      BaseAdapter<?, ?> adapter,
      EnhancedPaginationAndFiltering enhancedAdapter,
      ContainsFilter containsFilter,
      ResultsPaginator resultsPaginator,
      ResultsHandler resultsHandler) {
    Map<String, Object> prefetchData =
        adapter.getDriver().getPrefetch(adapter.getIdentityModelClass());
    Set<IdentityModel> matchingResults =
        adapter
            .getDriver()
            .getAll(
                adapter.getIdentityModelClass(),
                new ResultsFilter(),
                SearchExecutor.getMaximumPageSizePaginator(adapter),
                null,
                prefetchData);
    Set<IdentityModel> filteredResults = new LinkedHashSet<>();
    matchingResults.stream()
        .filter(
            identity ->
                StringUtils.containsIgnoreCase(
                    identity.getIdentityIdValue(), containsFilter.getValue()))
        .skip(SearchExecutor.correctConnIdOffset(resultsPaginator.getCurrentOffset()))
        .limit(resultsPaginator.getPageSize())
        .forEachOrdered(filteredResults::add);
    SearchExecutor.processResultsPage(
        adapter, enhancedAdapter, filteredResults, resultsHandler, prefetchData);
    return new SearchResult(null, -1, false);
  }
}
