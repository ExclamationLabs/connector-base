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
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.SearchResult;
import org.identityconnectors.framework.common.objects.filter.AndFilter;
import org.identityconnectors.framework.common.objects.filter.AttributeFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

/**
 * AndFilter implemented with some limitations to overly complex logic: All restrictions on
 * AndFilter implemented in FilterValidator All filter attributes involved in AND's must be natively
 * supported by API (in getContainsFilterAttributes() or getEqualsFilterAttributes()) ... OR All
 * filter attributes involved in AND's must be part of getSearchResultsAttributesPresent() ...
 * basically, API Filters and Base-implemented filters cannot execute in tandem
 *
 * <p>NOTE: At current time, ConnId AbstractFilterTranslator createAndExpression only supports two
 * filters, no more.
 */
public class AndFilterExecutor {

  private AndFilterExecutor() {}

  protected static SearchResult execute(
      SearchExecutor executor,
      AndFilter andFilter,
      ResultsHandler resultsHandler,
      OperationOptions options)
      throws InvalidAttributeValueException {

    // AndFilter pre-examination
    Optional<Filter> checkFilter = andFilter.getFilters().stream().findFirst();
    if (!checkFilter.isPresent()) {
      throw new InvalidAttributeValueException("Unexpected empty AndFilter");
    }
    AttributeFilter firstFilter = (AttributeFilter) checkFilter.get();
    boolean equalsType = firstFilter instanceof EqualsFilter;

    if (executor.getEnhancedAdapter() instanceof FilterCapableSource) {
      FilterCapableSource filterAdapter = (FilterCapableSource) executor.getEnhancedAdapter();
      if (equalsType
          && filterAdapter.getEqualsFilterAttributes() != null
          && filterAdapter.getEqualsFilterAttributes().contains(firstFilter.getName())) {
        // execute Equals
        return executeApiSearch(executor, true, andFilter, resultsHandler, options);
      } else if (equalsType
          && filterAdapter.getContainsFilterAttributes() != null
          && filterAdapter.getContainsFilterAttributes().contains(firstFilter.getName())) {
        // execute Contains
        return executeApiSearch(executor, false, andFilter, resultsHandler, options);
      } else if ((!equalsType)
          && filterAdapter.getContainsFilterAttributes() != null
          && filterAdapter.getContainsFilterAttributes().contains(firstFilter.getName())) {
        // execute Contains
        return executeApiSearch(executor, false, andFilter, resultsHandler, options);
      } else if ((!equalsType)
          && filterAdapter.getEqualsFilterAttributes() != null
          && filterAdapter.getEqualsFilterAttributes().contains(firstFilter.getName())) {
        // execute Equals
        return executeApiSearch(executor, true, andFilter, resultsHandler, options);
      }
    }

    // If we reached this point, API AndFilter execution does not seem possible.  See if
    // Manual base connector support is possible
    boolean attemptManualSearch =
        executor.getEnhancedAdapter().getSearchResultsContainsAllAttributes();

    if (!attemptManualSearch) {
      for (Filter inner : andFilter.getFilters()) {
        AttributeFilter innerCheck = (AttributeFilter) inner;
        if (!executor
            .getEnhancedAdapter()
            .getSearchResultsAttributesPresent()
            .contains(innerCheck.getName())) {
          throw new InvalidAttributeValueException(
              String.format(
                  "For adapter %s, attribute %s filter is not supported by source API or available data",
                  executor.getAdapter().getClass().getSimpleName(), innerCheck.getName()));
        }
      }
    }
    return executeManualSearch(executor, equalsType, andFilter, resultsHandler, options);
  }

