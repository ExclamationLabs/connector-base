package com.exclamationlabs.connid.base.connector.driver.rest;

import org.identityconnectors.common.logging.Log;

public class TrustStoreConfiguration {
    private static final String TRUST_STORE_TYPE_PROPERTY = "javax.net.ssl.trustStoreType";
    private static final String TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";

    private static final Log LOG = Log.getLog(TrustStoreConfiguration.class);

    private TrustStoreConfiguration() {}

    public static void clearJdkProperties() {

        LOG.info("Authentication to Zoom starting, obtaining new access token ...");
        LOG.info("Clearing out property {0} for Zoom auth support.  Value was {1}",
                TRUST_STORE_TYPE_PROPERTY, System.getProperty(TRUST_STORE_TYPE_PROPERTY));
        LOG.info("Clearing out property {0} for Zoom auth support.  Value was {1}",
                TRUST_STORE_PROPERTY, System.getProperty(TRUST_STORE_PROPERTY));

        System.clearProperty(TRUST_STORE_TYPE_PROPERTY);
        System.clearProperty(TRUST_STORE_PROPERTY);
    }
}
