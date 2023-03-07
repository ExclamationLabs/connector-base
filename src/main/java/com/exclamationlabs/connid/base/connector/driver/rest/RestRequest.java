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
 * BaseRestDriver implementation. Requests should be constructed using the embeddeed Builder
 * pattern.
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

    public Builder(Class<T> responseTypeClass) {
      this.responseClass = responseTypeClass;
    }

    public Builder<T> withRequestUri(String uri) {
      this.requestUri = uri;
      return this;
    }

    public Builder<T> withRequestBody(Object bodyData) {
      this.requestBody = bodyData;
      return this;
    }

    public Builder<T> withPut() {
      this.method = PUT;
      return this;
    }

    public Builder<T> withPatch() {
      this.method = PATCH;
      return this;
    }

    public Builder<T> withGet() {
      this.method = GET;
      return this;
    }

    public Builder<T> withPost() {
      this.method = POST;
      return this;
    }

    public Builder<T> withDelete() {
      this.method = DELETE;
      return this;
    }

    public Builder<T> withHeaders(Map<String, String> headers) {
      this.additionalHeaders = headers;
      return this;
    }

    public Builder<T> withSerializationExclusionStrategy(ExclusionStrategy strategy) {
      this.serializationExclusionStrategy = strategy;
      return this;
    }

    public Builder<T> withDeserializationExclusionStrategy(ExclusionStrategy strategy) {
      this.deserializationExclusionStrategy = strategy;
      return this;
    }

    public Builder<T> withCustomHttpClient(HttpClient client) {
      this.customHttpClient = client;
      return this;
    }

    public Builder<T> withNoRetries() {
      this.ioErrorRetries = false;
      return this;
    }

    public Builder<T> withFullUrl(String url) {
      this.fullUrl = url;
      return this;
    }

    public RestRequest<T> build() {
      return new RestRequest<>(this);
    }
  }
}
