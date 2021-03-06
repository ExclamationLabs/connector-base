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
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.identityconnectors.framework.common.objects.*;

import java.util.*;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.*;
import static com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.MULTIVALUED;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

public class StubUsersAdapter extends BaseAdapter<StubUser> {

    @Override
    public ObjectClass getType() {
        return ObjectClass.ACCOUNT;
    }

    @Override
    public Class<StubUser> getIdentityModelClass() {
        return StubUser.class;
    }

    @Override
    public List<ConnectorAttribute> getConnectorAttributes() {
        List<ConnectorAttribute> result = new ArrayList<>();
        result.add(new ConnectorAttribute(USER_ID.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(USER_NAME.name(), STRING));
        result.add(new ConnectorAttribute(EMAIL.name(), STRING));
        result.add(new ConnectorAttribute(GROUP_IDS.name(), ASSIGNMENT_IDENTIFIER, MULTIVALUED));
        result.add(new ConnectorAttribute(CLUB_IDS.name(), ASSIGNMENT_IDENTIFIER, MULTIVALUED));

        result.add(new ConnectorAttribute(USER_TEST_BIG_DECIMAL.name(), BIG_DECIMAL));
        result.add(new ConnectorAttribute(USER_TEST_BIG_INTEGER.name(), BIG_INTEGER));
        result.add(new ConnectorAttribute(USER_TEST_BOOLEAN.name(), BOOLEAN));
        result.add(new ConnectorAttribute(USER_TEST_BYTE.name(), BYTE));
        result.add(new ConnectorAttribute(USER_TEST_CHARACTER.name(), CHARACTER));
        result.add(new ConnectorAttribute(USER_TEST_DOUBLE.name(), DOUBLE));
        result.add(new ConnectorAttribute(USER_TEST_FLOAT.name(), FLOAT));
        result.add(new ConnectorAttribute(USER_TEST_GUARDED_BYTE_ARRAY.name(), GUARDED_BYTE_ARRAY));
        result.add(new ConnectorAttribute(USER_TEST_GUARDED_STRING.name(), GUARDED_STRING));
        result.add(new ConnectorAttribute(USER_TEST_INTEGER.name(), INTEGER));
        result.add(new ConnectorAttribute(USER_TEST_LONG.name(), LONG));
        result.add(new ConnectorAttribute(USER_TEST_MAP.name(), MAP));
        result.add(new ConnectorAttribute(USER_TEST_ZONED_DATE_TIME.name(), ZONED_DATE_TIME));

        return result;
    }

    @Override
    protected StubUser constructModel(Set<Attribute> attributes, boolean isCreate) {
        StubUser user = new StubUser();
        user.setId(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));
        user.setUserName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, USER_NAME));
        user.setEmail(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, EMAIL));

        user.setGroupIds(readAssignments(attributes, GROUP_IDS));
        user.setClubIds(readAssignments(attributes, CLUB_IDS));

        return user;
    }

    @Override
    protected List<Attribute> constructAttributes(StubUser user) {
        List<Attribute> attributes = new ArrayList<>();

        attributes.add(AttributeBuilder.build(USER_ID.name(), user.getId()));
        attributes.add(AttributeBuilder.build(USER_NAME.name(), user.getUserName()));
        attributes.add(AttributeBuilder.build(EMAIL.name(), user.getEmail()));

        attributes.add(AttributeBuilder.build(GROUP_IDS.name(), user.getGroupIds()));
        attributes.add(AttributeBuilder.build(CLUB_IDS.name(), user.getClubIds()));

        attributes.add(AttributeBuilder.build(USER_TEST_BIG_DECIMAL.name(), user.getUserTestBigDecimal()));
        attributes.add(AttributeBuilder.build(USER_TEST_BIG_INTEGER.name(), user.getUserTestBigInteger()));
        attributes.add(AttributeBuilder.build(USER_TEST_BOOLEAN.name(), user.getUserTestBoolean()));
        attributes.add(AttributeBuilder.build(USER_TEST_BYTE.name(), user.getUserTestByte()));
        attributes.add(AttributeBuilder.build(USER_TEST_CHARACTER.name(), user.getUserTestCharacter()));
        attributes.add(AttributeBuilder.build(USER_TEST_DOUBLE.name(), user.getUserTestDouble()));
        attributes.add(AttributeBuilder.build(USER_TEST_FLOAT.name(), user.getUserTestFloat()));
        attributes.add(AttributeBuilder.build(USER_TEST_GUARDED_BYTE_ARRAY.name(), user.getUserTestGuardedByteArray()));
        attributes.add(AttributeBuilder.build(USER_TEST_GUARDED_STRING.name(), user.getUserTestGuardedString()));
        attributes.add(AttributeBuilder.build(USER_TEST_INTEGER.name(), user.getUserTestInteger()));
        attributes.add(AttributeBuilder.build(USER_TEST_LONG.name(), user.getUserTestLong()));
        attributes.add(AttributeBuilder.build(USER_TEST_MAP.name(), user.getUserTestMap()));
        attributes.add(AttributeBuilder.build(USER_TEST_ZONED_DATE_TIME.name(), user.getUserTestZonedDateTime()));

        return attributes;
    }

    public List<Attribute> constructAttributesTestAccess(StubUser user) {
        return constructAttributes(user);
    }
}
