package com.exclamationlabs.connid.base.connector.driver.rest.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.function.Function;
import javax.cache.expiry.Duration;

/**
 * A simple cache map that uses Caffeine to cache values. CacheMap accepts a lambda expression for
 * fetching values from a source and storing them in the cache. The CacheMap can be implemented with
 * an expiry duration, forcing the cache to refresh using the fetch from source lambda expression.
 */
public class CacheMap<T> {
  private final Cache<String, T> cache;
  private final Function<String, T> fetchFromSource;

  public CacheMap(
      String cacheName, Class<T> clazz, Duration duration, Function<String, T> fetchFromSource) {
    this.fetchFromSource = fetchFromSource;
    this.cache =
        Caffeine.newBuilder()
            .expireAfterWrite(duration.getDurationAmount(), duration.getTimeUnit())
            .build();
  }

  public T getValue(String key) {
    return cache.get(key, fetchFromSource::apply);
  }

  public void clearCache() {
    cache.invalidateAll();
  }
}
