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

package com.exclamationlabs.connid.base.edition.neo.internal.search;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.edition.neo.driver.Driver;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessDriver;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessInvocator;
import com.exclamationlabs.connid.base.edition.neo.driver.Invocator;
import com.exclamationlabs.connid.base.edition.neo.internal.IdentityModelAccess;
import com.exclamationlabs.connid.base.edition.neo.internal.model.ModelWriter;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeDelta;
import org.identityconnectors.framework.common.objects.Uid;

public class FullAccessHandler<T extends ConnectorConfiguration> {

  @SuppressWarnings({"rawtypes", "unchecked"})
  public Uid create(
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      Set<Attribute> attributes,
      IdentityModelAccess identityModelAccess) {
    final var OPERATION = "Create";
    checkForFullAccess(identityModelClass, driver, invocator, OPERATION);
    var fullAccessDriver = (FullAccessDriver<T>) driver;
    IdentityModel identityModel =
        ModelWriter.execute(identityModelClass, attributes, OPERATION, identityModelAccess);
    if (invocator != null) {
      var fullAccessInvocator = (FullAccessInvocator) invocator;
      var idString = fullAccessInvocator.create(fullAccessDriver, configuration, identityModel);
      return new Uid(idString);
    } else {
      var idString = fullAccessDriver.create(configuration, identityModelClass, identityModel);
      return new Uid(idString);
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void update(
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      Uid uid,
      Set<AttributeDelta> attributeModifications,
      IdentityModelAccess identityModelAccess) {
    final var OPERATION = "Update";
    checkForFullAccess(identityModelClass, driver, invocator, OPERATION);
    IdentityModel identityModel =
        ModelWriter.executeUpdateDelta(
            identityModelClass, attributeModifications, uid.getUidValue(), identityModelAccess);
    var fullAccessDriver = (FullAccessDriver<T>) driver;
    if (invocator != null) {
      var fullAccessInvocator = (FullAccessInvocator) invocator;
      fullAccessInvocator.update(fullAccessDriver, configuration, uid.getUidValue(), identityModel);
    } else {
      fullAccessDriver.update(configuration, identityModelClass, uid.getUidValue(), identityModel);
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void delete(
      T configuration,
      Class<? extends IdentityModel> identityModelClass,
      Driver<T> driver,
      Invocator<T, Driver<T>, ?> invocator,
      Uid uid) {
    checkForFullAccess(identityModelClass, driver, invocator, "Delete");
    var fullAccessDriver = (FullAccessDriver<T>) driver;
    if (invocator != null) {
      var fullAccessInvocator = (FullAccessInvocator) invocator;
      fullAccessInvocator.delete(fullAccessDriver, configuration, uid.getUidValue());
    } else {
      fullAccessDriver.delete(configuration, identityModelClass, uid.getUidValue());
    }
  }

  private void checkForFullAccess(
      final Class<? extends IdentityModel> identityModelClass,
      final Driver<T> driver,
      final Invocator<T, Driver<T>, ?> invocator,
      final String operation) {
    if (invocator == null) {
      if (!(driver instanceof FullAccessDriver)) {
        throw new ConnectorException(
            String.format(
                "Driver %s does not support full access for %s to %s",
                driver.getClass().getSimpleName(), operation, identityModelClass.getSimpleName()));
      }
    } else {
      if (!(invocator instanceof FullAccessInvocator)) {
        throw new ConnectorException(
            String.format(
                "Invocator %s does not support full access for %s to %s",
                invocator.getClass().getSimpleName(),
                operation,
                identityModelClass.getSimpleName()));
      }
    }
  }
}
