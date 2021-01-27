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
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Classes that implement Driver should be able to connect to some kind of data system
 * and be able to perform user and group CRUD operations, and may also have the ability
 * to assign a user to a group or remove him from a group.
 *
 * Some drivers may not be able to support all methods (for example, some systems may
 * not allow you to delete a user, or assign him to a group).  In those cases, implementations
 * should throw UnsupportedOperationException to indicate it's not supported.
 */

@SuppressWarnings("rawtypes")
public interface Driver {

    /**
     * Returns the names of the properties for properties
     * that must be present in order for this Driver to function.
     * @return Set containing property names, represented as a set of enum values.
     * Returning null or an empty set is also allowed if there are no properties for
     * this driver.
     */
    Set<ConnectorProperty> getRequiredPropertyNames();

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
    void initialize(BaseConnectorConfiguration configuration, Authenticator authenticator)
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

    DriverInvocator getInvocator(Class<? extends IdentityModel> identityModelClass);

    void addInvocator(Class<? extends IdentityModel> identityModelClass, DriverInvocator invocator);

    String create(Class<? extends IdentityModel> identityModelClass,
                  IdentityModel model, Map<String,List<String>> assignmentIdentifiers) throws ConnectorException;

    void update(Class<? extends IdentityModel> identityModelClass, String userId, IdentityModel userModel,
                Map<String,List<String>> assignmentIdentifiers) throws ConnectorException;

    void delete(Class<? extends IdentityModel> identityModelClass, String userId) throws ConnectorException;

    List<IdentityModel> getAll(Class<? extends IdentityModel> identityModelClass) throws ConnectorException;

    IdentityModel getOne(Class<? extends IdentityModel> identityModelClass, String userId) throws ConnectorException;

}
