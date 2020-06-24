package com.exclamationlabs.connid.base.connector.driver.rest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TrustStoreConfigurationTest {

    @Test
    public void test() {
        System.setProperty(TrustStoreConfiguration.TRUST_STORE_TYPE_PROPERTY,
                "test_truststoretype");
        System.setProperty(TrustStoreConfiguration.TRUST_STORE_PROPERTY,
                "test_truststore");
        String testType = System.getProperty(TrustStoreConfiguration.TRUST_STORE_TYPE_PROPERTY);
        String test = System.getProperty(TrustStoreConfiguration.TRUST_STORE_PROPERTY);
        assertEquals("test_truststoretype", testType);
        assertEquals("test_truststore", test);
        TrustStoreConfiguration.clearJdkProperties();
        assertNull( System.getProperty(TrustStoreConfiguration.TRUST_STORE_TYPE_PROPERTY));
        assertNull( System.getProperty(TrustStoreConfiguration.TRUST_STORE_PROPERTY));
    }
}
