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
}
