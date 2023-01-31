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

import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenPasswordAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.*;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2PasswordConfiguration;
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

/** Integration test for both the OAuth2TokenPasswordAuthenticator, using Dev ELabs GotoMeeting */
@ExtendWith(MockitoExtension.class)
public class OAuth2TokenPasswordAuthenticatorTest extends IntegrationTest {

  private static HttpClient testClient;

  @Override
  public String getConfigurationName() {
    return new ConfigurationNameBuilder().withConnector(() -> "password").build();
  }

  @Test
  public void test() throws IOException {
    String jsonResponse = "{\"access_token\":\"abc123\", \"token_type\":\"mine\"}";
    HttpResponse testResponse = mock(HttpResponse.class);
    StatusLine testStatusLine = mock(StatusLine.class);
    HttpEntity testEntity = mock(HttpEntity.class);
    testClient = mock(HttpClient.class);
    when(testClient.execute(any())).thenReturn(testResponse);
    when(testResponse.getStatusLine()).thenReturn(testStatusLine);
    when(testStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    when(testResponse.getEntity()).thenReturn(testEntity);
    when(testEntity.getContent()).thenReturn(new ByteArrayInputStream(jsonResponse.getBytes()));

    TestConfiguration configuration = new TestConfiguration(getConfigurationName());
    ConfigurationReader.setupTestConfiguration(configuration);
    setup(configuration);
    String response = new TestOAuth2TokenPasswordAuthenticator().authenticate(configuration);
    assertNotNull(response);
    assertNotNull(configuration.getOauth2Information());
    assertNotNull(configuration.getOauth2Information().get("accessToken"));
    assertNotNull(configuration.getOauth2Information().get("tokenType"));
  }

  protected static class TestConfiguration extends DefaultConnectorConfiguration
      implements Oauth2PasswordConfiguration {

    @ConfigurationInfo(path = "security.authenticator.oauth2Password.tokenUrl")
    private String tokenUrl;

    @ConfigurationInfo(path = "security.authenticator.oauth2Password.encodedSecret")
    private String encodedSecret;

    @ConfigurationInfo(path = "security.authenticator.oauth2Password.oauth2Username")
    private String username;

    @ConfigurationInfo(path = "security.authenticator.oauth2Password.oauth2Password")
    private String password;

    @ConfigurationInfo(path = "security.authenticator.oauth2Password.oauth2Information")
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
    public String getEncodedSecret() {
      return encodedSecret;
    }

    @Override
    public void setEncodedSecret(String input) {
      encodedSecret = input;
    }

    @Override
    public String getOauth2Username() {
      return username;
    }

    @Override
    public void setOauth2Username(String input) {
      username = input;
    }

    @Override
    public String getOauth2Password() {
      return password;
    }

    @Override
    public void setOauth2Password(String input) {
      password = input;
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

  protected static class TestOAuth2TokenPasswordAuthenticator
      extends OAuth2TokenPasswordAuthenticator {

    @Override
    public HttpClient createClient() {
      return testClient;
    }
  }
}
