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

package com.exclamationlabs.connid.base.connector.authenticator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exclamationlabs.connid.base.connector.configuration.ConfigurationInfo;
import com.exclamationlabs.connid.base.connector.configuration.DefaultConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.DirectAccessTokenConfiguration;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;
import org.junit.jupiter.api.Test;

public class DirectAccessTokenAuthenticatorTest {

  @Test
  public void test() {
    DirectAccessTokenConfiguration configuration = new TestConfiguration();
    Authenticator<DirectAccessTokenConfiguration> authenticator =
        new DirectAccessTokenAuthenticator() {
          @Override
          public String authenticate(DirectAccessTokenConfiguration configuration)
              throws ConnectorSecurityException {
            return "cooltoken";
          }
        };
    String response = authenticator.authenticate(configuration);
    assertNotNull(response);
    assertEquals("cooltoken", response);
  }

  static class TestConfiguration extends DefaultConnectorConfiguration
      implements DirectAccessTokenConfiguration {

    @ConfigurationInfo(path = "security.authenticator.directAccessToken.token")
    private String token;

    @Override
    public String getToken() {
      return token;
    }

    @Override
    public void setToken(String input) {
      token = input;
    }
  }
}
