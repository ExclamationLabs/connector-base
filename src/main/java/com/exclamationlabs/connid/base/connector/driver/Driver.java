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

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.Map;
import java.util.Set;

/**
 * Classes that implement Driver should be able to connect to some kind of destination system
 * and be able to perform user and group CRUD operations, and may also have the ability
 * to assign a user to a group or remove him from a group.
 *
 * Some drivers may not be able to support all methods (for example, some systems may
 * not allow you to delete a user, or assign him to a group).  In those cases, implementations
 * should throw UnsupportedOperationException to indicate it's not supported.
 *
 * A driver must consist of one to many DriverInvocator objects.  Invocator objects
 * instruct the driver how each IdentityModel type should interact with the destination system.
 * These DriverInvocator objects should be registered to the driver using the addInvocator() method.
 * In most cases, your constructor should make calls to addInvocator() to notify the Driver
 * of the invocators it should register.
 */
@SuppressWarnings("rawtypes")
public interface Driver<T extends ConnectorConfiguration> {

    /**
     * Receives the configuration and authenticator objects that may be needed by the driver.
     * In this method, any additional initialization that needs to be done to
     * prep the Driver for repeated usage should also be done.
     * @param configuration Reference to Configuration object so that
     *                      this driver has access to configuration properties
     *                      and the access token.
     * @param authenticator Reference to the Authenticator object
     *                      in case this Driver needs to make a call to reauthenticate
     *                      (often because of a timeout or token expiration condition).
     * @throws ConnectorException If a problem occurred that prevented Driver
     * from completing it's initialization.
     */
    void initialize(T configuration, Authenticator<T> authenticator)
            throws ConnectorException;

    /**
     * Performs a quick ping or health check to verify the Driver is still working
     * or valid. Some Drivers may opt to a simpler sanity check (like making sure the
     * configuration, authenticator or other fields are not null) or have this as a no-op,
     * which is acceptable.
     *
     * NOTE: Midpoint systems tend to invoke this often, so be cautious this is a relatively
     * inexpensive operation.
     * @throws ConnectorException if connector test failed in some way
     */
    void test() throws ConnectorException;

    /**
     * Close any resources associated with this driver so that the object is unusable going
     * forward.
     */
    void close();

    /**
     * Return the DriverInvocator corresponding to a particular IdentityModel class.
     * @param identityModelClass Class reference for a particular IdentityModel implementation.
     * @return DriverInvocator pertaining to the input IdentityModel class.
     */
    DriverInvocator getInvocator(Class<? extends IdentityModel> identityModelClass);

    /**
     * Register an Invocator definition with this Driver.
     * @param identityModelClass Class reference for the IdentityModel implementation
     *                           relating to the Invocator.
     * @param invocator An instance of the applicable Invocator object.
     */
    void addInvocator(Class<? extends IdentityModel> identityModelClass, DriverInvocator invocator);

    /**
     * Process a request to create a new object on the destination system.
     * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
     *                                the creation request.
     * @param model IdentityModel instance holding all object data for the creation request.
     * @return The new id for the object just created
     * @throws ConnectorException If create operation failed or was invalid.
     */
    String create(Class<? extends IdentityModel> identityModelClass,
                  IdentityModel model) throws ConnectorException;

    /**
     * Process a request to update an object on the destination system.
     * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
     *                          the update request.
     * @param objectId String containing the id pertaining to the item being updated
     * @param userModel IdentityModel instance holding all object data for the update request.
     *                  Any fields that are null or not set should remain unchanged for this record
     *                  on the destination system.
     * @throws ConnectorException If update operation failed or was invalid.
     */
    void update(Class<? extends IdentityModel> identityModelClass, String objectId, IdentityModel userModel)
            throws ConnectorException;

    /**
     * Process a request to delete an object on the destination system.
     * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
     *                              the delete request.
     * @param objectId String containing the id pertaining to the item to be deleted
     * @throws ConnectorException If delete operation failed or was invalid
     */
    void delete(Class<? extends IdentityModel> identityModelClass, String objectId) throws ConnectorException;


    /**
     * Process a request to get all objects of a particular type from the destination system.
     * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
     *                                      the get request.
     * @param resultsFilter Object possibly containing an attribute value to filter upon.
     * @param pagination Object possibly containing current pagination information for results
     *                   being processed.  Fields in this pagination could/should be
     *                   updated by the driver or invocator as results are processed for the connector.
*    * @param resultCap The maximum number of results that should be returned by getAll.  This
     *                        can be null but if present will override the pagination pageSize.
     *
     * @return A set of IdentityModel instances representing all the objects of a particular type.  Or
     * null or an empty set if no objects for this type were found.
     * @throws ConnectorException If get operation failed or was invalid.  Note: A request returning
     * no records found (an empty or null list) is not considered an exception condition.
     */
    Set<IdentityModel> getAll(Class<? extends IdentityModel> identityModelClass, ResultsFilter resultsFilter,
                              ResultsPaginator pagination, Integer resultCap) throws ConnectorException;

    /**
     * Process a request to get a single object of a particular type from the destination system,
     * matching the requested id.
     * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
    *                                     the get request.
     * @param idValue String containing the id for the record to be retrieved.
     * @param operationOptionsData data map possibly containing current paging information
     * @return An IdentityModel instance representing the object for the given id.  Or null
     * if a record was not found.
     * @throws ConnectorException If get operation failed or was invalid.  Note: A request returning
     * no matching record for the given id is not considered an exception condition.
     */
    IdentityModel getOne(Class<? extends IdentityModel> identityModelClass, String idValue,
                         Map<String, Object> operationOptionsData) throws ConnectorException;

    /**
     * Process a request to get a single object of a particular type from the destination system,
     * matching the requested name.
     *
     * Default behavior is to throw UnsupportedOperationException.  Underlying driver/invocator must
     * both provide their own support for this method if it supported and needed.
     *
     * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
     *                                     the get request.
     * @param nameValue String containing the id for the record to be retrieved.
     * @return An IdentityModel instance representing the object for the given id.  Or null
     * if a record was not found.
     * @throws ConnectorException If get operation failed or was invalid.  Note: A request returning
     * no matching record for the given name is not considered an exception condition.
     */
    default IdentityModel getOneByName(Class<? extends IdentityModel> identityModelClass, String nameValue)
            throws ConnectorException {
        throw new UnsupportedOperationException("Driver does not support getOneByName");
    }

}
