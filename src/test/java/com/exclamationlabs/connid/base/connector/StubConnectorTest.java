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

import com.exclamationlabs.connid.base.connector.configuration.ConfigurationEnvironment;
import com.exclamationlabs.connid.base.connector.configuration.ConfigurationNameBuilder;
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.stub.StubConnector;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubGroupAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.driver.StubDriver;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import com.exclamationlabs.connid.base.connector.test.util.ConnectorTestUtils;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

public class StubConnectorTest {

    private BaseFullAccessConnector connector;
    private StubDriver driver;
    private OperationOptions testOperationOptions;

    @Before
    public void setup() {
        connector = new StubConnector();
        StubConfiguration configuration = new StubConfiguration(
            new ConfigurationNameBuilder()
                    .withEnvironment(ConfigurationEnvironment.DEVELOPMENT)
                    .withOwner("Test")
                    .withConnector("Stub").build()
        );
        configuration.setTestConfiguration();
        connector.init(configuration);
        driver = (StubDriver) connector.getDriver();
        testOperationOptions = new OperationOptionsBuilder().build();
    }

    @Test
    public void testTest() {
        connector.test();
        assertEquals("test", driver.getMethodInvoked());
    }

    @Test
    public void testIdentityToString() {
        StubUser test = new StubUser();
        test.setId("id");
        test.setUserName("name");
        assertEquals("id;name", test.identityToString());
    }

    @Test
    public void testAccessMethods() {
        assertTrue(connector.isCrud());
        assertTrue(connector.readEnabled());
        assertTrue(connector.createEnabled());
        assertTrue(connector.updateEnabled());
        assertTrue(connector.deleteEnabled());

        assertFalse(connector.isReadOnly());
        assertFalse(connector.isWriteOnly());
    }

