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

package com.exclamationlabs.connid.base.connector.stub.adapter;

import com.exclamationlabs.connid.base.connector.adapter.AdapterValueTypeConverter;
import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import org.identityconnectors.framework.common.objects.*;

import java.util.HashSet;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.connector.stub.attribute.StubGroupAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

public class StubGroupsAdapter extends BaseAdapter<StubGroup> {


    @Override
    public ObjectClass getType() {
        return ObjectClass.GROUP;
    }

    @Override
    public Class<StubGroup> getIdentityModelClass() {
        return StubGroup.class;
    }

    @Override
    public Set<ConnectorAttribute> getConnectorAttributes() {
        Set<ConnectorAttribute> result = new HashSet<>();
        result.add(new ConnectorAttribute(GROUP_ID.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(GROUP_NAME.name(), STRING));
        return result;
    }

    @Override
    protected StubGroup constructModel(Set<Attribute> attributes, boolean isCreate) {
        StubGroup group = new StubGroup();
        group.setId(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));
        group.setName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, GROUP_NAME));
        group.setSupergroupIds(readAssignments(attributes, SUPERGROUP_IDS));
        return group;
    }

    @Override
    protected Set<Attribute> constructAttributes(StubGroup group) {
        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(GROUP_ID.name(), group.getId()));
        attributes.add(AttributeBuilder.build(GROUP_NAME.name(), group.getName()));

        return attributes;
    }
}
