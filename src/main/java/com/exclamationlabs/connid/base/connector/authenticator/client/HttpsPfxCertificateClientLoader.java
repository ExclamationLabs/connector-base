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

import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.PfxConfiguration;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

/** Workhorse to create a secure HttpClient using a supplied PFX file. */
public class HttpsPfxCertificateClientLoader implements SecureClientLoader<PfxConfiguration> {

  @Override
  public HttpClient load(PfxConfiguration configuration, KeyStore keyStore)
      throws ConnectorSecurityException {
    TrustManager trustManager = setupTrustManager();

    try {
      SSLContext sslContext = SSLContext.getInstance(configuration.getTlsVersion());

      KeyManagerFactory keyManagerFactory =
          KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

      GuardedString keyPassword =
          configuration.getKeyPassword() != null
              ? configuration.getKeyPassword()
              : configuration.getPfxPassword();

      // Use original keystore if no alias specified
      KeyStore storeToUse = keyStore;

      // Only filter by alias if one is specified
      String alias = configuration.getKeyAlias();
      if (alias != null && !alias.isEmpty()) {
        if (!keyStore.containsAlias(alias)) {
          throw new ConnectorSecurityException("Specified keystore alias not found: " + alias);
        }

        // Create filtered keystore with only the specified alias
        storeToUse = KeyStore.getInstance(keyStore.getType());
        storeToUse.load(null, null);
        storeToUse.setKeyEntry(
            alias,
            keyStore.getKey(alias, GuardedStringUtil.read(keyPassword).toCharArray()),
            GuardedStringUtil.read(keyPassword).toCharArray(),
            keyStore.getCertificateChain(alias));
      }

      keyManagerFactory.init(storeToUse, GuardedStringUtil.read(keyPassword).toCharArray());

      KeyManager[] managers = keyManagerFactory.getKeyManagers();
      sslContext.init(managers, new TrustManager[] {trustManager}, null);

      SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);

      Registry<ConnectionSocketFactory> socketFactoryRegistry =
          RegistryBuilder.<ConnectionSocketFactory>create()
              .register("https", socketFactory)
              .register("http", new PlainConnectionSocketFactory())
              .build();

      BasicHttpClientConnectionManager connectionManager =
          new BasicHttpClientConnectionManager(socketFactoryRegistry);
      return HttpClients.custom()
          .setSSLSocketFactory(socketFactory)
          .setConnectionManager(connectionManager)
          .build();
    } catch (NoSuchAlgorithmException
        | KeyStoreException
        | KeyManagementException
        | UnrecoverableKeyException
        | IOException
        | CertificateException kse) {
      throw new ConnectorSecurityException(
          kse.getClass().getSimpleName()
              + " while preparing secure client based on keystore: "
              + kse.getMessage(),
          kse);
    }
  }

  private static TrustManager setupTrustManager() {
    return new X509TrustManager() {
      private X509Certificate[] acceptedChain = null;

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType) {
        checkCertificateChain(chain);
      }

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType) {
        checkCertificateChain(chain);
      }

      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return acceptedChain;
      }

      private void checkCertificateChain(java.security.cert.X509Certificate[] chain) {
        try {
          if (chain == null || chain.length == 0) {
            throw new IllegalArgumentException("Received empty/null certificate chain");
          }
          chain[0].checkValidity();
          acceptedChain = chain;
        } catch (Exception e) {
          throw new RuntimeException("Certificate not valid or trusted.", e);
        }
      }
    };
  }
}
