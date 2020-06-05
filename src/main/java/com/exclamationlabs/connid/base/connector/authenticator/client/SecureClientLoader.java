package com.exclamationlabs.connid.base.connector.authenticator.client;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.apache.http.client.HttpClient;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.security.KeyStore;
import java.util.Set;

public interface SecureClientLoader {
    Set<ConnectorProperty> getRequiredPropertyNames();

    HttpClient load(BaseConnectorConfiguration configuration, KeyStore keyStore) throws ConnectorSecurityException;
}
