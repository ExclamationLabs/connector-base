/*
    Copyright 2020 Exclamation Labs

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

package com.exclamationlabs.connid.base.connector.driver.rest;

import org.identityconnectors.common.logging.Log;

public class TrustStoreConfiguration {
    private static final String TRUST_STORE_TYPE_PROPERTY = "javax.net.ssl.trustStoreType";
    private static final String TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";

    private static final Log LOG = Log.getLog(TrustStoreConfiguration.class);

    private TrustStoreConfiguration() {}

    public static void clearJdkProperties() {

        LOG.info("Clearing out property {0} for MidPoint integration.  Value was {1}",
                TRUST_STORE_TYPE_PROPERTY, System.getProperty(TRUST_STORE_TYPE_PROPERTY));
        LOG.info("Clearing out property {0} for MidPoint integration.  Value was {1}",
                TRUST_STORE_PROPERTY, System.getProperty(TRUST_STORE_PROPERTY));

        System.clearProperty(TRUST_STORE_TYPE_PROPERTY);
        System.clearProperty(TRUST_STORE_PROPERTY);
    }
}
