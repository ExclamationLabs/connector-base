package com.exclamationlabs.connid.base.edition.neo.driver.rest;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.google.gson.ExclusionStrategy;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.util.Collections;
import java.util.Map;

public interface RestBehavior<T extends ConnectorConfiguration> {

  public static final String APPLICATION_JSON = "application/json";

  String getName();

  boolean usesBearerAuthentication();

  boolean supportsReauthentication();

  String getBaseServiceUrl(T configuration);

  default Map<String, String> getAdditionalHeaders(T configuration) {
    return Collections.emptyMap();
  }

  default RestFaultHandler<T> getFaultHandler() {
    return null;
  }

  default String getContentType() {
    return APPLICATION_JSON;
  }

  default String getAcceptType() {
    return APPLICATION_JSON;
  }

  default ExclusionStrategy getDeserializationExclusionStrategy(T configuration) {
    return null;
  }

  default ExclusionStrategy getSerializationExclusionStrategy(T configuration) {
    return null;
  }

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
