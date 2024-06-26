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

package com.exclamationlabs.connid.base.connector.authenticator.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.exclamationlabs.connid.base.connector.authenticator.JWTRS256Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.*;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.JksConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.JwtRs256Configuration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2JwtConfiguration;
import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OAuth2TokenJWTRS256JKSAuthenticatorTest extends IntegrationTest {

  @Override
  public String getConfigurationName() {
    return new ConfigurationNameBuilder().withConnector(() -> "jwtrsjks").build();
  }

  @Test
  public void test() {
    RSAPrivateKey testPrivateKey = mock(RSAPrivateKey.class);

    TestConfiguration configuration = new TestConfiguration(getConfigurationName());
    ConfigurationReader.setupTestConfiguration(configuration);
    setup(configuration);
    JWTRS256Authenticator jwtAuthenticator =
        new JWTRS256Authenticator() {
          @Override
          protected RSAPrivateKey getPrivateKey() {
            return testPrivateKey;
          }

          @Override
          protected String sign(JWTCreator.Builder builder, Algorithm algorithm) {
            return "hello";
          }

          @Override
          public String authenticate(JwtRs256Configuration configuration)
              throws ConnectorSecurityException {
            ((TestConfiguration) configuration).setOauth2Information(new HashMap<>());
            ((TestConfiguration) configuration).getOauth2Information().put("accessToken", "test1");
            ((TestConfiguration) configuration).getOauth2Information().put("id", "test2");
            ((TestConfiguration) configuration).getOauth2Information().put("instanceUrl", "test3");
            ((TestConfiguration) configuration).getOauth2Information().put("scope", "test4");
            ((TestConfiguration) configuration).getOauth2Information().put("tokenType", "test5");
            return super.authenticate(configuration);
          }
        };

    String response = jwtAuthenticator.authenticate(configuration);
    assertNotNull(response);
    assertNotNull(configuration.getOauth2Information());
    assertNotNull(configuration.getOauth2Information().get("accessToken"));
    assertNotNull(configuration.getOauth2Information().get("id"));
    assertNotNull(configuration.getOauth2Information().get("instanceUrl"));
    assertNotNull(configuration.getOauth2Information().get("scope"));
    assertNotNull(configuration.getOauth2Information().get("tokenType"));
  }

  protected static class TestConfiguration extends DefaultConnectorConfiguration
      implements JwtRs256Configuration, JksConfiguration, Oauth2JwtConfiguration {

    @ConfigurationInfo(path = "security.jks.jksFile")
    private String file;

    @ConfigurationInfo(path = "security.jks.jksPassword")
    private GuardedString password;

    @ConfigurationInfo(path = "security.jks.jksAlias")
    private String alias;

    @ConfigurationInfo(path = "security.authenticator.jwtRs256.issuer")
    private GuardedString issuer;

    @ConfigurationInfo(path = "security.authenticator.jwtRs256.subject")
    private GuardedString subject;

    @ConfigurationInfo(path = "security.authenticator.jwtRs256.expirationPeriod")
    private Long expirationPeriod;

    @ConfigurationInfo(path = "security.authenticator.jwtRs256.audience")
    private String audience;

    @ConfigurationInfo(path = "security.authenticator.jwtRs256.useIssuedAt")
    private Boolean useIssuedAt;

    @ConfigurationInfo(path = "security.authenticator.jwtRs256.extraClaimData")
    private Map<String, String> extraClaimData;

    @ConfigurationInfo(path = "security.authenticator.oauth2Jwt.tokenUrl")
    private String tokenUrl;

    @ConfigurationInfo(path = "security.authenticator.oauth2Jwt.oauth2Information")
    private Map<String, String> oauth2Information;

    public TestConfiguration(String nameIn) {
      name = nameIn;
    }

    @Override
    public String getJksFile() {
      return file;
    }

    @Override
    public void setJksFile(String input) {
      file = input;
    }

    @Override
    public GuardedString getJksPassword() {
      return password;
    }

    @Override
    public void setJksPassword(GuardedString input) {
      password = input;
    }

    @Override
    public String getJksAlias() {
      return alias;
    }

    @Override
    public void setJksAlias(String input) {
      alias = input;
    }

    @Override
    public GuardedString getIssuer() {
      return issuer;
    }

    @Override
    public void setIssuer(GuardedString input) {
      issuer = input;
    }

    @Override
    public GuardedString getSubject() {
      return subject;
    }

    @Override
    public void setSubject(GuardedString input) {
      subject = input;
    }

    @Override
    public Long getExpirationPeriod() {
      return expirationPeriod;
    }

    @Override
    public void setExpirationPeriod(Long input) {
      expirationPeriod = input;
    }

    @Override
    public String getAudience() {
      return audience;
    }

    @Override
    public void setAudience(String input) {
      audience = input;
    }

    @Override
    public Boolean getUseIssuedAt() {
      return useIssuedAt;
    }

    @Override
    public void setUseIssuedAt(Boolean input) {
      useIssuedAt = input;
    }

    @Override
    public String getTokenUrl() {
      return tokenUrl;
    }

    @Override
    public void setTokenUrl(String input) {
      tokenUrl = input;
    }

    @Override
    public Map<String, String> getOauth2Information() {
      return oauth2Information;
    }

    @Override
    public void setOauth2Information(Map<String, String> info) {
      oauth2Information = info;
    }
  }
}
