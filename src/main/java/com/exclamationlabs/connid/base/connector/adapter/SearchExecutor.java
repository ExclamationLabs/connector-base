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
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.*;

/**
 * For Base Connector 4.0 and higher, SearchExecutor coordinates and executes getAll (search)
 * requests, possibly with search filter and pagination input provided. It is invoked solely by
 * BaseAdapter.
 */
public class SearchExecutor {

  static final int ABSOLUTE_RESULTS_MAX = 999999;
  public static final int DEFAULT_FILTER_PAGE_SIZE = 20;

  private final BaseAdapter<?, ?> adapter;
  private final EnhancedPaginationAndFiltering enhancedAdapter;

  public SearchExecutor(BaseAdapter<?, ?> adapterIn) {
    this.adapter = adapterIn;
    this.enhancedAdapter = (EnhancedPaginationAndFiltering) adapterIn;
  }

  /**
   * Execute a search result, using both the Source API and internal routines (Java streams) based
   * on data available from invoking a basic getAll request.
   *
   * @param filter Filter provided for search criteria (EqualsFilter, ContainsFilter, AndFilter)
   * @param resultsHandler The ResultHandler that will receive result object/attribute data.
   * @param options OperationOptions object giving inbound pagination info.
   * @return SearchResult object containing limited known information about the search result.
   * @throws InvalidAttributeValueException If requested filter information provided was invalid or
   *     search could not be performed due to performance limitations.
   */
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
    boolean validPagingValuesSupplied =
        OperationOptionsDataFinder.hasValidPagingOptions(options.getOptions());
    if (filter == null && validPagingValuesSupplied) {
      SearchResult result = executePaginationOnly(resultsHandler, options);
      if (result != null) {
        return result;
      }
    }

    // ImportAll execution
    if (filter == null && (!validPagingValuesSupplied)) {
      if (ImportAllExecutor.execute(this, resultsHandler)) {
        return new SearchResult(); // Search Results not relevant for importAll
      }
    }

    // EqualsFilter handling for non UID/Name Attribute
    if (filter instanceof EqualsFilter) {
      return EqualsFilterExecutor.execute(this, (EqualsFilter) filter, resultsHandler, options);
    }

    // ContainsFilter handling for non UID/Name Attribute
    if (filter instanceof ContainsFilter) {
      return ContainsFilterExecutor.execute(this, (ContainsFilter) filter, resultsHandler, options);
    }

    if (filter == null) {
      throw new InvalidAttributeValueException("Unexpected null filter value encountered.");
    }

    // TODO: support AndFilter
    // Complexity Limitations:
    // All restrictions on AndFilter implemented in FilterValidator
    // All filter attributes involved in AND's must be natively supported by API
    // (in getContainsFilterAttributes() or getEqualsFilterAttributes()) ... OR
    // All filter attributes involved in AND's must be part of getSearchResultsAttributesPresent()
    // ... API Filters and Base-implemented filters cannot execute in tandem
    return AndFilterExecutor.execute(this, (AndFilter) filter, resultsHandler, options);
  }

  static SearchResult executeAPIFilter(
      BaseAdapter<?, ?> adapter,
      EnhancedPaginationAndFiltering enhancedAdapter,
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
    matchingResults =
        performManualPaginationIfNeeded(enhancedAdapter, matchingResults, resultsPaginator);
    processResultsPage(adapter, enhancedAdapter, matchingResults, resultsHandler);
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
        processResultsPage(adapter, enhancedAdapter, pageOfIdentityResults, resultsHandler);
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
        processResultsPage(adapter, enhancedAdapter, allIdentityResults, resultsHandler);
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
          processResultsPage(adapter, enhancedAdapter, pageOfResults, resultsHandler);
          return new SearchResult(null, -1, false);
        }
      }
    }
    return null;
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

  static Set<IdentityModel> performManualPaginationIfNeeded(
      EnhancedPaginationAndFiltering enhancedAdapter,
      Set<IdentityModel> results,
      ResultsPaginator resultsPaginator) {
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

  static void processResultsPage(
      BaseAdapter<?, ?> adapter,
      EnhancedPaginationAndFiltering enhancedAdapter,
      Set<IdentityModel> results,
      ResultsHandler resultsHandler) {
    if (!enhancedAdapter.getSearchResultsContainsAllAttributes()) {
      // IdentityModels do not contain all attributes, need to call getOne for each.
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

  /**
   * ConnId/Midpoint uses a 1-based index for offset. Java streams and most other API's use a
   * 0-based index for offset. This utility method makes that correction. Offsets CANNOT be
   * negative.
   *
   * @param input Inbound offset value that is 1-based
   * @return 0-based offset value.
   */
  static int correctConnIdOffset(Integer input) {
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
