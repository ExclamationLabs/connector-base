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
import com.exclamationlabs.connid.base.connector.filter.FilterValidator;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.connector.util.OperationOptionsDataFinder;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.*;

/** */
public class SearchExecutor {

  private static final int ABSOLUTE_RESULTS_MAX = 999999; // TODO: don't we need this?!
  public static final int DEFAULT_FILTER_PAGE_SIZE = 20;

  private final BaseAdapter<?, ?> adapter;
  private final EnhancedPaginationAndFiltering enhancedAdapter;

  public SearchExecutor(BaseAdapter<?, ?> adapterIn) {
    this.adapter = adapterIn;
    this.enhancedAdapter = (EnhancedPaginationAndFiltering) adapterIn;
  }

  public SearchResult execute(
      Filter filter, ResultsHandler resultsHandler, OperationOptions options)
      throws InvalidAttributeValueException {

    // Initial Filter Validation (if filter is not null):
    // Examine filter type: supported types: AndFilter, ContainsFilter and EqualsFilter.  Throw
    // error if
    // unsupported Filter type was used.
    // Also validate the contents of the filter and make sure value(s) are not blank.
    if (filter != null) {
      FilterValidator.validate(filter, adapter);

      if (GetOneExecutor.byUID(this, filter, resultsHandler, options)) {
        return new SearchResult(); // Results information not meaningful for single item response
      }

      if (GetOneExecutor.byName(this, filter, resultsHandler)) {
        return new SearchResult(); // Results information not meaningful for single item response
      }
    }

    // Check for pagination-only scenario:
    // OperationOptions pagination values are submitted AND filter is NULL
    // (A) When adapter has PaginationCapableSource implemented, invoke getAll with
    // resultsPagination and dummy Filter.
    // - Then invoke getOne on each item UNLESS getSearchResultsContainAllAttributes is true and
    // - pass to results handler
    // (B) When adapter does NOT have PaginationCapableSource implemented, invoke getAll with dummy
    // Pagination and dummy Filter.
    // - Splice the correct block of records per the ResultsPagination and place that block in a new
    // Set
    // - Perform getOne on each in that Set UNLESS getSearchResultsContainAllAttributes is true
    // - pass to results handler
    // For both (A) and (B), HAPPY OUTCOME 3: getAll for desired single page is invoked
    boolean validPagingValuesSupplied =
        OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions());
    if (filter == null && validPagingValuesSupplied) {
      SearchResult result = executePaginationOnly(resultsHandler, options);
      if (result != null) {
        return result;
      }
    }

    if (filter == null && (!validPagingValuesSupplied)) {
      if (ImportAllExecutor.execute(this, resultsHandler)) {
        return new SearchResult(); // Search Results not relevant for importAll
      }
    }

    // EqualsFilter handling for non UID/Name Attribute
    if (filter instanceof EqualsFilter) {
      return executeEqualsFilterLogic((EqualsFilter) filter, resultsHandler, options);
    }

    // ContainsFilter handling for non UID/Name Attribute
    if (filter instanceof ContainsFilter) {
      return executeContainsFilterLogic((ContainsFilter) filter, resultsHandler, options);
    }

    // TODO: support AndFilter
    // Complexity Limitations:
    // All Filters inside AndFilter must be contain's
    // All filter attributes involved in AND's must be natively supported by API ... OR
    // All filter attributes involved in AND's must be part of getContainsFilterAttributes() or
    // getEqualsFilterAttributes()
    throw new InvalidAttributeValueException("AndFilter not yet supported");

    // Advanced Filter validations:
    // 1) AndFilter validation - make sure all inside are ContainsFilter or EqualsFilter, if not
    // throw error
    // 1.1) AndFilter - check each ContainsFilter or EqualsFilter.  For each:
    //    If ContainsFilter and attribute IS found in getContainsFilterAttributes(), accept and
    // proceed
    //    If ContainsFilter and attribute IS NOT found in getContainsFilterAttributes()
    //    - check if it's in getEqualsFilterAttribute().  If not, throw error.  If so, change filter
    // type to EqualsFilter.
    //    If EqualsFilter, check if it's in getEqualsFilterAttribute().  If not throw error. If so,
    // accept and proceed
    // 2) ContainsFilter - if in getContainsFilterAttributes(), accept and proceed
    //    - if NOT found in getContainsFilterAttributes()
    //    - check if it's in getEqualsFilterAttribute().  If not, throw error.  If so, change filter
    // type to EqualsFilter.
    // 3) EqualsFilter - if in getEqualsFilterAttribute(), accept and proceed.  If not throw error.

