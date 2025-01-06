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
  private final Cache<String, Object> cache;
  private final String cacheAllKey;
  private Function<String, T> fetchOneFromSource;
  private Function<Void, Iterable<java.util.Map.Entry<String, T>>> fetchAllFromSource;

  public CacheMap(
      String cacheName, Class<T> clazz, Duration duration, Function<String, T> fetchOneFromSource) {
    this.fetchOneFromSource = fetchOneFromSource;
    this.fetchAllFromSource = null;
    this.cacheAllKey = "FETCH_ALL_AND_CACHE";
    this.cache =
        Caffeine.newBuilder()
            .expireAfterWrite(duration.getDurationAmount(), duration.getTimeUnit())
            .build();
  }

  public CacheMap(
      String cacheName,
      Class<T> clazz,
      Duration duration,
      Function<String, T> fetchOneFromSource,
      Function<Void, Iterable<java.util.Map.Entry<String, T>>> fetchAllFromSource) {
    this(cacheName, clazz, duration, fetchOneFromSource);
    this.fetchAllFromSource = fetchAllFromSource;
  }

  @SuppressWarnings("unchecked")
  public T getValue(String key) {
    return (T) cache.get(key, fetchOneFromSource::apply);
  }

  /**
   * Fetches all values from the source and caches them. Sets a timestamp fetchAll indicator in the
   * cache to indicate that the fetch was performed. If fetchAll has already been performed, it will
   * not be performed again until the fetchAll indicator has expired.
   */
  public void fetchAll() {
    if (cache.getIfPresent(cacheAllKey) == null) {
      fetchAllFromSource.apply(null).forEach(entry -> cache.put(entry.getKey(), entry.getValue()));
      cache.put(cacheAllKey, System.currentTimeMillis());
    }
  }

  public boolean hasFetchAllBeenPerformed() {
    return cache.getIfPresent(cacheAllKey) != null;
  }

  public void clearCache() {
    cache.invalidateAll();
  }
}
