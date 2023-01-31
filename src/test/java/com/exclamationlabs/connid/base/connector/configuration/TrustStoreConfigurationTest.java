package com.exclamationlabs.connid.base.connector.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TrustStoreConfigurationTest {

  @Test
  public void test() {
    System.setProperty(TrustStoreConfiguration.TRUST_STORE_TYPE_PROPERTY, "test_truststoretype");
    System.setProperty(TrustStoreConfiguration.TRUST_STORE_PROPERTY, "test_truststore");
    String testType = System.getProperty(TrustStoreConfiguration.TRUST_STORE_TYPE_PROPERTY);
    String test = System.getProperty(TrustStoreConfiguration.TRUST_STORE_PROPERTY);
    assertEquals("test_truststoretype", testType);
    assertEquals("test_truststore", test);
    TrustStoreConfiguration.clearJdkPropertiesForce();
    assertNull(System.getProperty(TrustStoreConfiguration.TRUST_STORE_TYPE_PROPERTY));
    assertNull(System.getProperty(TrustStoreConfiguration.TRUST_STORE_PROPERTY));
  }
}
