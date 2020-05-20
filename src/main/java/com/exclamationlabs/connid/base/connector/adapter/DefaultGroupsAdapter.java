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

import static com.exclamationlabs.connid.base.connector.attribute.DefaultGroupAttribute.*;

public class DefaultGroupsAdapter implements GroupsAdapter<DefaultUser, DefaultGroup>{

    protected Driver<DefaultUser, DefaultGroup> driver;
    protected Connector<DefaultUser, DefaultGroup> connector;

    public DefaultGroupsAdapter(Connector<DefaultUser, DefaultGroup> input) {
        connector = input;
        driver = input.getDriver();
    }

    @Override
    public Connector<DefaultUser, DefaultGroup> getConnector() {
        return connector;
    }

    @Override
    public DefaultGroup constructModel(Set<Attribute> attributes, boolean creation) {
        DefaultGroup group = new DefaultGroup();
        group.setId(getSingleAttributeValue(String.class, attributes, GROUP_ID));
        group.setName(getSingleAttributeValue(String.class, attributes, GROUP_NAME));
        return group;
    }

    @Override
    public ConnectorObject constructConnectorObject(DefaultGroup group) {
        return getConnectorObjectBuilder()
                .setUid(group.getIdentityIdValue())
                .setName(group.getIdentityNameValue())
                .addAttribute(AttributeBuilder.build(GROUP_ID.name(), group.getId()))
                .addAttribute(AttributeBuilder.build(GROUP_NAME.name(), group.getName()))
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
