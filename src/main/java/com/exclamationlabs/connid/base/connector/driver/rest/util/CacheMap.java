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
  private Function<String, Object> fetchOneFromSource;
  private Function<Void, Iterable<java.util.Map.Entry<String, T>>> fetchAllFromSource;

  private class NonExistentValue {
    private final String key;

    NonExistentValue(String key) {
      this.key = key;
    }

    @Override
    public String toString() {
      return "No value for key: " + key;
    }
  }

  public CacheMap(
      String cacheName, Class<T> clazz, Duration duration, Function<String, T> fetchOneFromSource) {
    this.fetchOneFromSource =
        (key) -> {
          Object o = fetchOneFromSource.apply(key);
          if (o == null) {
            return new NonExistentValue(key);
          }
          return o;
        };
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

  /**
   * Get the value from the cache for a given key. If the key is not found in the cache, the
   * fetchOneFromSource lambda expression is used to fetch the value from the source. If the value
   * is not found, a NonExistentValue is returned. The NonExistentValue is a special value that is
   * used to indicate that the value is not found in the source. This state is tracked in the cache
   * so that subsequent requests for the key from the cache will not attempt to fetch the value from
   * the source again, at least until the cache expires.
   *
   * @param key
   * @return T the value from the cache or null if the value is not found in the source
   */
  @SuppressWarnings("unchecked")
  public T getValue(String key) {
    Object o = cache.get(key, fetchOneFromSource::apply);
    if (o != null && o instanceof CacheMap.NonExistentValue) {
      return null;
    }
    return (T) o;
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
