/*
    Copyright 2025 Exclamation Labs

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

package com.exclamationlabs.connid.base.edition.neo.driver.rest;

import static java.net.HttpURLConnection.*;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.RestConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.HttpBasicAuthConfiguration;
import com.exclamationlabs.connid.base.connector.driver.exception.DriverDataNotFoundException;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.github.resilience4j.ratelimiter.RateLimiter;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

/**
 * RestClient is a RESTful HTTP client that utilizes a supplied RestConfiguration and RestBehavior
 * implementation to conveniently automate REST interactions for base connector implementations.
 * Uses native Java 11+ HttpClient API to perform HTTP requests and Google GSON API for JSON
 * serialization/deserialization process of model data. Supports GET, POST, PUT, PATCH (JSON PATCH
 * specification) and DELETE HTTP methods.
 *
 * @param <T> The type of connector configuration. This should be a subclass of RestConfiguration.
 */
public class RestClient<T extends RestConfiguration> {

  public static final String JSON_PATCH_JSON_CONTENT_TYPE = "application/json-patch+json";

  protected Authenticator<T> authenticator;
  protected T configuration;
  protected RestBehavior<T> behavior;

  /** Hide empty constructor and make sure parameterized constructor is used. */
  private RestClient() {}

  /**
   * Required constructor for RestClient
   *
   * @param configuration RestConfiguration configuration object for the connector. This is used to
   *     determine any configured values needed to perform RESTful operations.
   * @param authenticator The Authenticator implementation used for authenticating to the RESTful
   *     API.
   * @param behavior The RestBehavior implementation used to define some of the general behavior and
   *     nature of the RESTful API and how it's used.
   */
  public RestClient(T configuration, Authenticator<T> authenticator, RestBehavior<T> behavior) {
    this.authenticator = authenticator;
    this.configuration = configuration;
    this.behavior = behavior;
  }

  /**
   * Issue a GET request to the RESTful API using the specified URI and response type.
   *
   * @param uri The URI to be used for the GET request. This should be a relative URI beginning with
   *     a '/'.
   * @param responseType The model object that the JSON response will be mapped to.
   * @return RestResponse The response object containing the headers, status code and model data
   *     object.
   * @param <U> model object used for response type. The HTTP response body JSON will be serialized
   *     to this object type.
   */
  public <U> RestResponse<U> get(String uri, Class<U> responseType) {
    return get(uri, responseType, Collections.emptyMap());
  }

  /**
   * Issue a GET request to the RESTful API using the specified URI and response type.
   *
   * @param uri The URI to be used for the GET request. This should be a relative URI beginning with
   *     a '/'.
   * @param responseType The model object that the JSON response will be mapped to.
   * @param additionalHeaders A map of additional HTTP headers to be used for this request.
   * @return RestResponse The response object containing the headers, status code and model data
   *     object.
   * @param <U> model object used for response type. The HTTP response body JSON will be serialized
   *     to * this object type.
   */
  public <U> RestResponse<U> get(
      String uri, Class<U> responseType, Map<String, String> additionalHeaders) {
    HttpRequest request = constructCoreRequest(uri, additionalHeaders).GET().build();
    return executeRequest(request, responseType);
  }

  /**
   * Issue a POST request to the RESTful API using the specified URI, request body and response
   * type.
   *
   * @param uri The URI to be used for the POST request. This should be a relative URI beginning
   *     with a '/'.
   * @param requestBody The request body to be sent with the POST request. This object will be
   *     serialized to JSON format and sent as the HTTP request body.
   * @param responseType The model object that the JSON response will be mapped to.
   * @return RestResponse The response object containing the headers, status code and response model
   *     object.
   * @param <U> response object used for response type. The HTTP response body JSON will be
   *     serialized to this object type.
   */
  public <U> RestResponse<U> post(String uri, Object requestBody, Class<U> responseType) {
    return post(uri, requestBody, responseType, Collections.emptyMap());
  }

