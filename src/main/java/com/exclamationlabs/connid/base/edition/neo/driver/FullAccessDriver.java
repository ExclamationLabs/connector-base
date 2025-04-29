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
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

/**
 * Classes that implement FullAccessDriver should be able to connect to some kind of destination
 * system and be able to perform some or all data manipulation operations (create, update, and
 * delete) in additional to the getOne/getAll implementations required by the Driver interface for
 * at least one of the supported object classes.
 *
 * <p>Read the important notes included in the Driver interface for more information.
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
  default String create(
      T configuration, Class<? extends IdentityModel> identityModelClass, IdentityModel model)
      throws ConnectorException {
    throw new UnsupportedOperationException(
        "Create operation not supported by driver: " + this.getClass().getName());
  }

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
  default void update(
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      String objectId,
      IdentityModel userModel)
      throws ConnectorException {
    throw new UnsupportedOperationException(
        "Update operation not supported by driver: " + this.getClass().getName());
  }

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
  default void delete(
      T configuration, Class<? extends IdentityModel> identityModelClass, String objectId)
      throws ConnectorException {
    throw new UnsupportedOperationException(
        "Delete operation not supported by driver: " + this.getClass().getName());
  }
}
