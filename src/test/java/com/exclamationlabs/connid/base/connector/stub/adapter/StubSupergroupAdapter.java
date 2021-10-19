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
import com.exclamationlabs.connid.base.connector.stub.model.StubSupergroup;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;

import java.util.HashSet;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.connector.stub.attribute.StubSupergroupAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

public class StubSupergroupAdapter extends BaseAdapter<StubSupergroup> {


    @Override
    public ObjectClass getType() {
        return new ObjectClass("SUPERGROUP");
    }

    @Override
    public Class<StubSupergroup> getIdentityModelClass() {
        return StubSupergroup.class;
    }

    @Override
    public Set<ConnectorAttribute> getConnectorAttributes() {
        Set<ConnectorAttribute> result = new HashSet<>();
        result.add(new ConnectorAttribute(SUPERGROUP_ID.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(SUPERGROUP_NAME.name(), STRING));
        return result;
    }

    @Override
    protected StubSupergroup constructModel(Set<Attribute> attributes, boolean isCreate) {
        StubSupergroup supergroup = new StubSupergroup();
        supergroup.setId(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));
        supergroup.setName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, SUPERGROUP_NAME));
        return supergroup;
    }

    @Override
    protected Set<Attribute> constructAttributes(StubSupergroup supergroup) {
        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(SUPERGROUP_ID.name(), supergroup.getId()));
        attributes.add(AttributeBuilder.build(SUPERGROUP_NAME.name(), supergroup.getName()));

        return new HashSet<>(attributes);
    }
}
