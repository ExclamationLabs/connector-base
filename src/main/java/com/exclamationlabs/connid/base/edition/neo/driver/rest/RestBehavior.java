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

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.google.gson.ExclusionStrategy;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.util.Collections;
import java.util.Map;

/**
 * This interface defines the behavior and general operation information of RESTful API invocations.
 *
 * @param <T> Configuration subclass applicable to the connector.
 */
public interface RestBehavior<T extends ConnectorConfiguration> {

  String APPLICATION_JSON = "application/json";

  /**
   * Returns a name applicable to the RESTful API involved in request(s). This name be used for rate
   * limiting and other internal purposes to prevent possible data collisions.
   *
   * @return Name applicable to the RESTful behavior being defined.
   */
  String getName();

  /**
   * Specify whether the RESTful API requires Bearer authentication in the HTTP header. If this is
   * true, the driver will use the "Authorization" header with a value of "Bearer [token]"
   *
   * @return True if the RESTful API utilizes Bearer authentication, false if it does not.
   */
  boolean usesBearerAuthentication();

  /**
   * Specify whether the authentication strategy for this RESTful API supports re-authentication
   * (namely, the ability to reauthenticate and obtain a new valid token). This is primarily used
   * for OAuth2 strategies that involve exchanging credentials for a token with finite expiration
   * (refresh token, client credentials, etc.) If true is specified, the driver will attempt to
   * reauthenticate automatically if HTTP 401 Unauthorized is detected and it's likely that the
   * token has expired.
   *
   * @return True if the authentication strategy supports re-authentication, false if it does not.
   */
  boolean supportsReauthentication();

  /**
   * Return the base URL for the RESTful API. This should be the root URL of the RESTful API without
   * any pathing or parameters specific to the request itself. Do not include a trailing '/' as it
   * is presumed request operations will starting with a '/'. Examples: - https://api.example.com/v1
   * - http://40.10.20.100:1234/somevendor - http://examplehost:1234/somevendor -
   * https://example.com/api/v1/services
   *
   * @param configuration The configuration object for the connector. This may be used to help
   *     determine or construct the base URL.
   * @return The base URL for the RESTful API, with no trailing '/'.
   */
  String getBaseServiceUrl(T configuration);

  /**
   * Return any additional headers that need to be included for some or all requests related to the
   * RESTful API. The default behavior is no additional headers (an empty map).
   *
   * @param configuration The configuration object for the connector. This may be used to help
   *     determine or populate the additional headers.
   * @return A non-null map of additional headers to be included in the request.
   */
  default Map<String, String> getAdditionalHeaders(T configuration) {
    return Collections.emptyMap();
  }

  /**
   * Define the fault handler, if any, to be used for all requests that pertain to the RESTful API.
   * The fault handler is used for custom handling of HTTP 400/500 series errors that occur during
   * request(s) applicable to this RestBehavior definition. The default behavior is to return null,
   * indicating default error handling by the RestClient is to be used.
   *
   * @return The custom RestFaultHandler applicable to RestBehavior request(s), or null to default
   *     to the RestClient's default error handling.
   */
  default RestFaultHandler<T> getFaultHandler() {
    return null;
  }

  /**
   * Return the content type to be used for all requests that pertain to this RestBehavior. All
   * types using this behavior will include a Content-Type header with this value. The default
   * behavior is to return "application/json".
   *
   * @return Valid Content-Type header value to be used for all requests relating to this behavior.
   */
  default String getContentType() {
    return APPLICATION_JSON;
  }

  /**
   * Return the Accepts type to be used for all requests that pertain to this RestBehavior. All
   * types using this behavior will include an Accept header with this value. The default behavior
   * is to return "application/json".
   *
   * @return Valid Accept header value to be used for all requests relating to this behavior.
   */
  default String getAcceptType() {
    return APPLICATION_JSON;
  }

  /**
   * If necessary, return the ExclusionStrategy to be used for serialization of request body data
   * sent to the RESTful API. This is used to exclude certain fields from being serialized into JSON
   * from the Java object model via Google GSON. The default behavior is return null, indicating no
   * exclusion strategy is needed for requests.
   *
   * @param configuration The configuration object for the connector. This may be used to help
   *     determine or populate the ExclusionStrategy.
   * @return The ExclusionStrategy to be used for serialization, or null if none is needed.
   */
  default ExclusionStrategy getSerializationExclusionStrategy(T configuration) {
    return null;
  }

  /**
   * If necessary, return the ExclusionStrategy to be used for deserialization of responses from the
   * RESTful API. This is used to exclude certain fields from being deserialized into the Java
   * object model via Google GSON. The default behavior is return null, indicating no exclusion
   * strategy is needed for responses.
   *
   * @param configuration The configuration object for the connector. This may be used to help
   *     determine or populate the ExclusionStrategy.
   * @return The ExclusionStrategy to be used for deserialization, or null if none is needed.
   */
  default ExclusionStrategy getDeserializationExclusionStrategy(T configuration) {
    return null;
  }

  /**
   * Return the RateLimiterConfig to be used for this HTTP request(s) pertaining to this
   * RestBehavior. This ability is used to accommodate provider API or account rate limits and
   * ensure thresholds are not violated. The default behavior is to return null, indicating no rate
   * limiting is used.
   *
   * @param configuration The configuration object for the connector. This may be used to help
   *     determine or populate the RateLimiterConfig.
   * @return The built RateLimiterConfig object, or null if not used.
   */
  default RateLimiterConfig getRateLimiterConfig(T configuration) {
    // Example:
    //        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
    //                .limitForPeriod(10) // Max 10 requests per minute
    //                .limitRefreshPeriod(Duration.ofMinutes(1))
    //                .timeoutDuration(Duration.ofSeconds(2))
    //                .build();
    return null;
  }
}
