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
