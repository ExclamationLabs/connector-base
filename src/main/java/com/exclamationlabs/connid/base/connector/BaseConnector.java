package com.exclamationlabs.connid.base.connector;

import com.exclamationlabs.connid.base.connector.adapter.GroupsAdapter;
import com.exclamationlabs.connid.base.connector.adapter.UsersAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import com.exclamationlabs.connid.base.connector.schema.ConnectorSchemaBuilder;
import com.exclamationlabs.connid.base.connector.schema.DefaultConnectorSchemaBuilder;

import java.util.EnumMap;

public abstract class BaseConnector<U extends UserIdentityModel, G extends GroupIdentityModel>
        implements Connector<U,G> {

    protected Driver<U,G> driver;
    protected ConnectorSchemaBuilder<U,G> schemaBuilder;
    protected UsersAdapter<U,G> usersAdapter;
    protected GroupsAdapter<U,G> groupsAdapter;
    protected Authenticator authenticator;
    protected ConnectorConfiguration configuration;

    protected EnumMap<?, ConnectorAttribute> userAttributes;
    protected EnumMap<?, ConnectorAttribute> groupAttributes;

    @Override
    public ConnectorSchemaBuilder<U,G> getConnectorSchemaBuilder() {
        if (schemaBuilder == null) {
            setConnectorSchemaBuilder(new DefaultConnectorSchemaBuilder<>());
        }
        return schemaBuilder;
    }

    @Override
    public void setConnectorSchemaBuilder(ConnectorSchemaBuilder<U,G> input) {
        schemaBuilder = input;
    }

    @Override
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    @Override
    public ConnectorConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Driver<U,G> getDriver() {
        return driver;
    }

    @Override
    public UsersAdapter<U,G> getUsersAdapter() {
        return usersAdapter;
    }

    @Override
    public GroupsAdapter<U,G> getGroupsAdapter() {
        return groupsAdapter;
    }

    public void setAuthenticator(Authenticator in) {
        authenticator = in;
    }

    public void setConnectorConfiguration(ConnectorConfiguration in) {
        configuration = in;
    }

    @Override
    public ConnectorConfiguration getConnectorConfiguration() {
        return configuration;
    }

    public void setUserAttributes(EnumMap<?, ConnectorAttribute> input) {
        userAttributes = input;
    }

    public void setGroupAttributes(EnumMap<?, ConnectorAttribute> input) {
        groupAttributes = input;
    }

    @Override
    public EnumMap<?, ConnectorAttribute> getUserAttributes() {
        return userAttributes;
    }

    @Override
    public EnumMap<?, ConnectorAttribute> getGroupAttributes() {
        return groupAttributes;
    }

    protected void setDriver(Driver<U, G> input) {
        driver = input;
    }

    protected void setUsersAdapter(UsersAdapter<U, G> input) {
        usersAdapter = input;
    }

    protected void setGroupsAdapter(GroupsAdapter<U, G> input) {
        groupsAdapter = input;
    }
}
