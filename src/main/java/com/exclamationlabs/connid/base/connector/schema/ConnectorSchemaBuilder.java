package com.exclamationlabs.connid.base.connector.schema;

import com.exclamationlabs.connid.base.connector.Connector;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.Schema;

public interface ConnectorSchemaBuilder {

    /**
     * Build and define the schema capturing all the Attribute definitions for user
     * and group types for this connector.
     * @return Schema object needed for ConnId identity management system (normally Midpoint)
     * @throws ConfigurationException if exception or failure occurred while trying
     * to read or construct the schema.
     */
    Schema build(Connector connector) throws ConfigurationException;
}
