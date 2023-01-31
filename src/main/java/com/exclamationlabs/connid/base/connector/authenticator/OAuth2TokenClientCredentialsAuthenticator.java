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

import com.exclamationlabs.connid.base.connector.authenticator.util.OAuth2TokenExecution;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2ClientCredentialsConfiguration;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

/** This implementation performs the OAuth2 "client_credentials" grant type. */
public class OAuth2TokenClientCredentialsAuthenticator
    implements Authenticator<Oauth2ClientCredentialsConfiguration> {
  protected static GsonBuilder gsonBuilder;

  static {
    gsonBuilder = new GsonBuilder();
  }

  @Override
  public String authenticate(Oauth2ClientCredentialsConfiguration configuration)
      throws ConnectorSecurityException {
    try {
      OAuth2TokenExecution.initializeForHttp();

      HttpPost request = new HttpPost(configuration.getTokenUrl());

      List<NameValuePair> params = new ArrayList<>();
      params.add(new BasicNameValuePair("grant_type", "client_credentials"));

      // Scope is optional for OAuth2 client credentials
      if (StringUtils.isNotBlank(configuration.getScope())) {
        params.add(new BasicNameValuePair("scope", configuration.getScope()));
      }

      request.setEntity(new UrlEncodedFormEntity(params));

      String auth =
          configuration.getClientId()
              + ":"
              + GuardedStringUtil.read(configuration.getClientSecret());

      byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
      String authHeader = "Basic " + new String(encodedAuth);
      request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
      return OAuth2TokenExecution.executeRequest(
          this,
          configuration,
          getHttpClient(),
          request,
          new UrlEncodedFormEntity(params),
          gsonBuilder);

    } catch (IOException e) {
      throw new ConnectorSecurityException(
          "Unexpected error occurred during OAuth2 call: " + e.getMessage(), e);
    }
  }

  public HttpClient getHttpClient() {
    return HttpClients.createDefault();
  }
}