  /**
   * Issue a POST request to the RESTful API using the specified URI, request body and response
   * type.
   *
   * @param uri The URI to be used for the POST request. This should be a relative URI beginning
   *     with a '/'.
   * @param requestBody The request body to be sent with the POST request. This object will be
   *     serialized to JSON format and sent as the HTTP request body.
   * @param responseType The model object that the JSON response will be mapped to.
   * @param additionalHeaders A map of additional HTTP headers to be used for this request.
   * @return RestResponse The response object containing the headers, status code and response model
   *     object.
   * @param <U> response object used for response type. The HTTP response body JSON will be
   *     serialized to this object type.
   */
  public <U> RestResponse<U> post(
      String uri,
      Object requestBody,
      Class<U> responseType,
      Map<String, String> additionalHeaders) {
    if (requestBody != null) {
      String requestBodyString = setupJsonRequestBody(requestBody);
      HttpRequest request =
          constructCoreRequest(uri, additionalHeaders)
              .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
              .build();
      return executeRequest(request, responseType);
    } else {
      HttpRequest request =
          constructCoreRequest(uri, additionalHeaders)
              .POST(HttpRequest.BodyPublishers.ofString(""))
              .build();
      return executeRequest(request, responseType);
    }
  }

  /**
   * Issue a PUT request to the RESTful API using the specified URI, request body and response type.
   *
   * @param uri The URI to be used for the PUT request. This should be a relative URI beginning with
   *     a '/'.
   * @param requestBody The request body to be sent with the PUT request. This object will be
   *     serialized to JSON format and sent as the HTTP request body.
   * @param responseType The model object that the JSON response will be mapped to.
   * @return RestResponse The response object containing the headers, status code and response model
   *     object.
   * @param <U> response object used for response type. The HTTP response body JSON will be
   *     serialized to this object type.
   */
  public <U> RestResponse<U> put(String uri, Object requestBody, Class<U> responseType) {
    return put(uri, requestBody, responseType, Collections.emptyMap());
  }

  /**
   * Issue a PUT request to the RESTful API using the specified URI, request body and response type.
   *
   * @param uri The URI to be used for the PUT request. This should be a relative URI beginning with
   *     a '/'.
   * @param requestBody The request body to be sent with the PUT request. This object will be
   *     serialized to JSON format and sent as the HTTP request body.
   * @param responseType The model object that the JSON response will be mapped to.
   * @param additionalHeaders A map of additional HTTP headers to be used for this request.
   * @return RestResponse The response object containing the headers, status code and response model
   *     object.
   * @param <U> response object used for response type. The HTTP response body JSON will be
   *     serialized to this object type.
   */
  public <U> RestResponse<U> put(
      String uri,
      Object requestBody,
      Class<U> responseType,
      Map<String, String> additionalHeaders) {
    if (requestBody != null) {
      String requestBodyString = setupJsonRequestBody(requestBody);
      HttpRequest request =
          constructCoreRequest(uri, additionalHeaders)
              .PUT(HttpRequest.BodyPublishers.ofString(requestBodyString))
              .build();
      return executeRequest(request, responseType);
    } else {
      HttpRequest request =
          constructCoreRequest(uri, additionalHeaders)
              .PUT(HttpRequest.BodyPublishers.ofString(""))
              .build();
      return executeRequest(request, responseType);
    }
  }

  /**
   * Issue a PATCH request to the RESTful API using the specified URI, request body and response
   * type.
   *
   * @param uri The URI to be used for the PATCH request. This should be a relative URI beginning
   *     with a '/'.
   * @param requestBody The request body to be sent with the PATCH request. This object will be
   *     serialized to JSON format and sent as the HTTP request body.
   * @param responseType The model object that the JSON response will be mapped to.
   * @return RestResponse The response object containing the headers, status code and response model
   *     object.
   * @param <U> response object used for response type. The HTTP response body JSON will be
   *     serialized to this object type.
   */
  public <U> RestResponse<U> patch(String uri, Object requestBody, Class<U> responseType) {
    return patch(uri, requestBody, responseType, Collections.emptyMap());
  }

