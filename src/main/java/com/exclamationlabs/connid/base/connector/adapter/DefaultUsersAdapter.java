/*
    Copyright 2020 Exclamation Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

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

public class DefaultUsersAdapter implements UsersAdapter<DefaultUser, DefaultGroup>{

    protected Driver<DefaultUser, DefaultGroup> driver;
    protected Connector<DefaultUser, DefaultGroup> connector;

    public DefaultUsersAdapter(Connector<DefaultUser, DefaultGroup> input) {
        connector = input;
        driver = input.getDriver();
    }

    @Override
    public Connector<DefaultUser, DefaultGroup> getConnector() {
        return connector;
    }

    @Override
    public DefaultUser constructModel(Set<Attribute> attributes, boolean creation) {
        DefaultUser user = new DefaultUser();
        user.setId(getSingleAttributeValue(String.class, attributes, USER_ID));
        user.setUserName(getSingleAttributeValue(String.class, attributes, USER_NAME));
        user.setEmail(getSingleAttributeValue(String.class, attributes, EMAIL));
        return user;
    }

    @Override
    public ConnectorObject constructConnectorObject(DefaultUser user) {
        return getConnectorObjectBuilder()
                .setUid(user.getIdentityIdValue())
                .setName(user.getIdentityNameValue())
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
