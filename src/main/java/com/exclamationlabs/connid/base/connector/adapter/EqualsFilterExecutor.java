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
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.SearchResult;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;

/**
 * Executor used by SearchExecutor to perform Equals string searches on attribute values of
 * Identities (not including UID and NAME matching capabilities).
 */
public class EqualsFilterExecutor {

  private EqualsFilterExecutor() {}

  protected static SearchResult execute(
      SearchExecutor executor,
      EqualsFilter equalsFilter,
      ResultsHandler resultsHandler,
      OperationOptions options)
      throws InvalidAttributeValueException {
    if ((!executor
            .getEnhancedAdapter()
            .getSearchResultsAttributesPresent()
            .contains(equalsFilter.getName()))
        && (!executor.getEnhancedAdapter().getSearchResultsContainsAllAttributes())) {
      if (executor.getEnhancedAdapter() instanceof FilterCapableSource) {
        FilterCapableSource filterCapable = (FilterCapableSource) executor.getEnhancedAdapter();
        if (filterCapable.getEqualsFilterAttributes().contains(equalsFilter.getName())) {
          return SearchExecutor.executeAPIFilter(
              executor.getAdapter(),
              executor.getEnhancedAdapter(),
              equalsFilter,
              resultsHandler,
              options,
              FilterType.EqualsFilter);
        } else if (filterCapable.getContainsFilterAttributes().contains(equalsFilter.getName())) {
          // Fallback option - must search using Contains filter since it is only manner available
          Map<String, Object> prefetchData =
              executor
                  .getAdapter()
                  .getDriver()
                  .getPrefetch(executor.getAdapter().getIdentityModelClass());
          ResultsPaginator resultsPaginator =
              OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
                  ? new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset())
                  : new ResultsPaginator(SearchExecutor.DEFAULT_FILTER_PAGE_SIZE, 0);

          Set<IdentityModel> matchingResults =
              executor
                  .getAdapter()
                  .getDriver()
                  .getAll(
                      executor.getAdapter().getIdentityModelClass(),
                      new ResultsFilter(
                          equalsFilter.getName(),
                          AdapterValueTypeConverter.readSingleAttributeValueAsString(
                              equalsFilter.getAttribute()),
                          FilterType.ContainsFilter),
                      resultsPaginator,
                      null,
                      prefetchData);
          SearchExecutor.processResultsPage(
              executor.getAdapter(),
              executor.getEnhancedAdapter(),
              matchingResults,
              resultsHandler,
              prefetchData);
          return new SearchResult(
              resultsPaginator.getTokenAsString(), -1, resultsPaginator.getNoMoreResults());
        } else {
          throw new InvalidAttributeValueException(
              String.format(
                  "%s source API has limited filter capabilities and is not able to filter based on attribute %s",
                  executor.getEnhancedAdapter().getClass().getSimpleName(),
                  equalsFilter.getName()));
        }
      } else {

        throw new InvalidAttributeValueException(
            String.format(
                "%s source API is not able to filter based on attribute %s",
                executor.getEnhancedAdapter().getClass().getSimpleName(), equalsFilter.getName()));
      }
    } else {
      if (executor.getEnhancedAdapter() instanceof FilterCapableSource) {
        FilterCapableSource filterCapable = (FilterCapableSource) executor.getEnhancedAdapter();
        if (filterCapable.getEqualsFilterAttributes().contains(equalsFilter.getName())
            || filterCapable.getContainsFilterAttributes().contains(equalsFilter.getName())) {

          return SearchExecutor.executeAPIFilter(
              executor.getAdapter(),
              executor.getEnhancedAdapter(),
              equalsFilter,
              resultsHandler,
              options,
              FilterType.EqualsFilter);
        }
      }
      // No direct API filter, get all results up to API max and return matches
      ResultsPaginator resultsPaginator =
          OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
              ? new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset())
              : new ResultsPaginator(SearchExecutor.DEFAULT_FILTER_PAGE_SIZE, 0);
      int offset =
          OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
              ? resultsPaginator.getCurrentOffset()
              : 0;
      Map<String, Object> prefetchData =
          executor
              .getAdapter()
              .getDriver()
              .getPrefetch(executor.getAdapter().getIdentityModelClass());
      Set<IdentityModel> allResults =
          executor
              .getAdapter()
              .getDriver()
              .getAll(
                  executor.getAdapter().getIdentityModelClass(),
                  new ResultsFilter(),
                  SearchExecutor.getMaximumPageSizePaginator(executor.getAdapter()),
                  null,
                  prefetchData);
      final String filterValue =
          AdapterValueTypeConverter.readSingleAttributeValueAsString(equalsFilter.getAttribute());
      Set<IdentityModel> filteredResults = new LinkedHashSet<>();
      allResults.stream()
          .filter(
              identity ->
                  StringUtils.equalsIgnoreCase(
                      filterValue,
                      identity.getValueBySearchableAttributeName(equalsFilter.getName())))
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
}
