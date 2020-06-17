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

package com.exclamationlabs.connid.base.connector;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import com.exclamationlabs.connid.base.connector.stub.ComplexStubConnector;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubGroupAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute;
import com.exclamationlabs.connid.base.connector.stub.configuration.ComplexStubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.driver.StubDriver;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.identityconnectors.framework.common.objects.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ComplexStubConnectorTest {

    private BaseConnector<StubUser, StubGroup> connector;
    private StubDriver driver;

    @Before
    public void setup() {
        connector = new ComplexStubConnector();
        connector.init(new ComplexStubConfiguration());
        driver = (StubDriver) connector.getDriver();
    }

    @Test
    public void testUserCreateWithGroups() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.GROUP_IDS.name()).addValue(
                Arrays.asList("id1", "id2")).build());

        Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("createUser", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof StubUser);
        UserIdentityModel userParameter = (UserIdentityModel) driver.getMethodParameter1();
        assertNull(userParameter.getIdentityIdValue());
        assertNotNull(userParameter.getIdentityNameValue());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testUserModifyWithGroups() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.GROUP_IDS.name()).addValue(
                Arrays.asList("id1", "id2")).build());

        Uid newId = connector.update(ObjectClass.ACCOUNT, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("updateUser", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof String);
        assertEquals("1234", driver.getMethodParameter1().toString());

        assertNotNull(driver.getMethodParameter2());
        assertTrue(driver.getMethodParameter2() instanceof StubUser);
        UserIdentityModel userParameter = (UserIdentityModel) driver.getMethodParameter2();
        assertNull(userParameter.getIdentityIdValue());
        assertNotNull(userParameter.getIdentityNameValue());
    }

    @Test
    public void testConstruction() {
        assertNotNull(connector.getConnectorFilterTranslator());
        assertTrue(connector.getConnectorFilterTranslator() instanceof DefaultFilterTranslator);

        assertTrue(connector.getGroupAttributes().containsKey(StubGroupAttribute.GROUP_ID));
        assertTrue(connector.getUserAttributes().containsKey(StubUserAttribute.USER_ID));

        assertNotNull(connector.getConnectorSchemaBuilder());
        assertNotNull(connector.getAuthenticator());
        assertNotNull(connector.getConnectorConfiguration());
        assertTrue(connector.getConnectorConfiguration().isValidated());

        assertNotNull(connector.getConnectorConfiguration().getRequiredPropertyNames());
        assertEquals(7,
                connector.getConnectorConfiguration().getRequiredPropertyNames().size());
        assertTrue(connector.getConnectorConfiguration().getRequiredPropertyNames()
                .contains(ConnectorProperty.CONNECTOR_BASE_CONFIGURATION_ACTIVE));
        assertTrue(connector.getConnectorConfiguration().getRequiredPropertyNames()
                .contains(ConnectorProperty.CONNECTOR_BASE_AUTH_PFX_FILE));
        assertTrue(connector.getConnectorConfiguration().getRequiredPropertyNames()
                .contains(ConnectorProperty.CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL));
        assertTrue(connector.getConnectorConfiguration().getRequiredPropertyNames()
                .contains(ConnectorProperty.CONNECTOR_BASE_AUTH_JKS_PASSWORD));
        assertTrue(connector.getConnectorConfiguration().getRequiredPropertyNames()
                .contains(ConnectorProperty.CONNECTOR_BASE_AUTH_JKS_ALIAS));
        assertTrue(connector.getConnectorConfiguration().getRequiredPropertyNames()
                .contains(ConnectorProperty.CONNECTOR_BASE_AUTH_PFX_PASSWORD));
        assertTrue(connector.getConnectorConfiguration().getRequiredPropertyNames()
                .contains(ConnectorProperty.CONNECTOR_BASE_AUTH_JKS_FILE));

        assertEquals("ComplexStubConnector", connector.getName());
        assertEquals("ComplexStubConfiguration", connector.getConnectorConfiguration().getName());
    }

    @Test
    public void testAuthentication() {
        assertEquals("ying-yang", connector.getAuthenticator().authenticate(connector.getConnectorConfiguration()));
    }
}
