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

/**
 * Since Base Connector 4.0, this interface should be implemented by all BaseAdapters whose
 * respective source API can perform pagination in a way that is compatible with ConnId/Midpoint
 * pagination implementation.
 */
public interface PaginationCapableSource {

  /**
   * Return true to indicate that source API has a known results per page maximum that it enforces.
   * Return false if no maximum is known or there is no tangible limit.
   *
   * @return True/false as described.
   */
  boolean hasSearchResultsMaximum();

  /**
   * Return the maximum results per page that the source API enforces.
   *
   * @return Maximum results per page (Integer above 1) or 0 or null if there is no known maximum.
   */
  Integer getSearchResultsMaximum();
}
