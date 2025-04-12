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
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
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
 * <p>A driver must consist of one to many DriverInvocator objects. Invocator objects instruct the
 * driver how each IdentityModel type should interact with the destination system. These
 * DriverInvocator objects should be registered to the driver using the addInvocator() method. In
 * most cases, your constructor should make calls to addInvocator() to notify the Driver of the
 * invocators it should register.
 */
@SuppressWarnings("rawtypes")
public interface FullAccessDriver<T extends ConnectorConfiguration> extends Driver<T> {

  /**
   * Process a request to create a new object on the destination system.
   *
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
   *     the creation request.
   * @param model IdentityModel instance holding all object data for the creation request.
   * @return The new id for the object just created
   * @throws ConnectorException If create operation failed or was invalid.
   */
  String create(
          T configuration, Class<? extends IdentityModel> identityModelClass, IdentityModel model)
      throws ConnectorException;

  /**
   * Process a request to update an object on the destination system.
   *
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
   *     the update request.
   * @param objectId String containing the id pertaining to the item being updated
   * @param userModel IdentityModel instance holding all object data for the update request. Any
   *     fields that are null or not set should remain unchanged for this record on the destination
   *     system.
   * @throws ConnectorException If update operation failed or was invalid.
   */
  void update(
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      String objectId,
      IdentityModel userModel)
      throws ConnectorException;

  /**
   * Process a request to delete an object on the destination system.
   *
   * @param configuration Reference to Configuration object so that this driver has access to
   *     configuration properties and the access token.
   * @param identityModelClass Class reference pertaining to the IdentityModel object applicable for
   *     the delete request.
   * @param objectId String containing the id pertaining to the item to be deleted
   * @throws ConnectorException If delete operation failed or was invalid
   */
  void delete(T configuration, Class<? extends IdentityModel> identityModelClass, String objectId)
      throws ConnectorException;
}
