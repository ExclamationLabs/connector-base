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

package com.exclamationlabs.connid.base.connector.authenticator.client;

import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.PemPrivateKeyConfiguration;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import java.security.*;
import javax.net.ssl.*;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

/**
 * Workhorse to create a secure HttpClient using a supplied in-memory KeyStore created from PEM and
 * Private Key.
 */
public class HttpsPemPrivateKeyClientLoader
    implements SecureClientLoader<PemPrivateKeyConfiguration> {

  @Override
  public HttpClient load(PemPrivateKeyConfiguration configuration, KeyStore keyStore)
      throws ConnectorSecurityException {

    try {
      SSLContext sslContext;
      sslContext =
          SSLContexts.custom()
              .loadKeyMaterial(
                  keyStore,
                  GuardedStringUtil.read(configuration.getKeyStorePassword()).toCharArray())
              .build();

      return HttpClients.custom().setSSLContext(sslContext).build();
    } catch (NoSuchAlgorithmException
        | KeyStoreException
        | KeyManagementException
        | UnrecoverableKeyException kse) {
      throw new ConnectorSecurityException(
          kse.getClass().getSimpleName()
              + " while preparing secure client based on keystore: "
              + kse.getMessage(),
          kse);
    }
  }
}
