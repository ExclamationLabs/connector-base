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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenAuthorizationCodeAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationInfo;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationNameBuilder;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationReader;
import com.exclamationlabs.connid.base.connector.configuration.DefaultConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2AuthorizationCodeConfiguration;
import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OAuth2TokenAuthorizationCodeAuthenticatorTest extends IntegrationTest {

  private static HttpClient testClient;

  @Override
  public String getConfigurationName() {
    return new ConfigurationNameBuilder().withConnector(() -> "authcode").build();
  }

  @Test
  public void test() throws IOException {
    String jsonResponse =
        "{\"access_token\":\"abc123\", \"refresh_token\":\"xyz567\", \"expires_in\":778899}";
    HttpResponse testResponse = mock(HttpResponse.class);
    StatusLine testStatusLine = mock(StatusLine.class);
    HttpEntity testEntity = mock(HttpEntity.class);
    testClient = mock(HttpClient.class);
    when(testClient.execute(any())).thenReturn(testResponse);
    when(testResponse.getStatusLine()).thenReturn(testStatusLine);
    when(testStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    when(testResponse.getEntity()).thenReturn(testEntity);
    when(testEntity.getContent()).thenReturn(new ByteArrayInputStream(jsonResponse.getBytes()));

    Oauth2AuthorizationCodeConfiguration configuration =
        new TestConfiguration(getConfigurationName());
    ConfigurationReader.setupTestConfiguration(configuration);
    setup(configuration);
    String response =
        new TestOAuth2TokenAuthorizationCodeAuthenticator().authenticate(configuration);
    assertNotNull(response);
    assertNotNull(configuration.getOauth2Information());
    assertNotNull(configuration.getOauth2Information().get("accessToken"));
    assertNotNull(configuration.getOauth2Information().get("refreshToken"));
    assertNotNull(configuration.getOauth2Information().get("expiresIn"));
  }

  protected static class TestConfiguration extends DefaultConnectorConfiguration
      implements Oauth2AuthorizationCodeConfiguration {

    @ConfigurationInfo(path = "security.authenticator.oauth2AuthorizationCode.tokenUrl")
    private String tokenUrl;

    @ConfigurationInfo(path = "security.authenticator.oauth2AuthorizationCode.authorizationCode")
    private String authorizationCode;

    @ConfigurationInfo(path = "security.authenticator.oauth2AuthorizationCode.clientId")
    private String clientId;

    @ConfigurationInfo(path = "security.authenticator.oauth2AuthorizationCode.clientSecret")
    private String clientSecret;

    @ConfigurationInfo(path = "security.authenticator.oauth2AuthorizationCode.redirectUri")
    private String redirectUri;

    @ConfigurationInfo(path = "security.authenticator.oauth2AuthorizationCode.oauth2Information")
    private Map<String, String> oauth2Information;

    public TestConfiguration(String nameIn) {
      name = nameIn;
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
    public String getAuthorizationCode() {
      return authorizationCode;
    }

    @Override
    public void setAuthorizationCode(String input) {
      authorizationCode = input;
    }

    @Override
    public String getClientId() {
      return clientId;
    }

    @Override
    public void setClientId(String input) {
      clientId = input;
    }

    @Override
    public String getClientSecret() {
      return clientSecret;
    }

    @Override
    public void setClientSecret(String input) {
      clientSecret = input;
    }

    @Override
    public String getRedirectUri() {
      return redirectUri;
    }

    @Override
    public void setRedirectUri(String input) {
      redirectUri = input;
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

  protected static class TestOAuth2TokenAuthorizationCodeAuthenticator
      extends OAuth2TokenAuthorizationCodeAuthenticator {

    @Override
    protected HttpClient createClient() {
      return testClient;
    }
  }
}