  private static SearchResult executeApiSearch(
      SearchExecutor executor,
      boolean equalsSearch,
      AndFilter andFilter,
      ResultsHandler resultsHandler,
      OperationOptions options) {
    Map<String, String> filterDataMap = new HashMap<>();
    for (Filter current : andFilter.getFilters()) {
      AttributeFilter currentFilter = (AttributeFilter) current;
      filterDataMap.put(
          currentFilter.getName(),
          AdapterValueTypeConverter.readSingleAttributeValueAsString(currentFilter.getAttribute()));
    }
    ResultsFilter resultsFilter =
        new ResultsFilter(
            filterDataMap, equalsSearch ? FilterType.EqualsFilter : FilterType.ContainsFilter);

    ResultsPaginator resultsPaginator =
        OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
            ? new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset())
            : new ResultsPaginator(SearchExecutor.DEFAULT_FILTER_PAGE_SIZE, 0);
    Set<IdentityModel> filteredResults =
        executor
            .getAdapter()
            .getDriver()
            .getAll(
                executor.getAdapter().getIdentityModelClass(),
                resultsFilter,
                resultsPaginator,
                null);
    SearchExecutor.processResultsPage(
        executor.getAdapter(), executor.getEnhancedAdapter(), filteredResults, resultsHandler);
    return new SearchResult(null, -1, false);
  }

  private static SearchResult executeManualSearch(
      SearchExecutor executor,
      boolean equalsSearch,
      AndFilter andFilter,
      ResultsHandler resultsHandler,
      OperationOptions options) {
    ResultsPaginator resultsPaginator =
        OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
            ? new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset())
            : new ResultsPaginator(SearchExecutor.ABSOLUTE_RESULTS_MAX, 0);
    int offset =
        OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
            ? resultsPaginator.getCurrentOffset()
            : 0;
    Set<IdentityModel> allResults =
        executor
            .getAdapter()
            .getDriver()
            .getAll(
                executor.getAdapter().getIdentityModelClass(),
                new ResultsFilter(),
                SearchExecutor.getMaximumPageSizePaginator(executor.getAdapter()),
                null);

    List<Set<IdentityModel>> filteredResultList = new ArrayList<>();
    for (Filter current : andFilter.getFilters()) {
      AttributeFilter currentFilter = (AttributeFilter) current;
      final String filterValue =
          AdapterValueTypeConverter.readSingleAttributeValueAsString(currentFilter.getAttribute());
      Set<IdentityModel> filteredResults = new LinkedHashSet<>();
      if (equalsSearch) {
        allResults.stream()
            .filter(
                identity ->
                    StringUtils.equalsIgnoreCase(
                        filterValue,
                        identity.getValueBySearchableAttributeName(currentFilter.getName())))
            .skip(SearchExecutor.correctConnIdOffset(offset))
            .limit(resultsPaginator.getPageSize())
            .forEachOrdered(filteredResults::add);
      } else {
        String whatIsIt =
            allResults.stream()
                .findFirst()
                .get()
                .getValueBySearchableAttributeName(currentFilter.getName());
        allResults.stream()
            .filter(
                identity ->
                    StringUtils.containsIgnoreCase(
                        identity.getValueBySearchableAttributeName(currentFilter.getName()),
                        filterValue))
            .skip(SearchExecutor.correctConnIdOffset(offset))
            .limit(resultsPaginator.getPageSize())
            .forEachOrdered(filteredResults::add);
      }
      filteredResultList.add(filteredResults);
    }

    Set<IdentityModel> exclusiveFilteredResults = performAndLogic(filteredResultList);

    SearchExecutor.processResultsPage(
        executor.getAdapter(),
        executor.getEnhancedAdapter(),
        exclusiveFilteredResults,
        resultsHandler);
    return new SearchResult(null, -1, false);
  }

  private static Set<IdentityModel> performAndLogic(List<Set<IdentityModel>> filteredResultList) {
    Set<IdentityModel> buildSet = filteredResultList.get(0);
    for (int xx = 1; xx < filteredResultList.size(); xx++) {
      Set<IdentityModel> andSet = filteredResultList.get(xx);
      buildSet.removeIf(current -> !andSet.contains(current));
    }
    return buildSet;
  }
}
