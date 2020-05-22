package com.exclamationlabs.connid.base.connector;

import com.exclamationlabs.connid.base.connector.adapter.Adapter;
import com.exclamationlabs.connid.base.connector.adapter.GroupsAdapter;
import com.exclamationlabs.connid.base.connector.adapter.UsersAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import com.exclamationlabs.connid.base.connector.schema.ConnectorSchemaBuilder;
import com.exclamationlabs.connid.base.connector.schema.DefaultConnectorSchemaBuilder;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.Configuration;

import java.util.EnumMap;

public abstract class BaseConnector<U extends UserIdentityModel, G extends GroupIdentityModel>
        implements Connector<U,G> {

    private static final Log LOG = Log.getLog(Connector.class);

    protected Driver<U,G> driver;
    protected ConnectorSchemaBuilder<U,G> schemaBuilder;
    protected UsersAdapter<U,G> usersAdapter;
    protected GroupsAdapter<U,G> groupsAdapter;
    protected Authenticator authenticator;
    protected ConnectorConfiguration configuration;

    protected EnumMap<?, ConnectorAttribute> userAttributes;
    protected EnumMap<?, ConnectorAttribute> groupAttributes;

    /**
     * Required for ConnId Connector interface
     */
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    protected void setConnectorSchemaBuilder(ConnectorSchemaBuilder<U,G> input) {
        schemaBuilder = input;
    }

    ConnectorSchemaBuilder<U,G> getConnectorSchemaBuilder() {
        if (schemaBuilder == null) {
            setConnectorSchemaBuilder(new DefaultConnectorSchemaBuilder<>());
        }
        return schemaBuilder;
    }

    public void setAuthenticator(Authenticator in) {
        authenticator = in;
    }

    Authenticator getAuthenticator() {
        return authenticator;
    }

    public void setConnectorConfiguration(ConnectorConfiguration in) {
        configuration = in;
    }

    ConnectorConfiguration getConnectorConfiguration() {
        return configuration;
    }

    public void setUserAttributes(EnumMap<?, ConnectorAttribute> input) {
        userAttributes = input;
    }

    EnumMap<?, ConnectorAttribute> getUserAttributes() {
        return userAttributes;
    }

    public void setGroupAttributes(EnumMap<?, ConnectorAttribute> input) {
        groupAttributes = input;
    }

    EnumMap<?, ConnectorAttribute> getGroupAttributes() {
        return groupAttributes;
    }

    protected void setDriver(Driver<U, G> input) {
        driver = input;
    }

    Driver<U, G> getDriver() {
        return driver;
    }

    protected void setUsersAdapter(UsersAdapter<U, G> input) {
        usersAdapter = input;
    }

    UsersAdapter<U, G> getUsersAdapter() {
        return usersAdapter;
    }

    protected void setGroupsAdapter(GroupsAdapter<U, G> input) {
        groupsAdapter = input;
    }

    GroupsAdapter<U, G> getGroupsAdapter() {
        return groupsAdapter;
    }

    public Adapter<U,G> getAdapter(ObjectClass objectClass) {
        if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
            return getUsersAdapter();
        } else if (objectClass.is(ObjectClass.GROUP_NAME)) {
            return getGroupsAdapter();
        } else {
            throw new ConnectorException("Adapter could not be resolved for connector " + getName() +
                    ", unsupported object class: " + objectClass);
        }
    }

    @Override
    public void initializeBaseConnector(Configuration inputConfiguration) {
        if (getConnectorConfiguration()==null) {
            LOG.info("Initializing Connector {0} ...", getName());

            if (inputConfiguration == null) {
                throw new ConfigurationException("Unable to find configuration for connector " + this.getName());
            }

            if (!(inputConfiguration instanceof BaseConnectorConfiguration)) {
                throw new ConfigurationException("Connector configuration does not use base framework " +
                        "for connector " + this.getName());
            }

            setConnectorConfiguration((ConnectorConfiguration) inputConfiguration);
        }

        LOG.info("Using configuration {0} for connector {1}", getConnectorConfiguration().getClass().getName(),
                this.getName());

        if (!getConnectorConfiguration().isValidated()) {
            getConnectorConfiguration().validate();
            LOG.info("Connector {0} successfully validated", this.getName());
        } else {
            LOG.info("Connector {0} validation bypassed, already validated", this.getName());
        }

        if (getAuthenticator() == null) {
            LOG.info("No authenticator found, using default no-op Authenticator for Connector {0}, already validated", getConnectorConfiguration().getName());
            setAuthenticator(authenticatorConfiguration -> "NA");
        } else {
            LOG.info("Using authenticator {0} for connector {1}", getAuthenticator().getClass().getName(),
                    this.getName());
        }
        getAuthenticator().authenticate(getConnectorConfiguration());
        LOG.info("Connector {0} successfully authenticated", this.getName());

        if (getDriver() == null) {
            throw new ConfigurationException("Driver not setup for connector " + this.getName());
        }
        getDriver().initialize(getConnectorConfiguration(), getAuthenticator());
        LOG.info("Connector {0} driver {1} successfully initialized", this.getName(), getDriver().getClass().getName());

        if (getUsersAdapter() == null) {
            throw new ConfigurationException("UsersAdapter not setup for connector " + this.getName());
        }
        getUsersAdapter().setDriver(getDriver());
        LOG.info("Connector {0} usersAdapter {1} successfully initialized", this.getName(), getUsersAdapter().getClass().getName());

        if (getGroupsAdapter() == null) {
            throw new ConfigurationException("GroupsAdapter not setup for connector " + this.getName());
        }
        getGroupsAdapter().setDriver(getDriver());

        LOG.info("Connector {0} groupsAdapter {1} successfully initialized", this.getName(), getGroupsAdapter().getClass().getName());

    }

    @Override
    public void test() {
        driver.test();
    }

    @Override
    public void dispose() {
        driver.close();
    }

    @Override
    public void init() {
        init(configuration);
    }

    @Override
    public Schema schema() {
        if (getConnectorSchemaBuilder() == null) {
            throw new ConfigurationException("SchemaBuilder not setup for this connector");
        }
        return getConnectorSchemaBuilder().build(this, userAttributes, groupAttributes);
    }
}
