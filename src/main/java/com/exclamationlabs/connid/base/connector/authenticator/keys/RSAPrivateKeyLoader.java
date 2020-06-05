package com.exclamationlabs.connid.base.connector.authenticator.keys;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.security.interfaces.RSAPrivateKey;
import java.util.Set;

public interface RSAPrivateKeyLoader {
    Set<ConnectorProperty> getRequiredPropertyNames();

    RSAPrivateKey load(BaseConnectorConfiguration configuration) throws ConnectorSecurityException;
}
