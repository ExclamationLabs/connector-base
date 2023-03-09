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

package com.exclamationlabs.connid.base.connector.driver.rest;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TrustStoreConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.RestConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.HttpBasicAuthConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.ProxyConfiguration;
import com.exclamationlabs.connid.base.connector.driver.BaseDriver;
import com.exclamationlabs.connid.base.connector.driver.exception.DriverRenewableTokenExpiredException;
import com.exclamationlabs.connid.base.connector.driver.exception.DriverTokenExpiredException;
import com.exclamationlabs.connid.base.connector.driver.rest.util.CustomConnectionSocketFactory;
import com.exclamationlabs.connid.base.connector.driver.rest.util.HttpDeleteWithBody;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Map;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.identityconnectors.framework.common.exceptions.ConnectionBrokenException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

/**
 * Abstract class for drivers that need to make calls to RESTful web services to manage user and
 * group information.
 */
public abstract class BaseRestDriver<U extends ConnectorConfiguration> extends BaseDriver<U> {

  protected static GsonBuilder gsonBuilder;

  protected U configuration;
  protected Authenticator<U> authenticator;

  protected HttpClientContext socksProxyClientContext;

  protected HttpClient restClient = null;

  @Override
  public void initialize(U config, Authenticator<U> auth) throws ConnectorException {
    TrustStoreConfiguration.clearJdkProperties();
    authenticator = auth;
    if (!(config instanceof RestConfiguration)) {
      throw new ConnectorException("RestConfiguration not setup for connector");
    }
    configuration = config;
    gsonBuilder = new GsonBuilder();
  }

  /**
   * Return the fault processor that will be used to analyze and respond to HTTP error responses.
   *
   * @return RestFaultProcessor subclass to handle REST faults for this Driver.
   */
  protected abstract RestFaultProcessor getFaultProcessor();

  /**
   * Setup HTTP Client.
   *
   * <p>If configuration implements HttpBasicAuthConfiguration, a Client with HTTP Basic
   * Authentication support with configured username and password will be constructed.
   *
   * <p>If configuration implements ProxyConfiguration, a Client with Proxy connection information
   * will be established. Supported proxy types are 'socks5' and 'http'.
   *
   * @return A constructed HttpClient set up as needed per configuration values.
   */
  protected HttpClient createClient() {
    if (restClient != null) {
      Logger.debug(
          this,
          String.format(
              "Reusing prior restClient %s for driver %s",
              restClient.getClass().getName(), this.getClass().getSimpleName()));
      return restClient;
    }

    boolean usesHttpBasicAuth = getConfiguration() instanceof HttpBasicAuthConfiguration;
    boolean usesProxy = getConfiguration() instanceof ProxyConfiguration;
    boolean socksProxy = false;
    PoolingHttpClientConnectionManager socksProxyConnectionManager = null;
    DefaultProxyRoutePlanner httpProxyRoutePlanner = null;
    CredentialsProvider basicAuthProvider = null;
    if (usesProxy) {
      ProxyConfiguration proxyConfiguration = (ProxyConfiguration) getConfiguration();
      socksProxy = StringUtils.equalsIgnoreCase("socks", proxyConfiguration.getProxyType());
      if (socksProxy) {
        socksProxyClientContext = setupSocksProxyContext(proxyConfiguration);
        socksProxyConnectionManager = setupSocksProxyConnectionManager(proxyConfiguration);
      } else {
        httpProxyRoutePlanner = setupHttpProxyRouteManager(proxyConfiguration);
      }
    }
    if (usesHttpBasicAuth) {
      basicAuthProvider = setupBasicAuth((HttpBasicAuthConfiguration) getConfiguration());
    }

    if (usesHttpBasicAuth) {
      if (usesProxy) {
        if (socksProxy) {
          restClient =
              HttpClients.custom()
                  .setConnectionManager(socksProxyConnectionManager)
                  .setDefaultCredentialsProvider(basicAuthProvider)
                  .build();
        } else {
          restClient =
              HttpClients.custom()
                  .setRoutePlanner(httpProxyRoutePlanner)
                  .setDefaultCredentialsProvider(basicAuthProvider)
                  .build();
        }
      } else {
        restClient =
            HttpClientBuilder.create().setDefaultCredentialsProvider(basicAuthProvider).build();
      }

    } else {
      if (usesProxy) {
        if (socksProxy) {
          restClient =
              HttpClients.custom().setConnectionManager(socksProxyConnectionManager).build();
        } else {
          restClient = HttpClients.custom().setRoutePlanner(httpProxyRoutePlanner).build();
        }
      } else {
        restClient = HttpClients.custom().build();
      }
    }

    Logger.debug(
        this,
        String.format(
            "Calculated new restClient %s for driver %s",
            restClient.getClass().getName(), this.getClass().getSimpleName()));
    return restClient;
  }

