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

import static com.exclamationlabs.connid.base.connector.driver.rest.RestRequestMethod.*;

import com.google.gson.ExclusionStrategy;
import java.util.Collections;
import java.util.Map;
import org.apache.http.client.HttpClient;

/**
 * Describes all specific request information needed to perform a RESTful web service request in the
 * base framework. This information is in addition to additional information supplied by the
 * BaseRestDriver implementation. Requests should be constructed using the embedded Builder pattern.
 *
 * @param <T> What response object type that the HTTP response body (if applicable) will generate
 *     to. Acceptable types are: - Void.class (web service doesn't have a response body or we don't
 *     care about it) - String.class (for cases where want to process the raw response body) - any
 *     other serializable Java POJO Class that JSON will marshal to via GSON.
 */
public class RestRequest<T> {

  private final Class<T> responseClass;
  private final RestRequestMethod method;
  private final String requestUri;

  private final Map<String, String> additionalHeaders;

  private final ExclusionStrategy serializationExclusionStrategy;

  private final ExclusionStrategy deserializationExclusionStrategy;

  private final HttpClient customHttpClient;

  private final Boolean ioErrorRetries;

  private final String fullUrl;

  private final Object requestBody;

  /**
   * This constructor, with a Builder configured as needed, must be used perform a RESTful request.
   *
   * @param builder A new Builder instance.
   */
  public RestRequest(Builder<T> builder) {
    responseClass = builder.responseClass;
    method = builder.method;
    requestUri = builder.requestUri;
    additionalHeaders = builder.additionalHeaders;
    serializationExclusionStrategy = builder.serializationExclusionStrategy;
    deserializationExclusionStrategy = builder.deserializationExclusionStrategy;
    customHttpClient = builder.customHttpClient;
    ioErrorRetries = builder.ioErrorRetries;
    fullUrl = builder.fullUrl;
    requestBody = builder.requestBody;
  }

  public Object getRequestBody() {
    return requestBody;
  }

  public String getFullUrl() {
    return fullUrl;
  }

  public Class<T> getResponseClass() {
    return responseClass;
  }

  public RestRequestMethod getMethod() {
    return method;
  }

  public String getRequestUri() {
    return requestUri;
  }

  public Map<String, String> getAdditionalHeaders() {
    return additionalHeaders;
  }

  public ExclusionStrategy getSerializationExclusionStrategy() {
    return serializationExclusionStrategy;
  }

  public ExclusionStrategy getDeserializationExclusionStrategy() {
    return deserializationExclusionStrategy;
  }

  public HttpClient getCustomHttpClient() {
    return customHttpClient;
  }

  public Boolean getIoErrorRetries() {
    return ioErrorRetries;
  }

  public static class Builder<T> {

    private RestRequestMethod method = GET;

    private String requestUri = "";
    private final Class<T> responseClass;

    private Map<String, String> additionalHeaders = Collections.emptyMap();

    private ExclusionStrategy serializationExclusionStrategy = null;
    private ExclusionStrategy deserializationExclusionStrategy = null;

    private HttpClient customHttpClient = null;

    private Boolean ioErrorRetries = true;

    private String fullUrl = null;

    private Object requestBody = null;

    /**
     * This constructor, with a class designating the response type you wish to receive, must be
     * used.a Builder configured as needed, must be used perform a RESTful request.
     *
     * @param responseTypeClass The Class type here can be: any Java object that JSON can
     *     deserialize into, or String (to receive raw String response with no deserialization), or
     *     Void (no response is expected or any response received shall be ignored).
     */
    public Builder(Class<T> responseTypeClass) {
      this.responseClass = responseTypeClass;
    }

    /**
     * Add a partial request URI that will be appended to the base service URL configured for the
     * BaseRestDriver.
     *
     * @param uri String containing a partial URI. Should form a valid URL when concatenated with
     *     base service URL.
     * @return The updated Builder instance
     */
    public Builder<T> withRequestUri(String uri) {
      this.requestUri = uri;
      return this;
    }

