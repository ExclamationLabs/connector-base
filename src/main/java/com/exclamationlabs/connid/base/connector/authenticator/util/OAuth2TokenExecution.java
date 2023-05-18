/*
    Copyright 2021 Exclamation Labs

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

package com.exclamationlabs.connid.base.connector.authenticator.util;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.model.OAuth2AccessTokenContainer;
import com.exclamationlabs.connid.base.connector.authenticator.model.StandardOAuth2AccessTokenContainer;
import com.exclamationlabs.connid.base.connector.configuration.TrustStoreConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2Configuration;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

public class OAuth2TokenExecution {

  private OAuth2TokenExecution() {}

  public static String executeRequest(
      Authenticator<?> authenticator,
      Oauth2Configuration configuration,
      HttpClient client,
      HttpPost request,
      StringEntity entity,
      GsonBuilder gsonBuilder)
      throws IOException {
    return executeRequest(
        authenticator,
        configuration,
        client,
        request,
        entity,
        gsonBuilder,
        StandardOAuth2AccessTokenContainer.class);
  }

  public static String executeRequest(
      Authenticator<?> authenticator,
      Oauth2Configuration configuration,
      HttpClient client,
      HttpPost request,
      StringEntity entity,
      GsonBuilder gsonBuilder,
      Class<? extends OAuth2AccessTokenContainer> tokenResponseClass)
      throws IOException {
    request.setEntity(entity);
    authenticator.getAdditionalAuthenticationHeaders(configuration).forEach(request::setHeader);
    HttpResponse response = client.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    Logger.debug(
        OAuth2TokenExecution.class,
        String.format(
            "OAuth2 Received %d response for %s %s",
            statusCode, request.getMethod(), request.getURI()));
    String rawJson = EntityUtils.toString(response.getEntity(), Charsets.UTF_8.name());
    Logger.debug(
        OAuth2TokenExecution.class, String.format("OAuth2 Received raw JSON: %s", rawJson));
    if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
      throw new ConnectorSecurityException(
          "OAuth2 assertion failed, status code is HTTP " + statusCode);
    }

    OAuth2AccessTokenContainer authResponse =
        gsonBuilder.create().fromJson(rawJson, tokenResponseClass);

    if (authResponse != null && authResponse.getAccessToken() != null) {
      configuration.setOauth2Information(authResponse.toMap());
      return authResponse.getAccessToken();
    } else {
      throw new ConnectorSecurityException(
          "Invalid/empty response received from OAuth2: " + rawJson);
    }
  }

  public static void initializeForHttp() {
    TrustStoreConfiguration.clearJdkProperties();
  }
}