  protected DefaultProxyRoutePlanner setupHttpProxyRouteManager(ProxyConfiguration configuration) {
    HttpHost proxyHost = new HttpHost(configuration.getProxyHost(), configuration.getProxyPort());
    return new DefaultProxyRoutePlanner(proxyHost);
  }

  protected PoolingHttpClientConnectionManager setupSocksProxyConnectionManager(
      ProxyConfiguration configuration) {
    Registry<ConnectionSocketFactory> reg =
        RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new CustomConnectionSocketFactory(SSLContexts.createSystemDefault()))
            .build();
    return new PoolingHttpClientConnectionManager(reg);
  }

  protected HttpClientContext setupSocksProxyContext(ProxyConfiguration configuration) {
    InetSocketAddress socksAddr =
        new InetSocketAddress(configuration.getProxyHost(), configuration.getProxyPort());
    HttpClientContext context = HttpClientContext.create();
    context.setAttribute("socks.address", socksAddr);
    return context;
  }

  protected CredentialsProvider setupBasicAuth(HttpBasicAuthConfiguration configuration) {
    CredentialsProvider basicAuthProvider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials =
        new UsernamePasswordCredentials(
            configuration.getBasicUsername(), configuration.getBasicPassword());
    basicAuthProvider.setCredentials(AuthScope.ANY, credentials);
    return basicAuthProvider;
  }

  /**
   * Override this and return true if the RESTful services you are calling require Authorization:
   * Bearer [token] in the HTTP header. If true, this header will be added to all requests, with the
   * token coming from value stored in ConnectorConfiguration getCredentialAccessToken().
   *
   * @return true if Driver uses Bearer authorization.
   */
  protected boolean usesBearerAuthorization() {
    return false;
  }

  /**
   * Override this and return true if the RESTful services you are calling require Authorization:
   * Token token=[token] in the HTTP header. If true, this header will be added to all requests,
   * with the token coming from value stored in ConnectorConfiguration getCredentialAccessToken().
   *
   * @return true if Driver uses Token authorization.
   */
  protected boolean usesTokenAuthorization() {
    return false;
  }

  /**
   * Override this method if needed to perform a custom action after a new token has been
   * successfully obtained for an expired security token, and before the valid response of the
   * original request is returned.
   */
  protected void performPostNewAccessTokenCustomAction() {}

  /**
   * This method should return the base Service URL for RESTful endpoints that this driver invokes.
   * If not used and full URL needs to be fully reconstructed for every different RESTful API call,
   * have this method return empty string (not null).
   *
   * @return Empty string or String containing a beginning portion of the RESTful URL that will need
   *     to be invoked
   */
  protected abstract String getBaseServiceUrl();

  public <T> RestResponseData<T> executeRequest(RestRequest<T> request) {
    return executeRequest(request, false, 0);
  }

  public <T> RestResponseData<T> executeRequest(
      RestRequest<T> request, boolean isRetry, int retryCount) {
    if (gsonBuilder == null || getFaultProcessor() == null || configuration == null) {
      throw new ConnectionBrokenException(
          "Connection invalidated or disposed, request cannot "
              + "be performed.  Gsonbuilder: "
              + gsonBuilder
              + "; faultProcessor: "
              + getFaultProcessor()
              + "; configuration: "
              + configuration);
    }
    HttpRequestBase requestForClient = prepareHttpRequest(request);
    if (requestForClient instanceof HttpEntityEnclosingRequestBase) {
      setupJsonRequestBody((HttpEntityEnclosingRequestBase) requestForClient, request);
    }

    HttpClient client = createClient();
    HttpResponse response;
    int responseStatusCode;
    Header[] responseHeaders;

    try {
      Logger.debug(
          this,
          String.format(
              "Request details: %s to %s",
              requestForClient.getMethod(), requestForClient.getURI()));
      if (socksProxyClientContext != null) {
        response = client.execute(requestForClient, socksProxyClientContext);
      } else {
        response = client.execute(requestForClient);
      }

      Logger.debug(
          this,
          String.format(
              "Received %d response for %s %s",
              response.getStatusLine().getStatusCode(),
              requestForClient.getMethod(),
              requestForClient.getURI()));

      responseStatusCode = response.getStatusLine().getStatusCode();
      responseHeaders = response.getAllHeaders();

      Logger.info(this, String.format("Response status code is %d", responseStatusCode));
      if (responseStatusCode >= HttpStatus.SC_BAD_REQUEST) {
        Logger.info(
            this, String.format("request execution failed; status code is %d", responseStatusCode));
        getFaultProcessor().process(response, gsonBuilder);
      }

    } catch (DriverRenewableTokenExpiredException retryE) {
      if (isRetry) {
        Logger.error(
            this,
            String.format(
                "Driver %s token %s still invalid after re-authentication, should investigate",
                this.getClass().getSimpleName(), configuration.getCurrentToken()));
        throw new ConnectorException("Service rejected re-authenticated token for driver", retryE);
      } else {
        Logger.info(
            this,
            String.format(
                "Driver %s encountered token expiration and will attempt to reauthenticate.",
                this.getClass().getSimpleName()));
        configuration.setCurrentToken(authenticator.authenticate(configuration));
        Logger.info(
            this,
            String.format(
                "Driver %s acquired a new access token and will re-attempt original driver request once.",
                this.getClass().getSimpleName()));
        prepareHeaders(requestForClient);
        RestResponseData<T> holdResult = executeRequest(request, true, 1);
        performPostNewAccessTokenCustomAction();
        return holdResult;
      }
    } catch (DriverTokenExpiredException tokenE) {
      Logger.info(
          this,
          String.format(
              "Driver %s token %s is now invalid and will not retry.",
              this.getClass().getSimpleName(), configuration.getCurrentToken()));
      throw new ConnectorException("Token expired or rejected during driver usage", tokenE);
    } catch (ClientProtocolException e) {
      throw new ConnectorException(
          "Unexpected ClientProtocolException occurred while attempting call: " + e.getMessage(),
          e);
    } catch (IOException e) {
      if (getIoErrorRetryCount() > 0) {
        if (isRetry) {
          if (retryCount < getIoErrorRetryCount()) {
            return executeRequest(request, true, ++retryCount);
          } else {
            throw new ConnectorException(
                "Unexpected IOException occurred while attempting call: "
                    + e.getMessage()
                    + ".  "
                    + retryCount
                    + " retries were attempted.",
                e);
          }
        } else {
          return executeRequest(request, true, 1);
        }
      } else {
        throw new ConnectorException(
            "Unexpected IOException occurred while attempting call: " + e.getMessage(), e);
      }
    }

    T responseData = interpretResponse(response, request);
    return new RestResponseData<>(responseData, responseHeaders, responseStatusCode);
  }

  protected HttpRequestBase prepareHttpRequest(RestRequest<?> input) {
    final String destinationUrl =
        StringUtils.isNotBlank(input.getFullUrl())
            ? input.getFullUrl()
            : getBaseServiceUrl() + input.getRequestUri();
    HttpRequestBase requestBase;
    switch (input.getMethod()) {
      case POST:
        requestBase = new HttpPost(destinationUrl);
        break;
      case PUT:
        requestBase = new HttpPut(destinationUrl);
        break;
      case PATCH:
        requestBase = new HttpPatch(destinationUrl);
        break;
      case DELETE:
        if (input.getRequestBody() != null) {
          requestBase = new HttpDeleteWithBody(destinationUrl);
        } else {
          requestBase = new HttpDelete(destinationUrl);
        }
        break;
      default:
        requestBase = new HttpGet(destinationUrl);
        break;
    }

    prepareHeaders(requestBase);
    if (!input.getAdditionalHeaders().isEmpty()) {
      input.getAdditionalHeaders().forEach(requestBase::addHeader);
    }

    return requestBase;
  }

  @Deprecated
  public <T> RestResponseData<T> executeGetRequest(String restUri, Class<T> expectedResponseType) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType).withGet().withRequestUri(restUri).build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executePostRequest(
      String restUri, Class<T> expectedResponseType, Object requestBody) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withPost()
            .withRequestBody(requestBody)
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executePutRequest(
      String restUri, Class<T> expectedResponseType, Object requestBody) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withPut()
            .withRequestBody(requestBody)
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executePatchRequest(
      String restUri, Class<T> expectedResponseType, Object requestBody) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withPatch()
            .withRequestBody(requestBody)
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executeDeleteRequest(
      String restUri, Class<T> expectedResponseType) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withDelete()
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executeDeleteRequest(
      String restUri, Class<T> expectedResponseType, Object requestBody) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withDelete()
            .withRequestBody(requestBody)
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executeGetRequest(
      String restUri, Class<T> expectedResponseType, Map<String, String> additionalHeaders) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withGet()
            .withHeaders(additionalHeaders)
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executePostRequest(
      String restUri,
      Class<T> expectedResponseType,
      Object requestBody,
      Map<String, String> additionalHeaders) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withPost()
            .withRequestBody(requestBody)
            .withHeaders(additionalHeaders)
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executePutRequest(
      String restUri,
      Class<T> expectedResponseType,
      Object requestBody,
      Map<String, String> additionalHeaders) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withPut()
            .withRequestBody(requestBody)
            .withHeaders(additionalHeaders)
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executePatchRequest(
      String restUri,
      Class<T> expectedResponseType,
      Object requestBody,
      Map<String, String> additionalHeaders) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withPatch()
            .withRequestBody(requestBody)
            .withHeaders(additionalHeaders)
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executeDeleteRequest(
      String restUri, Class<T> expectedResponseType, Map<String, String> additionalHeaders) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withDelete()
            .withHeaders(additionalHeaders)
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  @Deprecated
  public <T> RestResponseData<T> executeDeleteRequest(
      String restUri,
      Class<T> expectedResponseType,
      Object requestBody,
      Map<String, String> additionalHeaders) {
    return executeRequest(
        new RestRequest.Builder<>(expectedResponseType)
            .withDelete()
            .withRequestBody(requestBody)
            .withHeaders(additionalHeaders)
            .withRequestUri(restUri)
            .build(),
        false,
        0);
  }

  private void prepareHeaders(HttpRequestBase request) {
    // Normally, RESTful services only transmit JSON
    request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    if (usesBearerAuthorization()) {
      request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + configuration.getCurrentToken());
    } else if (usesTokenAuthorization()) {
      request.setHeader(
          HttpHeaders.AUTHORIZATION, "Token token=" + configuration.getCurrentToken());
    }
  }

  protected void setupJsonRequestBody(
      HttpEntityEnclosingRequestBase request, RestRequest<?> restRequest) {
    if (restRequest.getRequestBody() != null) {
      String bodyData;
      if (restRequest.getRequestBody() instanceof String) {
        bodyData = restRequest.getRequestBody().toString();
      } else {
        Gson gson;
        if (restRequest.getSerializationExclusionStrategy() != null) {
          gson =
              new GsonBuilder()
                  .addSerializationExclusionStrategy(
                      restRequest.getSerializationExclusionStrategy())
                  .create();
        } else {
          gson = gsonBuilder.create();
        }
        bodyData = gson.toJson(restRequest.getRequestBody());
        Logger.debug(
            this,
            String.format(
                "JSON formatted request for %s: %s",
                restRequest.getRequestBody().getClass().getName(), bodyData));
      }

      try {
        request.setEntity(new StringEntity(bodyData));
      } catch (UnsupportedEncodingException e) {
        throw new ConnectorException("Request body encoding failed for data: " + bodyData, e);
      }
    }
  }

  protected <T> T interpretResponse(HttpResponse response, RestRequest<T> requestDetail) {
    T result;
    String rawJson;

    try {
      if (requestDetail.getResponseClass() == null
          || requestDetail.getResponseClass() == Void.class) {
        Logger.debug(
            this, "No response expected or needed from this invocation, returning null type");
        return null;
      }
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_NOT_FOUND) {
        rawJson = EntityUtils.toString(response.getEntity(), Charsets.UTF_8.name());
        Logger.debug(this, String.format("Received raw JSON: %s", rawJson));
      } else {
        Logger.debug(this, "Received HTTP Not Found for call, returning empty response type");
        return null;
      }
    } catch (ParseException pe) {
      throw new ConnectorException(
          "ParseException while reading raw JSON body from response: " + pe.getMessage(), pe);
    } catch (IOException ioe) {
      throw new ConnectorException(
          "IOException while reading raw JSON body from response:" + ioe.getMessage(), ioe);
    }

    if (requestDetail.getResponseClass() == String.class) {
      return (T) rawJson;
    }

    Gson gson;
    if (requestDetail.getDeserializationExclusionStrategy() != null) {
      gson =
          new GsonBuilder()
              .addDeserializationExclusionStrategy(
                  requestDetail.getDeserializationExclusionStrategy())
              .create();
    } else {
      gson = gsonBuilder.create();
    }
    try {
      result = gson.fromJson(rawJson, requestDetail.getResponseClass());
      Logger.info(
          this,
          String.format(
              "Successfully populated model type %s from JSON response body",
              requestDetail.getResponseClass().getName()));
    } catch (JsonSyntaxException jse) {
      throw new ConnectorException(
          "JSON syntax error occurred while trying to interpret JSON response "
              + "into type "
              + requestDetail.getResponseClass().getName(),
          jse);
    }

    return result;
  }

  private int getIoErrorRetryCount() {
    return ((RestConfiguration) configuration).getIoErrorRetries();
  }

  public U getConfiguration() {
    return configuration;
  }
}
