package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.Connector;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.DefaultGroup;
import com.exclamationlabs.connid.base.connector.model.DefaultUser;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.attribute.DefaultUserAttribute.*;

public class DefaultUsersAdapter implements UsersAdapter<DefaultUser, DefaultUser, DefaultGroup>{

    protected Driver<DefaultUser, DefaultGroup> driver;
    protected Connector<DefaultUser, DefaultUser, DefaultGroup> connector;

    public DefaultUsersAdapter(Connector<DefaultUser, DefaultUser, DefaultGroup> input) {
        connector = input;
        driver = input.getDriver();
    }

    @Override
    public Connector<DefaultUser, DefaultUser, DefaultGroup> getConnector() {
        return connector;
    }

    @Override
    public DefaultUser constructModel(Set<Attribute> attributes, boolean creation) {
        DefaultUser user = new DefaultUser();
        user.setId(getSingleAttributeValue(attributes, USER_ID).toString());
        user.setUserName(getSingleAttributeValue(attributes, USER_NAME).toString());
        user.setEmail(getSingleAttributeValue(attributes, EMAIL).toString());
        return user;
    }

    @Override
    public ConnectorObject constructConnectorObject(DefaultUser user) {
        return getConnectorObjectBuilder()
                .setUid(user.getAccessManagementIdValue())
                .setName(user.getAccessManagementNameValue())
                .addAttribute(AttributeBuilder.build(USER_ID.name(), user.getId()))
                .addAttribute(AttributeBuilder.build(USER_NAME.name(), user.getUserName()))
                .addAttribute(AttributeBuilder.build(EMAIL.name(), user.getEmail()))
                .build();
    }

    @Override
    public Driver<DefaultUser, DefaultGroup> getDriver() {
        return driver;
    }

    @Override
    public boolean groupAdditionControlledByUpdate() {
        return true;
    }

    @Override
    public boolean groupRemovalControlledByUpdate() {
        return true;
    }
}
