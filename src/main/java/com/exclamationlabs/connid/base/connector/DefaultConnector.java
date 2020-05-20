package com.exclamationlabs.connid.base.connector;

import com.exclamationlabs.connid.base.connector.adapter.*;
import com.exclamationlabs.connid.base.connector.attribute.*;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.DefaultConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.driver.DefaultDriver;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.model.DefaultGroup;
import com.exclamationlabs.connid.base.connector.model.DefaultUser;
import com.exclamationlabs.connid.base.connector.schema.ConnectorSchemaBuilder;
import com.exclamationlabs.connid.base.connector.schema.DefaultConnectorSchemaBuilder;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;

import java.util.EnumMap;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.connector.attribute.DefaultUserAttribute.*;
import static com.exclamationlabs.connid.base.connector.attribute.DefaultGroupAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

public class DefaultConnector implements Connector<DefaultUser, DefaultUser, DefaultGroup> {

    protected Driver<DefaultUser, DefaultGroup> driver;
    protected FilterTranslator<String> filterTranslator;
    protected ConnectorSchemaBuilder schemaBuilder;
    protected Adapter<DefaultUser, DefaultUser, DefaultGroup> usersAdapter;
    protected Adapter<DefaultUser, DefaultUser, DefaultGroup> groupsAdapter;
    protected Authenticator authenticator;
    protected ConnectorConfiguration configuration;

    public DefaultConnector() {
        driver = new DefaultDriver();
        filterTranslator = new DefaultFilterTranslator();
        schemaBuilder = new DefaultConnectorSchemaBuilder();
        usersAdapter = new DefaultUsersAdapter(this);
        //groupsAdapter = new DefaultGroupsAdapter();

        authenticator = configuration -> "NA";
        configuration = new DefaultConnectorConfiguration();
    }

    @Override
    public String getName() {
        return getClass().getName();
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
    public ConnectorSchemaBuilder getConnectorSchemaBuilder() {
        return schemaBuilder;
    }

    @Override
    public FilterTranslator<String> getConnectorFilterTranslator() {
        return filterTranslator;
    }

    @Override
    public Adapter<DefaultUser, DefaultUser, DefaultGroup> getUsersAdapter() {
        return usersAdapter;
    }

    @Override
    public Adapter<DefaultUser, DefaultUser, DefaultGroup> getGroupsAdapter() {
        return groupsAdapter;
    }

    @Override
    public Driver<DefaultUser, DefaultGroup> getDriver() {
        return driver;
    }

    @Override
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    @Override
    public ConnectorConfiguration getConnectorConfiguration() {
        return configuration;
    }
}
