package com.exclamationlabs.connid.base.connector.authenticator.keys;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.security.KeyStore;
import java.util.Set;

public interface KeyStoreLoader {
    Set<ConnectorProperty> getRequiredPropertyNames();
    KeyStore load(BaseConnectorConfiguration configuration) throws ConnectorSecurityException;
}
