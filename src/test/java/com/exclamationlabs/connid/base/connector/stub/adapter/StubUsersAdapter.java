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
import com.exclamationlabs.connid.base.connector.adapter.BaseUsersAdapter;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.OperationOptions;

import java.util.Set;

import static com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute.*;

public class StubUsersAdapter extends BaseUsersAdapter<StubUser, StubGroup> {

    @Override
    public StubUser constructUser(Set<Attribute> attributes, boolean creation) {
        StubUser user = new StubUser();
        user.setId(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));
        user.setUserName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, USER_NAME));
        user.setEmail(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, EMAIL));
        return user;
    }

    @Override
    public ConnectorObject constructConnectorObject(StubUser user, OperationOptions options) {
        return getConnectorObjectBuilder(user)
                .addAttribute(AttributeBuilder.build(USER_ID.name(), user.getId()))
                .addAttribute(AttributeBuilder.build(USER_NAME.name(), user.getUserName()))
                .addAttribute(AttributeBuilder.build(EMAIL.name(), user.getEmail()))
                .build();
    }

}
