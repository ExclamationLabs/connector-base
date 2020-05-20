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
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.List;

/**
 * Classes that implement Driver should be able to connect to some kind of data system
 * and be able to perform user and group CRUD operations, and may also have the ability
 * to assign a user to a group or remove him from a group.
 *
 * Some drivers may not be able to support all methods (for example, some systems may
 * not allow you to delete a user, or assign him to a group).  In those cases, implementations
 * should throw UnsupportedOperationException to indicate it's not supported.
 */
public interface Driver<U,G> {

    /**
     * Receives the configuration and authenticator objects that may be needed by the driver.
     * In this method, any additional initialization that needs to be done to
     * prep the Driver for repeated usage should also be done.
     * @param configuration
     * @param authenticator
     * @throws ConnectorException If a problem occurred that prevented Driver
     * from completing it's initialization.
     */
    void initialize(ConnectorConfiguration configuration, Authenticator authenticator)
            throws ConnectorException;

    /**
     * Performs a quick ping or health check to verify the Driver is still working
     * or valid. Some Drivers may opt to a simpler sanity check (like making sure the
     * configuration, authenticator or other fields are not null) or have this as a no-op,
     * which is acceptable.
     *
     * NOTE: Midpoint systems tend to invoke this often, so be cautious this is a relatively
     * inexpensive operation.
     * @throws ConnectorException
     */
    void test() throws ConnectorException;

    /**
     * Close any resources associated with this driver so that the object is unusable going
     * forward.
     */
    void close();

    String createUser(U userModel) throws ConnectorException;

    String createGroup(G groupModel) throws ConnectorException;

    void updateUser(String userId, U userModel) throws ConnectorException;

    void updateGroup(String groupId, G groupModel) throws ConnectorException;

    void deleteUser(String userId) throws ConnectorException;

    void deleteGroup(String groupId) throws ConnectorException;

    List<U> getUsers() throws ConnectorException;

    List<G> getGroups() throws ConnectorException;

    U getUser(String userId) throws ConnectorException;

    G getGroup(String groupId) throws ConnectorException;

    void addGroupToUser(String groupId, String userId) throws ConnectorException;

    void removeGroupFromUser(String groupId, String userId) throws ConnectorException;
}
