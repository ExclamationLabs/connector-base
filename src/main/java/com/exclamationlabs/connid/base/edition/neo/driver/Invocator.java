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

package com.exclamationlabs.connid.base.edition.neo.driver;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import java.util.Map;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

/**
 * A DriverInvocator belongs to a Driver and is responsible for making the calls on a destination
 * system to read, create, update and delete objects of a particular data type on the destination
 * system. Note: Not all of the operations have to necessarily be supported (example, if the
 * destination system does not support or permit deletion of that object type, having a no-op or
 * throwing an exception is acceptable).
 *
 * @param <D> Concrete Driver implementation pertaining to this Invocator.
 * @param <T> IdentityModel implementation pertaining to an object type on the destination system.
 */
public interface Invocator<
    T extends ConnectorConfiguration, D extends Driver<T>, M extends IdentityModel> {

  /**
   * Get all existing objects of this invocator's particular type on the destination system, using
   * supplied filter attribute and value. Unless overriden, default behavior is to presume filtering
   * is not supported, and simply execute the getAll method.
   *
   * @param driver Driver belonging to this Invocator and providing interaction with the applicable
   *     destination system.
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param filter Object possibly containing an attribute value to filter upon.
   * @param paginator Object possibly containing current pagination information for results being
   *     processed. Fields in this pagination could/should be updated by the invocator as results
   *     are processed for the connector.
   * @param resultCap The maximum number of results that should be returned by getAll. This can be
   *     null but if present will override the pagination pageSize.
   * @param prefetchDataMap Map of prefetch data applicable to the Identity Model and that may be
   *     understood by the invocator.
   * @return A list of all IdentityModel objects of this Invocator's particular type. Can be null or
   *     an empty list if the destination system currently has no records.
   * @throws ConnectorException If get request failed, was invalid or was not permitted.
   */
  Set<M> getAll(
      D driver,
      T configuration,
      ResultsFilter filter,
      ResultsPaginator paginator,
      Integer resultCap,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException;

  /**
   * Get a single object of this invocator's particular type on the destination system that matches
   * the given identifier.
   *
   * @param driver Driver belonging to this Invocator and providing interaction with the applicable
   *     destination system.
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param objectId String holding the identifier for the object being sought on the destination
   *     system.
   * @param prefetchDataMap Map of prefetch data applicable to the Identity Model and that may be
   *     understood by the invocator.
   * @return An IdentityModel object of this Invocator's particular type that corresponds to the
   *     given identifier. Can return null if no record matching the id was found on the destination
   *     system.
   * @throws ConnectorException If get request failed, was invalid or was not permitted.
   */
  M getOne(D driver, T configuration, String objectId, Map<String, Object> prefetchDataMap)
      throws ConnectorException;

  /**
   * Get a single object of this invocator's particular type on the destination system that matches
   * the given name value.
   *
   * <p>Default behavior is to throw UnsupportedOperationException. Underlying driver/invocator must
   * both provide their own support for this method if it supported and needed.
   *
   * @param driver Driver belonging to this Invocator and providing interaction with the applicable
   *     destination system.
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param objectName String holding the identifier for the object being sought on the destination
   *     system.
   * @param prefetchDataMap Map of prefetch data applicable to the Identity Model and that may be
   *     understood by the invocator.
   * @return An IdentityModel object of this Invocator's particular type that corresponds to the
   *     given name value. Can return null if no record matching the name was found on the
   *     destination system.
   * @throws ConnectorException If get request failed, was invalid or was not permitted.
   */
  default M getOneByName(
      D driver, T configuration, String objectName, Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    throw new UnsupportedOperationException("DriverInvocator does not support getOneByName");
  }
}