  /**
   * Issue a PATCH request to the RESTful API using the specified URI, request body and response
   * type.
   *
   * @param uri The URI to be used for the PATCH request. This should be a relative URI beginning
   *     with a '/'.
   * @param requestBody The request body to be sent with the PATCH request. This object will be
   *     serialized to JSON format and sent as the HTTP request body.
   * @param responseType The model object that the JSON response will be mapped to.
   * @param additionalHeaders A map of additional HTTP headers to be used for this request.
   * @return RestResponse The response object containing the headers, status code and response model
   *     object.
   * @param <U> response object used for response type. The HTTP response body JSON will be
   *     serialized to this object type.
   */
  public <U> RestResponse<U> patch(
      String uri,
      Object requestBody,
      Class<U> responseType,
      Map<String, String> additionalHeaders) {
    Map<String, String> requestHeaders = new HashMap<>();
    requestHeaders.put("Content-Type", JSON_PATCH_JSON_CONTENT_TYPE);
    if (additionalHeaders != null) {
      requestHeaders.putAll(additionalHeaders);
    }
    if (requestBody != null) {
      String requestBodyString = setupJsonRequestBody(requestBody);
      HttpRequest request =
          constructCoreRequest(uri, requestHeaders)
              .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBodyString))
              .build();
      return executeRequest(request, responseType);
    } else {
      HttpRequest request =
          constructCoreRequest(uri, requestHeaders)
              .method("PATCH", HttpRequest.BodyPublishers.ofString("{}"))
              .build();
      return executeRequest(request, responseType);
    }
  }

  /**
   * Issue a DELETE request to the RESTful API using the specified URI and response type.
   *
   * @param uri The URI to be used for the DELETE request. This should be a relative URI beginning
   *     with a '/'.
   * @param responseType The model object that the JSON response will be mapped to.
   * @return RestResponse The response object containing the headers, status code and response
   *     object.
   * @param <U> model object used for response type. The HTTP response body JSON will be serialized
   *     to this object type. Void.class can be used for DELETE responses that have no response
   *     body.
   */
  public <U> RestResponse<U> delete(String uri, Class<U> responseType) {
    return delete(uri, responseType, Collections.emptyMap());
  }

  /**
   * Issue a DELETE request to the RESTful API using the specified URI and response type.
   *
   * @param uri The URI to be used for the DELETE request. This should be a relative URI beginning
   *     with a '/'.
   * @param responseType The model object that the JSON response will be mapped to.
   * @param additionalHeaders A map of additional HTTP headers to be used for this request.
   * @return RestResponse The response object containing the headers, status code and response
   *     object.
   * @param <U> model object used for response type. The HTTP response body JSON will be serialized
   *     to this object type. Void.class can be used for DELETE responses that have no response
   *     body.
   */
  public <U> RestResponse<U> delete(
      String uri, Class<U> responseType, Map<String, String> additionalHeaders) {
    HttpRequest request = constructCoreRequest(uri, additionalHeaders).GET().build();
    return executeRequest(request, responseType);
  }

  /**
   * Setup the request body for the HTTP request. This will serialize the request body object to
   * JSON using Google GSON.
   *
   * @param requestBody The request body to be sent with the HTTP request. This object will be
   *     serialized to JSON format.
   * @return String The JSON string representation of the request body.
   */
  protected String setupJsonRequestBody(Object requestBody) {
    String bodyData;
    if (requestBody instanceof String) {
      bodyData = requestBody.toString();
    } else {
      Gson gson;
      // Check for serialization exclusion strategy for fields on request body to be excluded, if
      // applicable.
      if (behavior.getSerializationExclusionStrategy(configuration) != null) {
        gson =
            new GsonBuilder()
                .addSerializationExclusionStrategy(
                    behavior.getSerializationExclusionStrategy(configuration))
                .create();
      } else {
        gson = new GsonBuilder().create();
      }
      bodyData = gson.toJson(requestBody);
    }

    return bodyData;
  }

  /**
   * Execute a request to the RESTful API using the specified HttpRequest and response type.
   *
   * @param request HttpRequest containing the desired HTTP request method, URI and headers.
   * @param responseType The model object that the JSON response will be mapped to.
   * @return The response object containing the headers, status code and response object.
   * @param <U> model object used for response type. The HTTP response body JSON will be serialized
   *     to this object type.
   */
  protected <U> RestResponse<U> executeRequest(HttpRequest request, Class<U> responseType) {
    return executeRequest(request, responseType, 0);
  }

  /**
   * Execute a request to the RESTful API using the specified HttpRequest and response type.
   *
   * @param request HttpRequest containing the desired HTTP request method, URI and headers.
   * @param responseType The model object that the JSON response will be mapped to.
   * @param retryCount The current retry count for requests that have failed and are being retried
   *     after IOException(s) have occurred. This value should be 0 for the first attempt.
   * @return The response object containing the headers, status code and response object.
   * @param <U> model object used for response type. The HTTP response body JSON will be serialized
   *     to this object type.
   */
  protected <U> RestResponse<U> executeRequest(
      HttpRequest request, Class<U> responseType, int retryCount) {
    // If rate limiting is enabled, wait for permission before proceeding with the request.
    if (behavior.getRateLimiterConfig(configuration) != null) {
      RateLimiter limiter =
          RateLimiter.of(behavior.getName(), behavior.getRateLimiterConfig(configuration));
      RateLimiter.waitForPermission(limiter);
    }

    HttpClient client = createClient();
    try {
      // Execute request using native Java 11+ HttpClient API and obtain response.
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        // Successful HTTP response (200 series), proceed to process response if available
        if (response.statusCode() == SC_NO_CONTENT) {
          return new RestResponse<>(response.headers(), response.statusCode(), null);
        } else {
          // interpret response
          U responseData = interpretResponse(response.body(), responseType);
          return new RestResponse<>(response.headers(), response.statusCode(), responseData);
        }
      } else if (response.statusCode() == HTTP_UNAUTHORIZED
          && behavior.supportsReauthentication()
          && retryCount == 0) {
        // Attempt to Re-authenticate and then retry the request one more time
        String newToken = authenticator.authenticate(configuration);
        configuration.setCurrentToken(newToken);
        executeRequest(request, responseType, retryCount + 1);
      } else if (response.statusCode() == HTTP_NOT_FOUND
          && behavior.getFaultHandler() != null
          && behavior.getFaultHandler().supportsNotFound()) {
        // If HTTP 404 Not Found is received and fault handler exists and supports not found,
        // invoke the fault handler
        behavior.getFaultHandler().process(configuration, request, response);
      } else if (response.statusCode() == HTTP_NOT_FOUND) {
        // If HTTP 404 Not Found is received, throw DriverDataNotFoundException
        // that can be handled by the caller as desired
        throw new DriverDataNotFoundException("Resource not found for URI: " + request.uri());
      } else if (behavior.getFaultHandler() != null) {
        // Fault handler is present, pass non-404 HTTP 400/500 series error to fault handler
        behavior.getFaultHandler().process(configuration, request, response);
      } else {
        // No fault handler, throw ConnectorException for non-404 400/500 response
        Logger.error(
            this,
            String.format(
                "Error response code %d for %s URI %s.  Body: %s",
                response.statusCode(), request.method(), request.uri(), response.body()));
        throw new ConnectorException(
            String.format(
                "Received response code %d for %s URI %s.",
                response.statusCode(), request.method(), request.uri()));
      }

    } catch (IOException | InterruptedException e) {
      if (configuration.getIoErrorRetries() != null
          && configuration.getIoErrorRetries() > 0
          && retryCount < configuration.getIoErrorRetries()) {
        // IOException encountered - but retry count is not exceeded, reattempt same request
        Logger.warn(
            this,
            String.format(
                "Retrying request %s %s due to error: %s",
                request.method(), request.uri(), e.getMessage()));
        return executeRequest(request, responseType, retryCount + 1);
      }
      // Retry count exceeded or not configured, throw ConnectorException for IOException
      throw new ConnectorException(
          String.format(
              "IOException while executing %s %s request (retries executed: %d): %s",
              request.method(), request.uri(), retryCount, e.getMessage()),
          e);
    }
    return null;
  }

  /**
   * Interpret the response from the RESTful API and map it to the specified model object type.
   *
   * @param rawJson String containing the JSON response
   * @param responseType Object type to be used for mapping the JSON response
   * @return Object of the specified type containing the mapped JSON response. If there was no
   *     response data or type is void, null will be returned.
   * @param <U> model object used for response type. The HTTP response body JSON will be serialized
   *     to this object type.
   */
  @SuppressWarnings("unchecked")
  protected <U> U interpretResponse(String rawJson, Class<U> responseType) {
    U result;

    try {
      if (responseType == null || responseType == Void.class) {
        Logger.debug(
            this, "No response expected or needed from this invocation, returning null type");
        return null;
      }

      if (responseType == String.class) {
        // Called requested raw String response instead of JSON serialization, return String.
        return (U) rawJson;
      }

      Gson gson;
      if (behavior.getDeserializationExclusionStrategy(configuration) != null) {
        // Use deserialization exclusion strategy to exclude requested fields from being
        // deserialized
        gson =
            new GsonBuilder()
                .addDeserializationExclusionStrategy(
                    behavior.getDeserializationExclusionStrategy(configuration))
                .create();
      } else {
        // Normal GSON deserialization, no exclusion strategy
        gson = new GsonBuilder().create();
      }

      // Perform the JSON deserialization process using GSON
      result = gson.fromJson(rawJson, responseType);
      Logger.debug(
          this,
          String.format(
              "Successfully populated model type %s from JSON response body",
              responseType.getName()));
      return result;
    } catch (JsonSyntaxException jse) {
      throw new ConnectorException(
          String.format(
              "JSON syntax error occurred while trying to interpret JSON response "
                  + "into type %s.  Raw JSON: %s",
              responseType == null ? "null" : responseType.getName(), rawJson),
          jse);
    }
  }

  /**
   * Construct the core HTTP request using supplied URI and additional headers.
   *
   * @param uri The URI to be used for the request. This should be a relative URI beginning with a
   *     '/'.
   * @param additionalHeadersForRequest A map of additional HTTP headers to be used for this
   *     request. If null or empty, will not be used.
   * @return HttpRequest.Builder The HttpRequest.Builder object containing the constructed request.
   * @throws ConnectorException If an error occurs while constructing the URI or setting up header
   *     values.
   */
  protected HttpRequest.Builder constructCoreRequest(
      String uri, Map<String, String> additionalHeadersForRequest) throws ConnectorException {
    final String REQUEST_URL = behavior.getBaseServiceUrl(configuration) + uri;
    URI requestUri;
    try {
      requestUri = new URI(REQUEST_URL);
    } catch (URISyntaxException e) {
      throw new ConnectorException("Invalid URI: " + REQUEST_URL, e);
    }
    Map<String, String> headers = new HashMap<>();
    headers.put("Accept", behavior.getAcceptType());
    headers.put("Content-Type", behavior.getContentType());
    if (behavior.usesBearerAuthentication()
        && (!StringUtils.isNotBlank(configuration.getCurrentToken()))) {
      headers.put("Authorization", "Bearer " + configuration.getCurrentToken());
    }
    if (behavior.getAdditionalHeaders(configuration) != null
        && !behavior.getAdditionalHeaders(configuration).isEmpty()) {
      headers.putAll(behavior.getAdditionalHeaders(configuration));
    }
    if (additionalHeadersForRequest != null && !additionalHeadersForRequest.isEmpty()) {
      headers.putAll(additionalHeadersForRequest);
    }

    String[] flattenedHeaders =
        headers.entrySet().stream()
            .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
            .toArray(String[]::new);
    try {
      return HttpRequest.newBuilder().uri(requestUri).headers(flattenedHeaders);
    } catch (IllegalArgumentException e) {
      throw new ConnectorException("Invalid Request URL: " + REQUEST_URL, e);
    }
  }

  /**
   * Construct the HTTP client to be used for the RESTful API requests. This will be a default
   * HttpClient unless the HTTP Basic Auth is required.
   *
   * @return HttpClient to be used for RESTful API requests.
   */
  protected HttpClient createClient() {
    if (configuration instanceof HttpBasicAuthConfiguration) {
      return setupBasicAuth((HttpBasicAuthConfiguration) configuration);
    } else {
      return HttpClient.newBuilder().build();
    }
  }

  /**
   * Setup the HTTP client for Basic Authentication.
   *
   * @param configuration HttpBasicAuthConfiguration configuration containing HTTP Basic Auth
   *     username and password.
   * @return HttpClient to be used for RESTful API requests.
   */
  protected HttpClient setupBasicAuth(HttpBasicAuthConfiguration configuration) {
    return HttpClient.newBuilder()
        .authenticator(
            new java.net.Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    configuration.getBasicUsername(),
                    GuardedStringUtil.read(configuration.getBasicPassword()).toCharArray());
              }
            })
        .build();
  }
}
