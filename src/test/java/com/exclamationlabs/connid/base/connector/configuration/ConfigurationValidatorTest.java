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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationValidatorTest {

  private TestingConfiguration configuration;

  @BeforeEach
  public void setup() {
    configuration = new TestingConfiguration("testing");
    ConfigurationReader.prepareTestConfiguration(configuration);
    ConfigurationReader.readPropertiesFromSource(configuration);
  }

  @Test
  public void validateHappy() {
    ConfigurationValidator.validate(configuration);
  }

  @Test
  public void validateSadInvalidNumber() {
    configuration.setRestIoErrorRetries(4444);
    assertThrows(
        ConfigurationException.class, () -> ConfigurationValidator.validate(configuration));
  }

  @Test
  public void validateSadEmptyString() {
    configuration.setThing1("");
    assertThrows(
        ConfigurationException.class, () -> ConfigurationValidator.validate(configuration));
  }

  @Test
  public void validateSadNullString() {
    configuration.setThing1(null);
    assertThrows(
        ConfigurationException.class, () -> ConfigurationValidator.validate(configuration));
  }

  @Test
  public void validateSadNullNumber() {
    configuration.setThing2(null);
    assertThrows(
        ConfigurationException.class, () -> ConfigurationValidator.validate(configuration));
  }

  @Test
  public void validateSkipInactive() {
    configuration.setRestIoErrorRetries(4444);
    configuration.setActive(false);
    assertThrows(
        ConfigurationException.class, () -> ConfigurationValidator.validate(configuration));
  }
}
