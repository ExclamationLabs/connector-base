package com.exclamationlabs.connid.base.stub;

import com.exclamationlabs.connid.base.connector.Connector;
import com.exclamationlabs.connid.base.connector.adapter.*;
import com.exclamationlabs.connid.base.connector.attribute.*;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.stub.configuration.StubConnectorConfiguration;
import com.exclamationlabs.connid.base.stub.driver.StubDriver;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.stub.model.StubGroup;
import com.exclamationlabs.connid.base.stub.model.StubUser;
import com.exclamationlabs.connid.base.connector.schema.ConnectorSchemaBuilder;
import com.exclamationlabs.connid.base.connector.schema.DefaultConnectorSchemaBuilder;
import com.exclamationlabs.connid.base.stub.adapter.StubGroupsAdapter;
import com.exclamationlabs.connid.base.stub.adapter.StubUsersAdapter;
import com.exclamationlabs.connid.base.stub.attribute.StubGroupAttribute;
import com.exclamationlabs.connid.base.stub.attribute.StubUserAttribute;

import java.util.EnumMap;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.stub.attribute.StubUserAttribute.*;
import static com.exclamationlabs.connid.base.stub.attribute.StubGroupAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

public class StubConnector implements Connector<StubUser, StubGroup> {

    protected Driver<StubUser, StubGroup> driver;
    protected ConnectorSchemaBuilder<StubUser, StubGroup> schemaBuilder;
    protected Adapter<StubUser, StubGroup> usersAdapter;
    protected Adapter<StubUser, StubGroup> groupsAdapter;

    public StubConnector() {
        driver = new StubDriver();
        schemaBuilder = new DefaultConnectorSchemaBuilder<>();
        usersAdapter = new StubUsersAdapter(this);
        groupsAdapter = new StubGroupsAdapter(this);
    }

    @Override
    public EnumMap<?, ConnectorAttribute> getUserAttributes() {
        return new ConnectorAttributeMapBuilder<>(StubUserAttribute.class)
                .add(USER_ID, STRING, NOT_UPDATEABLE)
                .add(USER_NAME, STRING)
                .add(EMAIL, STRING)
                .build();
    }

    @Override
    public EnumMap<?, ConnectorAttribute> getGroupAttributes() {
        return new ConnectorAttributeMapBuilder<>(StubGroupAttribute.class)
                .add(GROUP_ID, STRING, NOT_UPDATEABLE)
                .add(GROUP_NAME, STRING)
                .build();
    }


    @Override
    public ConnectorSchemaBuilder<StubUser, StubGroup> getConnectorSchemaBuilder() {
        return schemaBuilder;
    }

    @Override
    public Adapter<StubUser, StubGroup> getUsersAdapter() {
        return usersAdapter;
    }

    @Override
    public Adapter<StubUser, StubGroup> getGroupsAdapter() {
        return groupsAdapter;
    }

    @Override
    public Driver<StubUser, StubGroup> getDriver() {
        return driver;
    }

    @Override
    public Authenticator getAuthenticator() {
        return configuration -> "NA";
    }

    @Override
    public ConnectorConfiguration getConnectorConfiguration() {
        return new StubConnectorConfiguration();
    }
}
