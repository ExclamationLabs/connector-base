package com.exclamationlabs.connid.base.connector.adapter;

import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.Schema;

import java.util.Set;

public interface ConnectorSchemaBuilder {

    /**
     * Build and define the schema capturing all the Attribute definitions for user
     * and group types for this connector.
     * @return Schema object needed for ConnId identity management system (normally Midpoint)
     * @throws ConfigurationException if exception or failure occurred while trying
     * to read or construct the schema.
     */
    Schema build() throws ConfigurationException;

    Set<ConnectorAttribute> getGroupAttributes();

    Set<ConnectorAttribute> getUserAttributes();
}
