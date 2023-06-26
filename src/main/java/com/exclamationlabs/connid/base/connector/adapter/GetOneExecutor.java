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

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.AttributeFilter;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

public class GetOneExecutor {

  private GetOneExecutor() {}

  // Check for 'getOne' by UID execution
  // filter = EqualsFilter AND attribute is Id attribute
  // HAPPY OUTCOME 1: getOne (by UID) is invoked (may or may not have been a match sent to result
  // handler)
  // SAD ERROR OUTCOME: UID value supplied is blank/empty
  protected static boolean byUID(
      SearchExecutor executor,
      Filter filter,
      ResultsHandler resultsHandler,
      OperationOptions options)
      throws InvalidAttributeValueException {
    boolean executeGetOne = false;
    if (filter instanceof AttributeFilter) {
      AttributeFilter attributeFilter = (AttributeFilter) filter;
      if (StringUtils.equalsIgnoreCase(Uid.NAME, attributeFilter.getAttribute().getName())
          && attributeFilter instanceof EqualsFilter) {
        String uidValue =
            AdapterValueTypeConverter.readSingleAttributeValueAsString(
                attributeFilter.getAttribute());
        if (uidValue == null) {
          throw new InvalidAttributeValueException(
              "UID value cannot be empty/null for Equals search by UID");
        }
        IdentityModel singleItem =
            executor
                .getAdapter()
                .getDriver()
                .getOne(
                    executor.getAdapter().getIdentityModelClass(), uidValue, options.getOptions());
        executeGetOne = true;
        if (singleItem != null) {
          executor
              .getAdapter()
              .passSetToResultsHandler(resultsHandler, Collections.singleton(singleItem), false);
        }
      }
    }
    return executeGetOne;
  }

  // Check for 'getOne' by Name execution
  // filter = EqualsFilter AND attribute is Name attribute AND getOneByName() is false
  // AND getSearchResultsContainNameAttribute() is false - throw error
  // OR filter = EqualsFilter AND attribute is Name attribute AND getOneByName() is true - call API
  // getOneByName endpoint
  // OR filter = EqualsFilter AND attribute is Name attribute and
  // getSearchResultsContainNameAttribute() is true - derive using max results
  // OR filter = ContainsFilter AND attribute is Name attribute AND getOneByName() is true
  // AND getSearchResultsContainsNameAttribute() is false - call API getOneByName endpoint
  // Unless error, HAPPY OUTCOME 2: getOneByName is invoked (may or may not have been a match sent
  // to result handler)
  protected static boolean byName(
      SearchExecutor executor, Filter filter, ResultsHandler resultsHandler)
      throws InvalidAttributeValueException {
    boolean executeGetOneByName = false;
    boolean callGetOneByNameAPI = false;
    if (filter instanceof AttributeFilter) {
      AttributeFilter attributeFilter = (AttributeFilter) filter;
      final String filterValue =
          AdapterValueTypeConverter.readSingleAttributeValueAsString(
              attributeFilter.getAttribute());
      if (StringUtils.equalsIgnoreCase(Name.NAME, attributeFilter.getAttribute().getName())) {

        if (attributeFilter instanceof EqualsFilter) {
          if ((!executor.getEnhancedAdapter().getSearchResultsContainsNameAttribute())
              && (!executor.getEnhancedAdapter().getOneByName())) {
            throw new InvalidAttributeValueException(
                String.format(
                    "Retrieval by NAME equals `%s` not supported since %s sourceAPI does not list name in results or provide search by name.",
                    filterValue, executor.getAdapter().getClass().getSimpleName()));
          }
          executeGetOneByName = true;
          if (executor.getEnhancedAdapter().getOneByName()) {
            callGetOneByNameAPI = true;
          }

        } else if (attributeFilter instanceof ContainsFilter
            && executor.getEnhancedAdapter().getOneByName()
            && (!executor.getEnhancedAdapter().getSearchResultsContainsNameAttribute())) {
          callGetOneByNameAPI = true;
          executeGetOneByName = true;
        }

        if (executeGetOneByName) {
          if (callGetOneByNameAPI) {
            IdentityModel singleItem =
                executor
                    .getAdapter()
                    .getDriver()
                    .getOneByName(executor.getAdapter().getIdentityModelClass(), filterValue);
            if (singleItem != null) {
              executor
                  .getAdapter()
                  .passSetToResultsHandler(
                      resultsHandler, Collections.singleton(singleItem), false);
            }
          } else {
            // Find single name using API max results
            Set<IdentityModel> pageOfIdentityResults =
                executor
                    .getAdapter()
                    .getDriver()
                    .getAll(
                        executor.getAdapter().getIdentityModelClass(),
                        new ResultsFilter(),
                        SearchExecutor.getMaximumPageSizePaginator(executor.getAdapter()),
                        null);
            Optional<IdentityModel> match =
                pageOfIdentityResults.stream()
                    .filter(
                        identity ->
                            StringUtils.equalsIgnoreCase(
                                filterValue, identity.getIdentityNameValue()))
                    .findFirst();
            if (match.isPresent()) {
              IdentityModel fetchedIdentity;
              if (!executor.getEnhancedAdapter().getSearchResultsContainsAllAttributes()) {
                fetchedIdentity =
                    executor
                        .getAdapter()
                        .getDriver()
                        .getOne(
                            executor.getAdapter().getIdentityModelClass(),
                            match.get().getIdentityIdValue(),
                            null);
              } else {
                fetchedIdentity = match.get();
              }
              executor
                  .getAdapter()
                  .passSetToResultsHandler(
                      resultsHandler, Collections.singleton(fetchedIdentity), false);
            }
          }
        }
      }
    }
    return executeGetOneByName;
  }
}