    // Construct ResultsFilter object based on Filter (different constructor for And vs other)

    // Scenario 1: Execute search (when adapter has PaginationCapableSource implemented)
    // call getAll with resultsPagination and resultsFilter.  Obtain results.
    // If getSearchResultsContainsAllAttributes() is false, iterate over items in Set
    // and invoke getOne on each and store responses in a new Set
    // Pass new Set with full detail to result handler

    // Scenario 2: Execute search (when adapter does NOT have PaginationCapableSource implemented)
    // call getAll with resultsPagination (has no values) and resultsFilter.  Obtain results.
    // Using ResultsPagination, extract the block of Set values corresponding to the desired page.
    // If getSearchResultsContainsAllAttributes() is false, iterate over items in Set (page block)
    // and invoke getOne on each and store responses in a new Set
    // Pass new Set with full detail to result handler

    // HAPPY OUTCOME 5: for Scenario 1 or 2, getAll perform filtered returning for a single page of
    // results is invoked.

  }

  private SearchResult executeEqualsFilterLogic(
      EqualsFilter equalsFilter, ResultsHandler resultsHandler, OperationOptions options)
      throws InvalidAttributeValueException {
    if ((!enhancedAdapter.getSearchResultsAttributesPresent().contains(equalsFilter.getName()))
        && (!enhancedAdapter.getSearchResultsContainsAllAttributes())) {
      if (enhancedAdapter instanceof FilterCapableSource) {
        FilterCapableSource filterCapable = (FilterCapableSource) enhancedAdapter;
        if (filterCapable.getEqualsFilterAttributes().contains(equalsFilter.getName())) {
          return executeAPIFilter(equalsFilter, resultsHandler, options, FilterType.EqualsFilter);
        } else if (filterCapable.getContainsFilterAttributes().contains(equalsFilter.getName())) {
          // Fallback option - must search using Contains filter since it is only manner available
          ResultsPaginator resultsPaginator =
              OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
                  ? new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset())
                  : new ResultsPaginator(DEFAULT_FILTER_PAGE_SIZE, 0);

          Set<IdentityModel> matchingResults =
              adapter
                  .getDriver()
                  .getAll(
                      adapter.getIdentityModelClass(),
                      new ResultsFilter(
                          equalsFilter.getName(),
                          AdapterValueTypeConverter.readSingleAttributeValueAsString(
                              equalsFilter.getAttribute()),
                          FilterType.ContainsFilter),
                      resultsPaginator,
                      null);
          processResultsPage(matchingResults, resultsHandler);
          return new SearchResult(
              resultsPaginator.getTokenAsString(), -1, resultsPaginator.getNoMoreResults());
        } else {
          throw new InvalidAttributeValueException(
              String.format(
                  "%s source API has limited filter capabilities and is not able to filter based on attribute %s",
                  enhancedAdapter.getClass().getSimpleName(), equalsFilter.getName()));
        }
      } else {

        throw new InvalidAttributeValueException(
            String.format(
                "%s source API is not able to filter based on attribute %s",
                enhancedAdapter.getClass().getSimpleName(), equalsFilter.getName()));
      }
    } else {
      if (enhancedAdapter instanceof FilterCapableSource) {
        FilterCapableSource filterCapable = (FilterCapableSource) enhancedAdapter;
        if (filterCapable.getEqualsFilterAttributes().contains(equalsFilter.getName())
            || filterCapable.getContainsFilterAttributes().contains(equalsFilter.getName())) {

          return executeAPIFilter(equalsFilter, resultsHandler, options, FilterType.EqualsFilter);
        }
      }
      // No direct API filter, get all results up to API max and return matches
      ResultsPaginator resultsPaginator =
          OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
              ? new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset())
              : new ResultsPaginator(DEFAULT_FILTER_PAGE_SIZE, 0);
      int offset =
          OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
              ? resultsPaginator.getCurrentOffset()
              : 0;
      Set<IdentityModel> allResults =
          adapter
              .getDriver()
              .getAll(
                  adapter.getIdentityModelClass(),
                  new ResultsFilter(),
                  getMaximumPageSizePaginator(getAdapter()),
                  null);
      final String filterValue =
          AdapterValueTypeConverter.readSingleAttributeValueAsString(equalsFilter.getAttribute());
      Set<IdentityModel> filteredResults = new LinkedHashSet<>();
      allResults.stream()
          .filter(
              identity ->
                  StringUtils.equalsIgnoreCase(
                      filterValue,
                      identity.getValueBySearchableAttributeName(equalsFilter.getName())))
          .skip(correctConnIdOffset(offset))
          .limit(resultsPaginator.getPageSize())
          .forEachOrdered(filteredResults::add);
      processResultsPage(filteredResults, resultsHandler);
      return new SearchResult(null, -1, false);
    }
  }

  private SearchResult executeContainsFilterLogic(
      ContainsFilter containsFilter, ResultsHandler resultsHandler, OperationOptions options)
      throws InvalidAttributeValueException {
    ResultsPaginator resultsPaginator =
        OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
            ? new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset())
            : new ResultsPaginator(DEFAULT_FILTER_PAGE_SIZE, 0);
    if ((!enhancedAdapter.getSearchResultsAttributesPresent().contains(containsFilter.getName()))
        && !(enhancedAdapter.getSearchResultsContainsAllAttributes())) {
      if (enhancedAdapter instanceof FilterCapableSource) {
        FilterCapableSource filterCapable = (FilterCapableSource) enhancedAdapter;
        if (filterCapable.getContainsFilterAttributes().contains(containsFilter.getName())) {
          return executeAPIFilter(
              containsFilter, resultsHandler, options, FilterType.ContainsFilter);
        } else if (filterCapable.getEqualsFilterAttributes().contains(containsFilter.getName())) {

          Set<IdentityModel> matchingResults =
              adapter
                  .getDriver()
                  .getAll(
                      adapter.getIdentityModelClass(),
                      new ResultsFilter(
                          containsFilter.getName(),
                          AdapterValueTypeConverter.readSingleAttributeValueAsString(
                              containsFilter.getAttribute()),
                          FilterType.EqualsFilter),
                      resultsPaginator,
                      null);
          matchingResults = performManualPaginationIfNeeded(matchingResults, resultsPaginator);
          processResultsPage(matchingResults, resultsHandler);
          return new SearchResult(
              resultsPaginator.getTokenAsString(), -1, resultsPaginator.getNoMoreResults());
        } else {
          if (StringUtils.equals(Uid.NAME, containsFilter.getName())) {
            return performManualUidSearch(containsFilter, resultsPaginator, resultsHandler);
          } else if (enhancedAdapter.getSearchResultsContainsNameAttribute()
              && StringUtils.equals(Name.NAME, containsFilter.getName())) {
            return performManualNameSearch(containsFilter, resultsPaginator, resultsHandler);
          }
          throw new InvalidAttributeValueException(
              String.format(
                  "%s source API has limited filter capabilities and is not able to filter based on attribute %s",
                  enhancedAdapter.getClass().getSimpleName(), containsFilter.getName()));
        }
      } else {
        if (StringUtils.equals(Uid.NAME, containsFilter.getName())) {
          return performManualUidSearch(containsFilter, resultsPaginator, resultsHandler);
        } else if (enhancedAdapter.getSearchResultsContainsNameAttribute()
            && StringUtils.equals(Name.NAME, containsFilter.getName())) {
          return performManualNameSearch(containsFilter, resultsPaginator, resultsHandler);
        } else {
          throw new InvalidAttributeValueException(
              String.format(
                  "%s source API is not able to filter based on attribute %s",
                  enhancedAdapter.getClass().getSimpleName(), containsFilter.getName()));
        }
      }
    } else {
      if (enhancedAdapter instanceof FilterCapableSource) {
        FilterCapableSource filterCapable = (FilterCapableSource) enhancedAdapter;
        if (filterCapable.getContainsFilterAttributes().contains(containsFilter.getName())) {
          return executeAPIFilter(
              containsFilter, resultsHandler, options, FilterType.ContainsFilter);
        }
      }
      // No direct API filter, get all results up to API max and return matches
      int offset =
          OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
              ? resultsPaginator.getCurrentOffset()
              : 0;
      Set<IdentityModel> allResults =
          adapter
              .getDriver()
              .getAll(
                  adapter.getIdentityModelClass(),
                  new ResultsFilter(),
                  getMaximumPageSizePaginator(getAdapter()),
                  null);
      final String filterValue =
          AdapterValueTypeConverter.readSingleAttributeValueAsString(containsFilter.getAttribute());
      Set<IdentityModel> filteredResults = new LinkedHashSet<>();
      allResults.stream()
          .filter(
              identity ->
                  StringUtils.containsIgnoreCase(
                      identity.getValueBySearchableAttributeName(containsFilter.getName()),
                      filterValue))
          .skip(correctConnIdOffset(offset))
          .limit(resultsPaginator.getPageSize())
          .forEachOrdered(filteredResults::add);
      processResultsPage(filteredResults, resultsHandler);
      return new SearchResult(null, -1, false);
    }
  }

  private SearchResult executeAPIFilter(
      AttributeFilter attributeFilter,
      ResultsHandler resultsHandler,
      OperationOptions options,
      FilterType filterType) {
    ResultsPaginator resultsPaginator =
        OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions())
            ? new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset())
            : new ResultsPaginator(DEFAULT_FILTER_PAGE_SIZE, 0);

    Set<IdentityModel> matchingResults =
        adapter
            .getDriver()
            .getAll(
                adapter.getIdentityModelClass(),
                new ResultsFilter(
                    attributeFilter.getName(),
                    AdapterValueTypeConverter.readSingleAttributeValueAsString(
                        attributeFilter.getAttribute()),
                    filterType),
                resultsPaginator,
                null);
    matchingResults = performManualPaginationIfNeeded(matchingResults, resultsPaginator);
    processResultsPage(matchingResults, resultsHandler);
    return new SearchResult(
        resultsPaginator.getTokenAsString(), -1, resultsPaginator.getNoMoreResults());
  }

  protected SearchResult executePaginationOnly(
      ResultsHandler resultsHandler, OperationOptions options) {
    if (adapter instanceof PaginationCapableSource) {
      ResultsPaginator resultsPaginator =
          new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset());
      // Rely on driver/invocator/API to take in ResultsPaginator and give us the results for the
      // applicable page.
      Set<IdentityModel> pageOfIdentityResults =
          adapter
              .getDriver()
              .getAll(adapter.getIdentityModelClass(), new ResultsFilter(), resultsPaginator, null);
      if (adapter instanceof FilterCapableSource) {
        processResultsPage(pageOfIdentityResults, resultsHandler);
        return new SearchResult(
            resultsPaginator.getTokenAsString(), -1, resultsPaginator.getNoMoreResults());
      }
    } else {
      // API cannot handle pagination; need to get all results and splice the applicable page of
      // results here
      ResultsPaginator paginationData =
          new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset());
      Set<IdentityModel> allIdentityResults =
          adapter
              .getDriver()
              .getAll(
                  adapter.getIdentityModelClass(),
                  new ResultsFilter(),
                  new ResultsPaginator(options.getPageSize(), options.getPagedResultsOffset()),
                  null);

      if (allIdentityResults.size() <= paginationData.getPageSize()) {
        processResultsPage(allIdentityResults, resultsHandler);
        return new SearchResult(null, -1, true);
      } else {
        if (paginationData.getCurrentOffset() >= allIdentityResults.size()) {
          // If the offset is at or higher than the number of results present,
          // return nothing to reflect that there are no more results;
          return new SearchResult(null, -1, true);
        } else {
          Set<IdentityModel> pageOfResults = new LinkedHashSet<>();
          allIdentityResults.stream()
              .skip(correctConnIdOffset(paginationData.getCurrentOffset()))
              .limit(paginationData.getPageSize())
              .forEachOrdered(pageOfResults::add);
          processResultsPage(pageOfResults, resultsHandler);
          return new SearchResult(null, -1, false);
        }
      }
    }
    return null;
  }

  private SearchResult performManualNameSearch(
      ContainsFilter containsFilter,
      ResultsPaginator resultsPaginator,
      ResultsHandler resultsHandler) {
    Set<IdentityModel> matchingResults =
        adapter
            .getDriver()
            .getAll(
                adapter.getIdentityModelClass(),
                new ResultsFilter(),
                getMaximumPageSizePaginator(getAdapter()),
                null);
    Set<IdentityModel> filteredResults = new LinkedHashSet<>();
    matchingResults.stream()
        .filter(
            identity ->
                StringUtils.containsIgnoreCase(
                    identity.getIdentityNameValue(), containsFilter.getValue()))
        .skip(correctConnIdOffset(resultsPaginator.getCurrentOffset()))
        .limit(resultsPaginator.getPageSize())
        .forEachOrdered(filteredResults::add);
    processResultsPage(filteredResults, resultsHandler);
    return new SearchResult(null, -1, false);
  }

  private SearchResult performManualUidSearch(
      ContainsFilter containsFilter,
      ResultsPaginator resultsPaginator,
      ResultsHandler resultsHandler) {
    Set<IdentityModel> matchingResults =
        adapter
            .getDriver()
            .getAll(
                adapter.getIdentityModelClass(),
                new ResultsFilter(),
                getMaximumPageSizePaginator(getAdapter()),
                null);
    Set<IdentityModel> filteredResults = new LinkedHashSet<>();
    matchingResults.stream()
        .filter(
            identity ->
                StringUtils.containsIgnoreCase(
                    identity.getIdentityIdValue(), containsFilter.getValue()))
        .skip(correctConnIdOffset(resultsPaginator.getCurrentOffset()))
        .limit(resultsPaginator.getPageSize())
        .forEachOrdered(filteredResults::add);
    processResultsPage(filteredResults, resultsHandler);
    return new SearchResult(null, -1, false);
  }

  static ResultsPaginator getMaximumPageSizePaginator(BaseAdapter<?, ?> currentAdapter) {
    if (currentAdapter instanceof PaginationCapableSource) {
      PaginationCapableSource pageable = (PaginationCapableSource) currentAdapter;
      if (pageable.hasSearchResultsMaximum()) {
        return new ResultsPaginator(pageable.getSearchResultsMaximum());
      }
    }
    return new ResultsPaginator();
  }

  private Set<IdentityModel> performManualPaginationIfNeeded(
      Set<IdentityModel> results, ResultsPaginator resultsPaginator) {
    if (enhancedAdapter instanceof PaginationCapableSource) {
      return results;
    } else {
      Set<IdentityModel> pagedResults = new LinkedHashSet<>();
      results.stream()
          .skip(correctConnIdOffset(resultsPaginator.getCurrentOffset()))
          .limit(resultsPaginator.getPageSize())
          .forEachOrdered(pagedResults::add);
      return pagedResults;
    }
  }

  void processResultsPage(Set<IdentityModel> results, ResultsHandler resultsHandler) {
    if (!enhancedAdapter.getSearchResultsContainsAllAttributes()) {
      Set<IdentityModel> pageOfDetailedIdentities = new LinkedHashSet<>();
      for (IdentityModel identity : results) {
        IdentityModel identityWithDetails =
            adapter
                .getDriver()
                .getOne(
                    adapter.getIdentityModelClass(),
                    identity.getIdentityIdValue(),
                    Collections.emptyMap());
        pageOfDetailedIdentities.add(identityWithDetails);
      }
      adapter.passSetToResultsHandler(resultsHandler, pageOfDetailedIdentities, false);
    } else {
      adapter.passSetToResultsHandler(resultsHandler, results, false);
    }
  }

  private static int correctConnIdOffset(Integer input) {
    if (input == null || input <= 0) {
      return 0;
    } else {
      return input - 1;
    }
  }

  BaseAdapter<?, ?> getAdapter() {
    return adapter;
  }

  EnhancedPaginationAndFiltering getEnhancedAdapter() {
    return enhancedAdapter;
  }
}
