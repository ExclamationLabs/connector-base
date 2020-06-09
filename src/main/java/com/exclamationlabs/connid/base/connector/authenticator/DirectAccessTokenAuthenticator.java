package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

public class DirectAccessTokenAuthenticator implements Authenticator {

    private static final Set<ConnectorProperty> PROPERTY_NAMES;
    static {
        PROPERTY_NAMES = new HashSet<>(Collections.singletonList(
                CONNECTOR_BASE_AUTH_DIRECT_TOKEN));
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public String authenticate(BaseConnectorConfiguration configuration) throws ConnectorSecurityException {
        return configuration.getProperty(CONNECTOR_BASE_AUTH_DIRECT_TOKEN);
    }
}
