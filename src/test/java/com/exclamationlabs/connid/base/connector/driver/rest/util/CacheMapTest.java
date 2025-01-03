package com.exclamationlabs.connid.base.connector.driver.rest.util;

import static org.junit.jupiter.api.Assertions.*;

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
              return "Value for " + key;
            });
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
}
