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
import com.exclamationlabs.connid.base.connector.stub.configuration.ComplexStubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.model.StubClub;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;

import java.util.HashSet;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.connector.stub.attribute.StubClubAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

public class StubClubAdapter extends BaseAdapter<StubClub, ComplexStubConfiguration> {


    @Override
    public ObjectClass getType() {
        return new ObjectClass("CLUB");
    }

    @Override
    public Class<StubClub> getIdentityModelClass() {
        return StubClub.class;
    }

    @Override
    public Set<ConnectorAttribute> getConnectorAttributes() {
        Set<ConnectorAttribute> result = new HashSet<>();
        result.add(new ConnectorAttribute(CLUB_ID.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(CLUB_NAME.name(), STRING));
        return result;
    }

    @Override
    protected StubClub constructModel(Set<Attribute> attributes, Set<Attribute> added,
                                      Set<Attribute> removed,
                                      boolean isCreate) {
        StubClub club = new StubClub();
        club.setId(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));
        club.setName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, CLUB_NAME));
        return club;
    }

    @Override
    protected Set<Attribute> constructAttributes(StubClub club) {
        Set<Attribute> attributes = new HashSet<>();

        attributes.add(AttributeBuilder.build(CLUB_ID.name(), club.getId()));
        attributes.add(AttributeBuilder.build(CLUB_NAME.name(), club.getName()));

        return attributes;
    }
}
