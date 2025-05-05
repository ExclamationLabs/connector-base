/*
    Copyright 2025 Exclamation Labs

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
 * An invocator works in tandem with a Driver and is responsible for making the calls on a
 * destination system to read objects of a particular object class (as indicated by the
 * IdentityModel ModelObjectClass annotation) on the destination system.
 *
 * <p>IMPORTANT NOTES: - There should only be one invocator implementation per object class per
 * connector. - Not all the operations have to necessarily be supported (example, if the destination
 * system does not support or permit deletion of that object type, having a no-op or throwing an
 * exception is acceptable). - The Invocator interface cannot be used for creating, updating or
 * deleting objects on the destination system. To support these operations in addition to read
 * operations (getOne and getAll), the FullAccessInvocator interface should be implemented instead
 * of this interface.
 *
 * @param <T> ConnectorConfiguration that applies to the configuration type used by the connector.
 * @param <D> Driver implementation pertaining to this connector.
 * @param <M> IdentityModel implementation pertaining to an object type on the destination system.
 */
public interface Invocator<
    T extends ConnectorConfiguration, D extends Driver<T>, M extends IdentityModel> {

  /**
   * This method needs to be implemented to inform the framework if the invocator supports native
   * pagination. 'true' should only be returned if destination API supports pagination using page
   * size and offset as integers. If the destination API doesn't support pagination at all, or it
   * has pagination strategies that use cookies, links, et al. (which this framework cannot
   * support), 'false' should be returned. If 'true' is returned, the invocator will be relied upon
   * to handle pagination for its getAll() requests using the ResultsPagination object. If 'false'
   * is returned, the framework will handle pagination internally.
   *
   * @param configuration Reference to Configuration object so that this invocator has access to
   *     configuration properties and values if needed.
   * @return True if the driver supports native pagination (using page size and numeric offset),
   *     false if it does not.
   */
  boolean supportsNativePagination(T configuration);

  /**
   * Get all existing objects of a particular object class type on the destination system, using
   * supplied filter attribute and value. Unless overriden, default behavior is to presume filtering
   * is not supported, and simply execute the getAll method.
   *
   * @param driver Driver belonging to this connector and providing interaction with the applicable
   *     destination system.
   * @param configuration Reference to Configuration object so that this invocator has access to
   *     configuration properties and the access token.
   * @param filter Object possibly containing an attribute value to filter upon.
   * @param paginator Object possibly containing current pagination information for results being
   *     processed. Fields in this pagination could/should be updated by the invocator as results
   *     are processed for the connector.
   * @param resultCap The maximum number of results that should be returned by getAll. This can be
   *     null but if present will override the pagination pageSize.
   * @param prefetchDataMap Map of prefetch data applicable to the Identity Model and that may need
   *     to be carried over between multiple requests in special use cases.
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
   * Get a single object of this invocator's particular object class type on the destination system
   * that matches the given identifier (objectId).
   *
   * @param driver Driver belonging to this connector and providing interaction with the applicable
   *     destination system.
   * @param configuration Reference to Configuration object so that this invocator has access to
   *     configuration properties and the access token.
   * @param objectId String holding the identifier for the object being sought on the destination
   *     system.
   * @param prefetchDataMap Map of prefetch data applicable to the Identity Model and that may need
   *     * to be carried over between multiple requests in special use cases.
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
   * @param driver Driver belonging to this connector and providing interaction with the applicable
   *     destination system.
   * @param configuration Reference to Configuration object so that this invocator has access to
   *     configuration properties and the access token.
   * @param objectName String holding the identifier for the object being sought on the destination
   *     system.
   * @param prefetchDataMap Map of prefetch data applicable to the Identity Model and that may need
   *     to be carried over between multiple requests in special use cases.
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
