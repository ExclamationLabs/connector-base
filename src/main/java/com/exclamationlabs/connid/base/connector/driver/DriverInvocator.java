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

package com.exclamationlabs.connid.base.connector.driver;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
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
public interface DriverInvocator<D extends Driver<?>, T extends IdentityModel> {

  /**
   * Create a new object on the destination system.
   *
   * @param driver Driver belonging to this Invocator and providing interaction with the applicable
   *     destination system.
   * @param model Model holding the data for the object to be created.
   * @return String containing new id for the object just created.
   * @throws ConnectorException If creation failed, was invalid or was not permitted.
   */
  String create(D driver, T model) throws ConnectorException;

  /**
   * Update an existing object on the destination system.
   *
   * @param driver Driver belonging to this Invocator and providing interaction with the applicable
   *     destination system.
   * @param userId String holding the id to match the item being updated on the destination system.
   * @param userModel Model holding the data for the object to be updated. Null fields present are
   *     to be treated as elements that should remain unchanged on the destination system.
   * @throws ConnectorException If update failed, was invalid or was not permitted.
   */
  void update(D driver, String userId, T userModel) throws ConnectorException;

  /**
   * Delete an existing object on the destination system.
   *
   * @param driver Driver belonging to this Invocator and providing interaction with the applicable
   *     destination system.
   * @param userId String holding the id to match the item being removed on the destination system.
   * @throws ConnectorException If deletion failed, was invalid or was not permitted.
   */
  void delete(D driver, String userId) throws ConnectorException;

  /**
   * Get all existing objects of this invocator's particular type on the destination system, using
   * supplied filter attribute and value. Unless overriden, default behavior is to presume filtering
   * is not supported, and simply execute the getAll method.
   *
   * @param driver Driver belonging to this Invocator and providing interaction with the applicable
   *     destination system.
   * @param filter Object possibly containing an attribute value to filter upon.
   * @param paginator Object possibly containing current pagination information for results being
   *     processed. Fields in this pagination could/should be updated by the invocator as results
   *     are processed for the connector.
   * @param resultCap The maximum number of results that should be returned by getAll. This can be
   *     null but if present will override the pagination pageSize.
   * @return A list of all IdentityModel objects of this Invocator's particular type. Can be null or
   *     an empty list if the destination system currently has no records.
   * @throws ConnectorException If get request failed, was invalid or was not permitted.
   */
  Set<T> getAll(D driver, ResultsFilter filter, ResultsPaginator paginator, Integer resultCap)
      throws ConnectorException;

  /**
   * Get a single object of this invocator's particular type on the destination system that matches
   * the given identifier.
   *
   * @param driver Driver belonging to this Invocator and providing interaction with the applicable
   *     destination system.
   * @param objectId String holding the identifier for the object being sought on the destination
   *     system.
   * @param operationOptionsData data map possibly containing current paging information
   * @return An IdentityModel object of this Invocator's particular type that corresponds to the
   *     given identifier. Can return null if no record matching the id was found on the destination
   *     system.
   * @throws ConnectorException If get request failed, was invalid or was not permitted.
   */
  T getOne(D driver, String objectId, Map<String, Object> operationOptionsData)
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
   * @param objectName String holding the identifier for the object being sought on the destination
   *     system.
   * @return An IdentityModel object of this Invocator's particular type that corresponds to the
   *     given name value. Can return null if no record matching the name was found on the
   *     destination system.
   * @throws ConnectorException If get request failed, was invalid or was not permitted.
   */
  default T getOneByName(D driver, String objectName) throws ConnectorException {
    throw new UnsupportedOperationException("DriverInvocator does not support getOneByName");
  }
}
