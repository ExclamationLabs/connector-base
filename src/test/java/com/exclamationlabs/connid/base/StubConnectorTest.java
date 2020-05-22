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

package com.exclamationlabs.connid.base;

import com.exclamationlabs.connid.base.connector.Connector;
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.stub.StubConnector;
import com.exclamationlabs.connid.base.stub.attribute.StubGroupAttribute;
import com.exclamationlabs.connid.base.stub.attribute.StubUserAttribute;
import com.exclamationlabs.connid.base.stub.driver.StubDriver;
import com.exclamationlabs.connid.base.stub.model.StubGroup;
import com.exclamationlabs.connid.base.stub.model.StubUser;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;

public class StubConnectorTest {

    private Connector<StubUser, StubGroup> connector;

    @Before
    public void setup() {
        connector = new StubConnector();
        connector.init();
    }

    @Test
    public void testUserCreate() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testUserCreateBadDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue(new BigDecimal(5)).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        connector.create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
    }

    @Test
    public void testUserModify() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        Uid newId = connector.update(ObjectClass.ACCOUNT, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testUserModifyDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue(new BigDecimal(5)).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        connector.update(ObjectClass.ACCOUNT, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
    }

    @Test
    public void testUserDelete() {
        connector.delete(ObjectClass.ACCOUNT, new Uid("1234"), new OperationOptionsBuilder().build());
    }

    @Test
    public void testUsersGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.ACCOUNT, "", resultsHandler, new OperationOptionsBuilder().build());
        assertEquals(new StubDriver().getUsers().size(), idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void testUserGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.ACCOUNT, "1234", resultsHandler, new OperationOptionsBuilder().build());
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void testGroupCreate() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue("Avengers").build());

        Uid newId = connector.create(ObjectClass.GROUP, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testGroupCreateBadDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue(new BigDecimal(5)).build());

        connector.create(ObjectClass.GROUP, attributes, new OperationOptionsBuilder().build());
    }

    @Test
    public void testGroupModify() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue("Avengers").build());

        Uid newId = connector.update(ObjectClass.GROUP, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testGroupModifyDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue(new BigDecimal(5)).build());

        connector.update(ObjectClass.GROUP, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
    }

    @Test
    public void testGroupDelete() {
        connector.delete(ObjectClass.GROUP, new Uid("1234"), new OperationOptionsBuilder().build());
    }

    @Test
    public void testGroupsGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.GROUP, "", resultsHandler, new OperationOptionsBuilder().build());
        assertEquals(new StubDriver().getGroups().size(), idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void testGroupGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.GROUP, "1234", resultsHandler, new OperationOptionsBuilder().build());
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
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

        assertEquals("StubConnector", connector.getName());
        assertEquals("StubConfiguration", connector.getConnectorConfiguration().getName());
    }

    @Test
    public void testDummyAuthentication() {
        assertEquals("NA", connector.getAuthenticator().authenticate(connector.getConnectorConfiguration()));

    }

    @Test
    public void testSchema() {
        Schema schemaResult = connector.schema();
        assertNotNull(schemaResult);

        ObjectClassInfo userSchema = schemaResult.findObjectClassInfo(ObjectClass.ACCOUNT_NAME);
        assertNotNull(userSchema);
        assertNotNull(userSchema.getAttributeInfo());
        assertEquals(4, userSchema.getAttributeInfo().size());

        ObjectClassInfo groupSchema = schemaResult.findObjectClassInfo(ObjectClass.GROUP_NAME);
        assertNotNull(groupSchema);
        assertNotNull(groupSchema.getAttributeInfo());
        assertEquals(3, groupSchema.getAttributeInfo().size());
    }

    private ResultsHandler buildResultsHandler(List<String> idValues, List<String> nameValues) {

        return (ConnectorObject connectorObject) -> {
            idValues.add(connectorObject.getUid().getUidValue());
            nameValues.add(connectorObject.getName().getNameValue());
            return true;
        };

    }
}
