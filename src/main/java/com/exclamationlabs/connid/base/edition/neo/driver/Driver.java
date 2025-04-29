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

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import java.util.Map;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

/**
 * Classes that implement Driver should be able to connect to some kind of destination system and be
 * able to perform user and group CRUD operations, and may also have the ability to assign a user to
 * a group or remove him from a group.
 *
 * <p>Some drivers may not be able to support all methods (for example, some systems may not allow
 * you to delete a user, or assign him to a group). In those cases, implementations should throw
 * UnsupportedOperationException to indicate it's not supported.
 *
 * <p>If an invocator is present that uses the applicable object class, its methods will be used for
 * all CRUD operations (getAll, getOne, create, update, delete). If no invocator is present, the
 * driver will be used directly for all CRUD operations.
 *
 * <p>Driver is however exclusively responsible for receiving the authenticator and performing
 * initialization, as well as supporting the test() method to check the connection to the
 * destination system.
 *
 * <p>All driver methods take the connector's ConnectorConfiguration as a parameter so that
 * configuration values are readily available for implementations.
 *
 * <p>IMPORTANT NOTES: - There should be only one driver implementation per connector. - For each
 * object class supported by the connector, either the driver or an invocator implementation must be
 * able to support the getAll() and getOne() methods. If an invocator is found for the applicable
 * object class, it takes precedences will be used for getOne() and getAll(). - If your connector
 * required create, update or delete for any object classes, implement the FullAccessDriver
 * interface instead. Implementing only Driver infers that your connector is limited to read-only
 * (getAll/getOne) operations.
 */
public interface Driver<T extends ConnectorConfiguration> {

  /**
   * Receives the configuration and authenticator objects that may be needed by the driver. In this
   * method, any additional initialization that needs to be done to prep the Driver for repeated
   * usage should also be done.
   *
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param authenticator Reference to the Authenticator object in case this Driver needs to make a
   *     call to reauthenticate (often because of a timeout or token expiration condition).
   * @throws ConnectorException If a problem occurred that prevented Driver from completing it's
   *     initialization.
   */
  void initialize(T configuration, Authenticator<T> authenticator) throws ConnectorException;

  /**
   * Performs a quick ping or health check to verify the Driver is still working or valid. Some
   * Drivers may opt to a simpler sanity check (like making sure the configuration, authenticator or
   * other fields are not null) or have this as a no-op, which is acceptable.
   *
   * <p>NOTE: Midpoint systems tend to invoke this often, so be cautious this is a relatively
   * inexpensive operation.
   *
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @throws ConnectorException if connector test failed in some way
   */
  void test(T configuration) throws ConnectorException;

  /**
   * Performs a full connection test to the source system. This is a more comprehensive test than
   * the test() method and may include more expensive operations. This method is intended to be used
   * to verify the driver is fully functional and can connect to the source system utilizing the
   * user connection information provided in the configuration.
   *
   * <p>In order to provide an accurate and hollistic connection test, the driver may need to make
   * additional calls to the source system to ensure appropriate access and permissions to the
   * various required resource endpoints These calls may include:
   *
   * <p>- Retrieving a list of users - Retrieving a list of groups - Retrieving a list of roles -
   * Retrieving a list of permissions
   *
   * <p>NOTE: This method is not utilized by MidPoint, instead is provided to Provision systems to
   * perform any necessary connection testing that may go beyond the basic test() method (since the
   * test method is intentionally kept inexpensive due to frequency of invocation).
   *
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @throws ConnectorException if connector test failed in some way
   */
  default void fullConnectionTest(T configuration) throws ConnectorException {
    throw new UnsupportedOperationException("Driver does not support fullConnectionTest");
  }
  ;

  /**
   * Close any resources associated with this driver so that the object is unusable going forward.
   */
  void close();

  /**
   * Process a request to get all objects of a particular type from the destination system. If
   * pagination and/or filters are detected, a subset of all items may be returned.
   *
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
   *     the get request.
   * @param resultsFilter Object possibly containing an attribute value to filter upon.
   * @param pagination Object possibly containing current pagination information for results being
   *     processed. Fields in this pagination could/should be updated by the driver or invocator as
   *     results are processed for the connector.
   * @param resultCap The maximum number of results that should be returned by getAll. This can be
   *     null but if present will override the pagination pageSize.
   * @param prefetchDataMap Map of prefetch data applicable to the Identity Model and that may need
   *     to be carried over between multiple requests in special use cases.
   * @return A set of IdentityModel instances representing all the objects of a particular type. Or
   *     null or an empty set if no objects for this type were found.
   * @throws ConnectorException If get operation failed or was invalid. Note: A request returning no
   *     records found (an empty or null list) is not considered an exception condition.
   */
  default Set<IdentityModel> getAll(
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      ResultsFilter resultsFilter,
      ResultsPaginator pagination,
      Integer resultCap,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    throw new UnsupportedOperationException("Driver does not support getAll");
  }

  /**
   * Process a request to get a single object of a particular type from the destination system,
   * matching the requested id.
   *
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
   *     the get request.
   * @param idValue String containing the id for the record to be retrieved.
   * @param prefetchDataMap Map of prefetch data applicable to the Identity Model and that may need
   *     * to be carried over between multiple requests in special use cases.
   * @return An IdentityModel instance representing the object for the given id. Or null if a record
   *     was not found.
   * @throws ConnectorException If get operation failed or was invalid. Note: A request returning no
   *     matching record for the given id is not considered an exception condition.
   */
  default IdentityModel getOne(
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      String idValue,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    throw new UnsupportedOperationException("Driver does not support getOne");
  }

  /**
   * Gives the ability for an driver to provide custom prefetched data prior to the execution of
   * repetitive getAll/getOne/getOneByName call. This should be used in cases where you have a set
   * of data that remains unchanging and needs to be reused for an indefinite number of requests.
   * The prefetch will only be performed once and carried forward.
   *
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param identityModelClass Class of IdentityModel applicable to the potential prefetch behavior.
   * @return Map of prefetched data that is understood and used by the driver.
   */
  default Map<String, Object> getPrefetch(
      T configuration, Class<? extends IdentityModel> identityModelClass) {
    return Map.of();
  }

  /**
   * Process a request to get a single object of a particular type from the destination system,
   * matching the requested name.
   *
   * <p>Default behavior is to throw UnsupportedOperationException. Underlying driver/invocator must
   * both provide their own support for this method if it supported and needed.
   *
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
   *     the get request.
   * @param nameValue String containing the id for the record to be retrieved.
   * @param prefetchDataMap Map of prefetch data applicable to the Identity Model and that may need
   *     * to be carried over between multiple requests in special use cases.
   * @return An IdentityModel instance representing the object for the given id. Or null if a record
   *     was not found.
   * @throws ConnectorException If get operation failed or was invalid. Note: A request returning no
   *     matching record for the given name is not considered an exception condition.
   */
  default IdentityModel getOneByName(
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      String nameValue,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    throw new UnsupportedOperationException("Driver does not support getOneByName");
  }
}