    @Test
    public void testUserCreateNoGroups() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, testOperationOptions);
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("user create", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof StubUser);
        IdentityModel userParameter = (IdentityModel) driver.getMethodParameter1();
        assertNull(userParameter.getIdentityIdValue());
        assertNotNull(userParameter.getIdentityNameValue());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testUserCreateWithGroups() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.GROUP_IDS.name()).addValue(
                Arrays.asList("id1", "id2")).build());

        Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, testOperationOptions);
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("user create with group ids", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof StubUser);
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testUserCreateBadDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue(new BigDecimal(5)).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        connector.create(ObjectClass.ACCOUNT, attributes, testOperationOptions);
        assertTrue(driver.isInitializeInvoked());
    }

    @Test
    public void testUserModifyNoGroups() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        Uid newId = connector.update(ObjectClass.ACCOUNT,new Uid("1234"), attributes, testOperationOptions);
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("user update", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof String);
        assertEquals("1234", driver.getMethodParameter1().toString());

        assertNotNull(driver.getMethodParameter2());
        assertTrue(driver.getMethodParameter2() instanceof StubUser);
        IdentityModel userParameter = (IdentityModel) driver.getMethodParameter2();
        assertNull(userParameter.getIdentityIdValue());
        assertNotNull(userParameter.getIdentityNameValue());
    }

    @Test
    public void testUserModifyWithGroups() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.GROUP_IDS.name()).addValue(
                Arrays.asList("id1", "id2")).build());

        Uid newId = connector.update(ObjectClass.ACCOUNT, new Uid("1234"), attributes, testOperationOptions);
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("user update with group ids", driver.getMethodInvoked());
        assertEquals("1234", driver.getMethodParameter1().toString());
        assertNotNull(driver.getMethodParameter2());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testUserModifyDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue(new BigDecimal(5)).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        connector.update(ObjectClass.ACCOUNT, new Uid("1234"), attributes, testOperationOptions);
        assertTrue(driver.isInitializeInvoked());
        assertEquals("user update", driver.getMethodInvoked());
        assertEquals("1234", driver.getMethodParameter1().toString());
    }

    @Test
    public void testUserDelete() {
        connector.delete(ObjectClass.ACCOUNT, new Uid("1234"), testOperationOptions);
        assertTrue(driver.isInitializeInvoked());
        assertEquals("user delete", driver.getMethodInvoked());
        assertEquals("1234", driver.getMethodParameter1());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testUsersGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.ACCOUNT,"", resultsHandler, testOperationOptions);
        assertEquals(new StubDriver().getAll(StubUser.class, Collections.emptyMap()).size(), idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
        assertTrue(driver.isInitializeInvoked());
        assertEquals("user getAll", driver.getMethodInvoked());
        assertNull(driver.getMethodParameter1());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testUserGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.ACCOUNT, "1234", resultsHandler, testOperationOptions);
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
        assertTrue(driver.isInitializeInvoked());
        assertEquals("user getOne", driver.getMethodInvoked());
        assertEquals("1234", driver.getMethodParameter1().toString());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testGroupCreate() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue("Avengers").build());

        Uid newId = connector.create(ObjectClass.GROUP, attributes, testOperationOptions);
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("group create", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof StubGroup);
        IdentityModel groupParameter = (IdentityModel) driver.getMethodParameter1();
        assertNull(groupParameter.getIdentityIdValue());
        assertNotNull(groupParameter.getIdentityNameValue());
        assertNull(driver.getMethodParameter2());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testGroupCreateBadDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue(new BigDecimal(5)).build());

        connector.create(ObjectClass.GROUP, attributes, testOperationOptions);
    }

    @Test
    public void testGroupModify() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue("Avengers").build());

        Uid newId = connector.update(ObjectClass.GROUP, new Uid("1234"), attributes, testOperationOptions);
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("group update", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertEquals("1234", driver.getMethodParameter1().toString());

        assertNotNull(driver.getMethodParameter2());
        assertTrue(driver.getMethodParameter2() instanceof StubGroup);
        IdentityModel groupParameter = (IdentityModel) driver.getMethodParameter2();
        assertNull(groupParameter.getIdentityIdValue());
        assertNotNull(groupParameter.getIdentityNameValue());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testGroupModifyDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue(new BigDecimal(5)).build());

        connector.update(ObjectClass.GROUP, new Uid("1234"), attributes, testOperationOptions);
    }

    @Test
    public void testGroupDelete() {
        connector.delete(ObjectClass.GROUP, new Uid("1234"), testOperationOptions);
        assertTrue(driver.isInitializeInvoked());
        assertEquals("group delete", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertEquals("1234", driver.getMethodParameter1().toString());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testGroupsGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.GROUP, "", resultsHandler, testOperationOptions);
        assertEquals(new StubDriver().getAll(StubGroup.class, Collections.emptyMap()).size(), idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
        assertTrue(driver.isInitializeInvoked());
        assertEquals("group getAll", driver.getMethodInvoked());
        assertNull(driver.getMethodParameter1());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testGroupGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.GROUP, "1234", resultsHandler, testOperationOptions);
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
        assertTrue(driver.isInitializeInvoked());
        assertEquals("group getOne", driver.getMethodInvoked());
        assertEquals("1234", driver.getMethodParameter1().toString());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testConstruction() {
        executeTestConstruction(connector, "StubConnector");
    }

    @Test
    public void testDummyAuthentication() {
        assertEquals("NA", connector.getAuthenticator().authenticate(connector.getConnectorConfiguration()));
    }

    @Test
    public void testSchema() {
        executeTestSchema(connector);
    }

    static void executeTestConstruction(final BaseConnector testConnector,
                                 final String expectedConnectorName) {
        assertNotNull(testConnector.getConnectorFilterTranslator(ObjectClass.ACCOUNT));
        assertTrue(testConnector.getConnectorFilterTranslator(ObjectClass.ACCOUNT) instanceof DefaultFilterTranslator);

        assertNotNull(testConnector.getAdapter(ObjectClass.ACCOUNT).getConnectorAttributes());
        assertNotNull(testConnector.getAdapter(ObjectClass.GROUP).getConnectorAttributes());

        assertNotNull(testConnector.getConnectorSchemaBuilder());
        assertNotNull(testConnector.getAuthenticator());
        assertNotNull(testConnector.getConnectorConfiguration());
        assertTrue(testConnector.getConnectorConfiguration().isValidated());

        assertNotNull(testConnector.getConnectorConfiguration().getRequiredPropertyNames());
        assertEquals(1,
                testConnector.getConnectorConfiguration().getRequiredPropertyNames().size());

        assertEquals(expectedConnectorName, testConnector.getName());
        assertEquals("StubConfiguration", testConnector.getConnectorConfiguration().getName());

    }

    static void executeTestSchema(final BaseConnector testConnector) {
        Schema schemaResult = testConnector.schema();
        assertNotNull(schemaResult);

        ObjectClassInfo userSchema = schemaResult.findObjectClassInfo(ObjectClass.ACCOUNT_NAME);
        assertNotNull(userSchema);
        assertNotNull(userSchema.getAttributeInfo());
        assertEquals(19, userSchema.getAttributeInfo().size());

        ObjectClassInfo groupSchema = schemaResult.findObjectClassInfo(ObjectClass.GROUP_NAME);
        assertNotNull(groupSchema);
        assertNotNull(groupSchema.getAttributeInfo());
        assertEquals(3, groupSchema.getAttributeInfo().size());

        Set<OperationOptionInfo> info = schemaResult.getOperationOptionInfo();
        assertNotNull(info);
        assertEquals(7, info.size());
    }
}
