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

package com.exclamationlabs.connid.base.connector.configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.ConnectorMessages;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class TestingConfiguration implements ConnectorConfiguration {

  @ConfigurationInfo(path = "currentToken", internal = true)
  protected String currentToken;

  @ConfigurationInfo(path = "source", internal = true)
  protected String source;

  @ConfigurationInfo(path = "name", internal = true)
  protected String name;

  @ConfigurationInfo(path = "active", internal = true)
  protected Boolean active;

  protected ConnectorMessages connectorMessages;

  @NotBlank
  @ConfigurationInfo(path = "custom.thing1")
  private String thing1;

  @NotNull
  @ConfigurationInfo(path = "custom.thing2")
  private Long thing2;

  @NotNull
  @ConfigurationInfo(path = "custom.thing3")
  private Boolean thing3;

  @Min(0)
  @Max(100)
  @ConfigurationInfo(path = "rest.ioErrorRetries")
  private Integer restIoErrorRetries;

  @NotNull(message = "loginPassword cannot be null")
  @ConfigurationInfo(path = "custom.guardedValue")
  private GuardedString guardedValue;

  @ConfigurationInfo(path = "custom.arrayValue")
  private String[] arrayValue;

  public TestingConfiguration() {}

  public TestingConfiguration(String configurationName) {
    name = configurationName;
  }

  @ConfigurationProperty(displayMessageKey = "test1", helpMessageKey = "test1")
  public String getThing1() {
    return thing1;
  }

  public void setThing1(String input) {
    this.thing1 = input;
  }

  @ConfigurationProperty(displayMessageKey = "retry", helpMessageKey = "retry")
  public Integer getRestIoErrorRetries() {
    return restIoErrorRetries;
  }

  public void setRestIoErrorRetries(Integer restIoErrorRetries) {
    this.restIoErrorRetries = restIoErrorRetries;
  }

  @ConfigurationProperty(displayMessageKey = "test2", helpMessageKey = "test2")
  public Long getThing2() {
    return thing2;
  }

  public void setThing2(Long thing2) {
    this.thing2 = thing2;
  }

  @ConfigurationProperty(displayMessageKey = "test3", helpMessageKey = "test3")
  public Boolean getThing3() {
    return thing3;
  }

  public void setThing3(Boolean thing3) {
    this.thing3 = thing3;
  }

  @ConfigurationProperty(
      displayMessageKey = "guarded value",
      helpMessageKey = "guarded value help",
      required = true)
  public GuardedString getGuardedValue() {
    return guardedValue;
  }

  public void setGuardedValue(GuardedString guardedValue) {
    this.guardedValue = guardedValue;
  }

  @ConfigurationProperty(
      displayMessageKey = "array value",
      helpMessageKey = "array value help",
      required = false)
  public String[] getArrayValue() {
    return arrayValue;
  }

  public void setArrayValue(String[] arrayValue) {
    this.arrayValue = arrayValue;
  }

  @Override
  public String getCurrentToken() {
    return currentToken;
  }

  @Override
  public void setCurrentToken(String input) {
    currentToken = input;
  }

  @Override
  public String getSource() {
    return source;
  }

  @Override
  public void setSource(String input) {
    source = input;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String input) {
    name = input;
  }

  @Override
  public Boolean getActive() {
    return active;
  }

  @Override
  public void setActive(Boolean input) {
    active = input;
  }

  @Override
  public ConnectorMessages getConnectorMessages() {
    return null;
  }

  @Override
  public void setConnectorMessages(ConnectorMessages messages) {}
}
