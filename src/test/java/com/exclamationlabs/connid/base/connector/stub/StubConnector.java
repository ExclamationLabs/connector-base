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

package com.exclamationlabs.connid.base.connector.stub;

import com.exclamationlabs.connid.base.connector.BaseConnector;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeMapBuilder;
import com.exclamationlabs.connid.base.connector.stub.adapter.StubGroupsAdapter;
import com.exclamationlabs.connid.base.connector.stub.adapter.StubUsersAdapter;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubGroupAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.driver.StubDriver;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.identityconnectors.framework.spi.ConnectorClass;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.connector.stub.attribute.StubGroupAttribute.GROUP_ID;
import static com.exclamationlabs.connid.base.connector.stub.attribute.StubGroupAttribute.GROUP_NAME;
import static com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

@ConnectorClass(displayNameKey = "test.display", configurationClass = StubConfiguration.class)
public class StubConnector extends BaseConnector<StubUser, StubGroup> {

    public StubConnector() {
        setDriver(new StubDriver());
        setUsersAdapter(new StubUsersAdapter());
        setGroupsAdapter(new StubGroupsAdapter());
        setUserAttributes( new ConnectorAttributeMapBuilder<>(StubUserAttribute.class)
                .add(USER_ID, STRING, NOT_UPDATEABLE)
                .add(USER_NAME, STRING)
                .add(EMAIL, STRING)
                .build());
        setGroupAttributes(new ConnectorAttributeMapBuilder<>(StubGroupAttribute.class)
                .add(GROUP_ID, STRING, NOT_UPDATEABLE)
                .add(GROUP_NAME, STRING)
                .build());
    }

}
