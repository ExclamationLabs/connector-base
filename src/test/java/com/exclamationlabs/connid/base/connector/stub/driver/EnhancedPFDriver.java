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

package com.exclamationlabs.connid.base.connector.stub.driver;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.driver.BaseDriver;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.stub.configuration.EnhancedPFConfiguration;
import com.exclamationlabs.connid.base.connector.stub.model.EnhancedPFUser;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class EnhancedPFDriver extends BaseDriver<EnhancedPFConfiguration> {

  private boolean containsOnly = false;
  private boolean equalsOnly = false;

  private boolean canPaginate = false;

  public EnhancedPFDriver() {
    addInvocator(EnhancedPFUser.class, new EnhancedPFUserInvocator());
  }

  public EnhancedPFDriver(boolean containsOnly, boolean equalsOnly, boolean canPaginate) {
    this();
    setContainsOnly(containsOnly);
    setEqualsOnly(equalsOnly);
  }

  @Override
  public void initialize(
      EnhancedPFConfiguration configuration, Authenticator<EnhancedPFConfiguration> authenticator)
      throws ConnectorException {}

  @Override
  public void test() throws ConnectorException {}

  @Override
  public void close() {}

  @Override
  public IdentityModel getOneByName(
      Class<? extends IdentityModel> identityModelClass, String nameValue)
      throws ConnectorException {
    return getInvocator(identityModelClass).getOneByName(this, nameValue);
  }

  public boolean isContainsOnly() {
    return containsOnly;
  }

  public void setContainsOnly(boolean containsOnly) {
    this.containsOnly = containsOnly;
  }

  public boolean isEqualsOnly() {
    return equalsOnly;
  }

  public void setEqualsOnly(boolean equalsOnly) {
    this.equalsOnly = equalsOnly;
  }

  public boolean isCanPaginate() {
    return canPaginate;
  }

  public void setCanPaginate(boolean canPaginate) {
    this.canPaginate = canPaginate;
  }
}
