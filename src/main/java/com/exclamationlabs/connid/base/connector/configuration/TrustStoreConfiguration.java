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

package com.exclamationlabs.connid.base.connector.configuration;

import com.exclamationlabs.connid.base.connector.logging.Logger;

/**
 * This utility class is used to clear out Java system properties for trust store setup. This seems
 * to be needed in current MidPoint versions where these default Java system property values cause
 * problems with any usage of HttpClient from a connector.
 */
public class TrustStoreConfiguration {
  static final String TRUST_STORE_TYPE_PROPERTY = "javax.net.ssl.trustStoreType";
  static final String TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";

  private static boolean propertiesCleared = false;

  private TrustStoreConfiguration() {}

  static void clearJdkPropertiesForce() {
    propertiesCleared = false;
    clearJdkProperties();
  }

  public static void clearJdkProperties() {
    if (!propertiesCleared) {
      Logger.debug(
          TrustStoreConfiguration.class,
          String.format(
              "Clearing out property %s for MidPoint integration.  Value was %s",
              TRUST_STORE_TYPE_PROPERTY, System.getProperty(TRUST_STORE_TYPE_PROPERTY)));
      Logger.debug(
          TrustStoreConfiguration.class,
          String.format(
              "Clearing out property %s for MidPoint integration.  Value was %s",
              TRUST_STORE_PROPERTY, System.getProperty(TRUST_STORE_PROPERTY)));

      System.clearProperty(TRUST_STORE_TYPE_PROPERTY);
      System.clearProperty(TRUST_STORE_PROPERTY);
      propertiesCleared = true;
    } else {
      Logger.debug(
          TrustStoreConfiguration.class, "Trust store properties already cleared for connector.");
    }
  }
}
