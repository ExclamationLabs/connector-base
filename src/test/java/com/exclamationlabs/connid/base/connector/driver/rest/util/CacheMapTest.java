package com.exclamationlabs.connid.base.connector.driver.rest.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CacheMapTest {
  private CacheMap<String> cache;
  private static final String TEST_CACHE_NAME = "testCache";
  private static final Duration TEST_DURATION = new Duration(TimeUnit.SECONDS, 1);
  private int cacheMissCount = 0;

  @BeforeEach
  public void setup() {
    cache =
        new CacheMap<>(
            TEST_CACHE_NAME,
            String.class,
            TEST_DURATION,
            (key) -> {
              cacheMissCount++;
              if (key.equals("nonexistent")) {
                return null;
              }
              return "Value for " + key;
            },
            (v) ->
                Map.of(
                        "key1", "Value for key1",
                        "key2", "Value for key2")
                    .entrySet());
  }

  @Test
  public void testGetValue_CacheMiss() {
    cacheMissCount = 0;
    String value = cache.getValue("key1");
    assertEquals("Value for key1", value);
    assertEquals(1, cacheMissCount);
  }

  @Test
  public void testGetValue_CacheHit() {
    cacheMissCount = 0;
    // First call - cache miss
    String value1 = cache.getValue("key1");
    assertEquals("Value for key1", value1);
    assertEquals(1, cacheMissCount);
    // Second call - should hit cache and return same value
    String value2 = cache.getValue("key1");
    assertEquals(value1, value2);
    assertEquals(1, cacheMissCount);
  }

  @Test
  public void testClearCache() {
    cacheMissCount = 0;
    // Populate cache
    String value1 = cache.getValue("key1");
    assertEquals("Value for key1", value1);
    assertEquals(1, cacheMissCount);
    // Clear cache
    cache.clearCache();

    // Value should be recomputed
    String value2 = cache.getValue("key1");
    assertEquals("Value for key1", value2);
    assertEquals(2, cacheMissCount);
  }

  @Test
  public void testMultipleKeys() {
    cacheMissCount = 0;
    String value1 = cache.getValue("key1");
    assertEquals(1, cacheMissCount);
    String value2 = cache.getValue("key2");
    assertEquals(2, cacheMissCount);

    assertEquals("Value for key1", value1);
    assertEquals("Value for key2", value2);
    assertNotEquals(value1, value2);
  }

  @Test
  public void testGetValue_CacheExpiration() throws InterruptedException {
    cacheMissCount = 0;
    // First call - cache miss
    String value1 = cache.getValue("key1");
    assertEquals("Value for key1", value1);
    assertEquals(1, cacheMissCount);

    // Wait longer than the cache duration (1 second + small buffer)
    long testDuration = TEST_DURATION.getAdjustedTime(100);
    Thread.sleep(testDuration);

    // After expiration - should be another cache miss
    String value2 = cache.getValue("key1");
    assertEquals("Value for key1", value2);
    assertEquals(2, cacheMissCount);
  }

  @Test
  public void testFetchAll() throws InterruptedException {
    cacheMissCount = 0;
    // First call - cache miss
    cache.fetchAll();
    String value1_get1 = cache.getValue("key1");
    String value2_get1 = cache.getValue("key2");
    assertEquals("Value for key1", value1_get1);
    assertEquals("Value for key2", value2_get1);
    // Expect no cache misses
    assertEquals(0, cacheMissCount);

    // Wait longer than the cache duration (1 second + small buffer)
    long testDuration = TEST_DURATION.getAdjustedTime(100);
    Thread.sleep(testDuration);

    // After expiration - should be another cache miss
    String value1_get2 = cache.getValue("key1");
    assertEquals("Value for key1", value1_get2);
    // Expect 1 cache miss
    assertEquals(1, cacheMissCount);

    // Fetch all again
    cache.fetchAll();
    String value2_get3 = cache.getValue("key2");
    assertEquals("Value for key2", value2_get3);
    // Expect no additional cache misses
    assertEquals(1, cacheMissCount);
  }

  @Test
  public void testGetValue_NonExistentKey() throws InterruptedException {
    cacheMissCount = 0;
    // First call for non-existent key - cache miss
    String value1 = cache.getValue("nonexistent");
    assertNull(value1);
    assertEquals(1, cacheMissCount);

    // Second immediate call - should use cached null value
    String value2 = cache.getValue("nonexistent");
    assertNull(value2);
    assertEquals(1, cacheMissCount);

    // Wait longer than cache duration
    long testDuration = TEST_DURATION.getAdjustedTime(100);
    Thread.sleep(testDuration);

    // After expiration - should try fetching again
    String value3 = cache.getValue("nonexistent");
    assertNull(value3);
    assertEquals(2, cacheMissCount);
  }
}
