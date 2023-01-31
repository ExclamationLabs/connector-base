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
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class StubDriver extends BaseDriver<StubConfiguration> {

  private String methodInvoked;
  private Object methodParameter1;
  private Object methodParameter2;
  private boolean initializeInvoked = false;

  public StubDriver() {
    addInvocator(StubUser.class, new StubUserInvocator());
    addInvocator(StubGroup.class, new StubGroupInvocator());
  }

  @Override
  public void initialize(
      StubConfiguration configuration, Authenticator<StubConfiguration> authenticator)
      throws ConnectorException {
    initializeInvoked = true;
  }

  @Override
  public void test() throws ConnectorException {
    setMethodInvoked("test");
  }

  @Override
  public void close() {}

  public String getMethodInvoked() {
    return methodInvoked;
  }

  void setMethodInvoked(String methodInvoked) {
    this.methodInvoked = methodInvoked;
  }

  public Object getMethodParameter1() {
    return methodParameter1;
  }

  void setMethodParameter1(Object methodParameter1) {
    this.methodParameter1 = methodParameter1;
  }

  public Object getMethodParameter2() {
    return methodParameter2;
  }

  void setMethodParameter2(Object methodParameter2) {
    this.methodParameter2 = methodParameter2;
  }

  public boolean isInitializeInvoked() {
    return initializeInvoked;
  }
}
