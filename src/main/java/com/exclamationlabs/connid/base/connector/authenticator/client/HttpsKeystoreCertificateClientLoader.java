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

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import javax.net.ssl.*;
import java.security.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.CONNECTOR_BASE_AUTH_PFX_PASSWORD;

public class HttpsKeystoreCertificateClientLoader implements SecureClientLoader {
    private static final Set<ConnectorProperty> PROPERTY_NAMES;

    static {
        PROPERTY_NAMES = new HashSet<>(Collections.singletonList(
                CONNECTOR_BASE_AUTH_PFX_PASSWORD));
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public HttpClient load(BaseConnectorConfiguration configuration, KeyStore keyStore)
            throws ConnectorSecurityException {
        TrustManager trustManager = setupTrustManager();

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore,
                    configuration.getProperty(CONNECTOR_BASE_AUTH_PFX_PASSWORD).toCharArray());

            KeyManager[] managers = keyManagerFactory.getKeyManagers();
            sslContext.init(managers, new TrustManager[]{trustManager}, null);

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);

            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory> create()
                            .register("https", socketFactory)
                            .register("http", new PlainConnectionSocketFactory())
                            .build();

            BasicHttpClientConnectionManager connectionManager =
                    new BasicHttpClientConnectionManager(socketFactoryRegistry);
            return HttpClients.custom().setSSLSocketFactory(socketFactory)
                    .setConnectionManager(connectionManager).build();
        } catch(NoSuchAlgorithmException | KeyStoreException |
                KeyManagementException | UnrecoverableKeyException kse) {
            throw new ConnectorSecurityException(kse.getClass().getSimpleName() +
                    " while preparing secure client based on keystore: " +
                    kse.getMessage(), kse);
        }

    }

    private static TrustManager setupTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
    }
}
