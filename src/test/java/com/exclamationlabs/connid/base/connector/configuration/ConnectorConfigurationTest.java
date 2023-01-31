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

import com.exclamationlabs.connid.base.connector.stub.StubConnector;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import javax.validation.constraints.NotBlank;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.ConfigurationProperty;
import org.junit.jupiter.api.Test;

public class ConnectorConfigurationTest {

  @Test
  public void testConfigurationValidationFailed() {
    ConnectorConfiguration configuration = new ValidatingConfiguration();
    assertThrows(ConfigurationException.class, () -> new StubConnector().init(configuration));
  }

  @Test
  public void testConfigurationValidationPassed() {
    ValidatingConfiguration configuration = new ValidatingConfiguration();

    configuration.setUnsetItem("Test");
    new StubConnector().init(configuration);
  }

  static class ValidatingConfiguration extends StubConfiguration {

    public ValidatingConfiguration() {
      super();
      setActive(true);
    }

    @NotBlank private String unsetItem;

    @ConfigurationProperty(displayMessageKey = "test1", helpMessageKey = "test2", required = true)
    public String getUnsetItem() {
      return unsetItem;
    }

    public void setUnsetItem(String input) {
      this.unsetItem = input;
    }
  }
}
