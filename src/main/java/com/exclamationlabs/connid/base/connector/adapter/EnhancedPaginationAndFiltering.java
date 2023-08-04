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

import java.util.Set;

/**
 * For Base Connector 4.0 and beyond, ALL BaseAdapters should begin implementing this interface. Its
 * purpose is to instruct the Base Connector framework of some important information about what
 * search results and capabilities the source API does (or doesn't) support.
 */
public interface EnhancedPaginationAndFiltering {

  /**
   * This should only be true if the getAll invocation of a source API for ObjectClass returns all
   * relevant information needed to fully populate attributes corresponding with the respective
   * IdentityModel.
   *
   * @return true if all data is returned from getAll, otherwise false.
   */
  boolean getSearchResultsContainsAllAttributes();

  /**
   * Return true if the NAME attribute returned by the source API/invocator is returned when a
   * getAll is performed. Note that as of Base Connector 4.0 and above, NAME *must* be a unique
   * value for the Object Class.
   *
   * @return Return true if unique NAME attribute is obtainable from getAll, false otherwise.
   */
  boolean getSearchResultsContainsNameAttribute();

  /**
   * Return true if the source API/invocator provides a performant way to lookup the Identity by
   * unique NAME (not UID).
   *
   * @return Is true if source API, driver and invocator support and implement getOneByName()
   *     method.
   */
  boolean getOneByName();

  /**
   * Return all the Attribute names (as Strings) that are returned by a getAll invocation.
   *
   * @return all applicable attribute names.
   */
  Set<String> getSearchResultsAttributesPresent();

  /**
   * If true, for any filter that is received (other than Equals UID), a full import of identities
   * is required to obtain the data. Note that for performance reasons this should probably only be
   * used for adapters that have getSearchResultsContainsAllAttributes() = true, so subsequent
   * getOne's do not need to be invoked.
   *
   * @return true if all filtering requires a full import to proceed
   */
  boolean getFilteringRequiresFullImport();

  /**
   * Specify the number of threads used when subsequent API requests are made to retrieve full
   * identity details while returning a set of identities. The default implementation is 1 (no
   * multithreading).
   *
   * @return The maximum number of executable threads to spawn at a given time. Anything less than 2
   *     will result in normal execution and multithreading of requests. The number of threads
   *     started will not exceed the number of identities in the set in context (usually
   *     corresponding to page size)
   */
  default Integer getSubsequentRequestThreadCount() {
    return 1;
  }

  /**
   * For cases where a full import runs and the API supports pagination, this thread count controls
   * the number of threads that will be used to retrieve pages of information. Note that this should
   * only be set for Adapters that implement PaginationCapableSource.
   *
   * @return The maximum number of executable threads to spawn at a given time for full import
   */
  default Integer getImportUsingPaginationThreadCount() {
    return 1;
  }
}
