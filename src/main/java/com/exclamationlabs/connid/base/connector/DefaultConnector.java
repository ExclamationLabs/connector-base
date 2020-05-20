package com.exclamationlabs.connid.base.connector;

import com.exclamationlabs.connid.base.connector.adapter.*;
import com.exclamationlabs.connid.base.connector.attribute.*;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.DefaultConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.driver.DefaultDriver;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.DefaultGroup;
import com.exclamationlabs.connid.base.connector.model.DefaultUser;
import com.exclamationlabs.connid.base.connector.schema.ConnectorSchemaBuilder;
import com.exclamationlabs.connid.base.connector.schema.DefaultConnectorSchemaBuilder;

import java.util.EnumMap;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.connector.attribute.DefaultUserAttribute.*;
import static com.exclamationlabs.connid.base.connector.attribute.DefaultGroupAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

public class DefaultConnector implements Connector<DefaultUser, DefaultGroup> {

    protected Driver<DefaultUser, DefaultGroup> driver;
    protected ConnectorSchemaBuilder<DefaultUser,DefaultGroup> schemaBuilder;
    protected Adapter<DefaultUser, DefaultGroup> usersAdapter;
    protected Adapter<DefaultUser, DefaultGroup> groupsAdapter;

    public DefaultConnector() {
        driver = new DefaultDriver();
        schemaBuilder = new DefaultConnectorSchemaBuilder<>();
        usersAdapter = new DefaultUsersAdapter(this);
        groupsAdapter = new DefaultGroupsAdapter(this);
    }

    @Override
    public EnumMap<?, ConnectorAttribute> getUserAttributes() {
        return new ConnectorAttributeMapBuilder<>(DefaultUserAttribute.class)
                .add(USER_ID, STRING, NOT_UPDATEABLE)
                .add(USER_NAME, STRING)
                .add(EMAIL, STRING)
                .build();
    }

    @Override
    public EnumMap<?, ConnectorAttribute> getGroupAttributes() {
        return new ConnectorAttributeMapBuilder<>(DefaultGroupAttribute.class)
                .add(GROUP_ID, STRING, NOT_UPDATEABLE)
                .add(GROUP_NAME, STRING)
                .build();
    }


    @Override
    public ConnectorSchemaBuilder<DefaultUser,DefaultGroup> getConnectorSchemaBuilder() {
        return schemaBuilder;
    }

    @Override
    public Adapter<DefaultUser, DefaultGroup> getUsersAdapter() {
        return usersAdapter;
    }

    @Override
    public Adapter<DefaultUser, DefaultGroup> getGroupsAdapter() {
        return groupsAdapter;
    }

    @Override
    public Driver<DefaultUser, DefaultGroup> getDriver() {
        return driver;
    }

    @Override
    public Authenticator getAuthenticator() {
        return configuration -> "NA";
    }

    @Override
    public ConnectorConfiguration getConnectorConfiguration() {
        return new DefaultConnectorConfiguration();
    }
}
