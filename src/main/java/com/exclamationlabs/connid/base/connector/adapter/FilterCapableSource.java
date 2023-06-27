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
 * Since Base Connector 4.0, this interface should be implemented by BaseAdapter implementations
 * when Source API is capable of some filtering capabilities.
 */
public interface FilterCapableSource {

  /**
   * A set of the Attributes that the Source API/invocator can filter upon with Equals matching.
   * NOTE: UID matching is supported by all Invocators via getOne, and the ability to NAME match is
   * dictated by getOneByName() methods, so neither of those attributes (or their Native name
   * equivalents) should be included in the Set.
   *
   * @return Set describing which Attributes Equals matches that the Source API/invocator can
   *     handle.
   */
  Set<String> getEqualsFilterAttributes();

  /**
   * A set of the Attributes that the Source API/invocator can filter upon with Contains matching.
   * NOTE: This should include __UID__ and __NAME__ (and their Native name equivalents) if the
   * Source API/invocator supports Contains searches of partial strings of those unique identifiers.
   *
   * @return Set describing which Attributes Contains matches that the Source API/invocator can
   *     handle.
   */
  Set<String> getContainsFilterAttributes();
}