    /**
     * For POST, PUT, PATCH and occasionally DELETE requests, add an object to be transmitted as the
     * HTTP Body for the RESTful invocation
     *
     * @param bodyData One of the following: An object that can be serialized to JSON, a String
     *     holding a value that will be directly used as the HTTP Body, or Void type (no HTTP Body
     *     should be sent).
     * @return The updated Builder instance
     */
    public Builder<T> withRequestBody(Object bodyData) {
      this.requestBody = bodyData;
      return this;
    }

    /**
     * Specifies that HTTP PUT method should be used.
     *
     * @return The updated Builder instance
     */
    public Builder<T> withPut() {
      this.method = PUT;
      return this;
    }

    /**
     * Specifies that HTTP PATCH method should be used.
     *
     * @return The updated Builder instance
     */
    public Builder<T> withPatch() {
      this.method = PATCH;
      return this;
    }

    /**
     * Specifies that HTTP GET method should be used.
     *
     * @return The updated Builder instance
     */
    public Builder<T> withGet() {
      this.method = GET;
      return this;
    }

    /**
     * Specifies that HTTP POST method should be used.
     *
     * @return The updated Builder instance
     */
    public Builder<T> withPost() {
      this.method = POST;
      return this;
    }

    /**
     * Specifies that HTTP DELETE method should be used.
     *
     * @return The updated Builder instance
     */
    public Builder<T> withDelete() {
      this.method = DELETE;
      return this;
    }

    /**
     * Apply a map of HTTP headers to the request. The headers are additive and will replace any
     * equally-named headers constructed as dictated by Driver setup: for instance, if
     * BaseRestDriver usesBearerAuthorization() is true, a header of "Authorization: Bearer
     * sometoken" will already be put in place, and you won't need to add it.
     *
     * @param headers Map of String name-value pairs to transmit as HTTP Headers.
     * @return The updated Builder instance
     */
    public Builder<T> withHeaders(Map<String, String> headers) {
      this.additionalHeaders = headers;
      return this;
    }

    /**
     * Apply a custom ExclusionStrategy for GSON serialization of body object to JSON. An
     * ExclusionStrategy here will allow to customize which field names should be ignored and not
     * serialized to JSON.
     *
     * @param strategy Custom ExclusionStrategy implementation to use.
     * @return The updated Builder instance
     */
    public Builder<T> withSerializationExclusionStrategy(ExclusionStrategy strategy) {
      this.serializationExclusionStrategy = strategy;
      return this;
    }

    /**
     * Apply a custom ExclusionStrategy for GSON deserialization of response body JSON to the object
     * understood as a response for your implementation. An ExclusionStrategy here will allow to
     * customize which field names should be ignored and not deserialized from JSON response body
     * into the response object.
     *
     * @param strategy Custom ExclusionStrategy implementation to use.
     * @return The updated Builder instance
     */
    public Builder<T> withDeserializationExclusionStrategy(ExclusionStrategy strategy) {
      this.deserializationExclusionStrategy = strategy;
      return this;
    }

    /**
     * Apply a custom HttpClient if a custom-configured client is needed for your RESTful
     * invocation.
     *
     * @param client HttpClient to be used for RESTful invocation.
     * @return The updated Builder instance
     */
    public Builder<T> withCustomHttpClient(HttpClient client) {
      this.customHttpClient = client;
      return this;
    }

    /**
     * If used, no retries for this invocation will be attempted if an IO error occurs, regardless
     * of configuration value given for RestConfiguration ioErrorRetries.
     *
     * @return The updated Builder instance
     */
    public Builder<T> withNoRetries() {
      this.ioErrorRetries = false;
      return this;
    }

    /**
     * If supplied, the request will use the given full URL for the invocation, as opposed to
     * `withRequestUri` which is partial and uses the driver to determine full path.
     *
     * @param url String containing the full URL to be invoked
     * @return The updated Builder instance
     */
    public Builder<T> withFullUrl(String url) {
      this.fullUrl = url;
      return this;
    }

    /**
     * The final method to be used for the Builder to return it's configured instance.
     *
     * @return The Builder instance
     */
    public RestRequest<T> build() {
      return new RestRequest<>(this);
    }
  }
}
