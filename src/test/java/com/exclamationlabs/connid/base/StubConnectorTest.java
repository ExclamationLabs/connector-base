package com.exclamationlabs.connid.base;

import com.exclamationlabs.connid.base.stub.StubConnector;
import com.exclamationlabs.connid.base.stub.attribute.StubGroupAttribute;
import com.exclamationlabs.connid.base.stub.attribute.StubUserAttribute;
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

    private StubConnector connector;

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

        Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, (new OperationOptionsBuilder()).build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testUserCreateBadDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue(new BigDecimal(5)).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        connector.create(ObjectClass.ACCOUNT, attributes, (new OperationOptionsBuilder()).build());
    }

    @Test
    public void testUserModify() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        Uid newId = connector.update(ObjectClass.ACCOUNT, new Uid("1234"), attributes, (new OperationOptionsBuilder()).build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testUserModifyDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue(new BigDecimal(5)).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        connector.update(ObjectClass.ACCOUNT, new Uid("1234"), attributes, (new OperationOptionsBuilder()).build());
    }

    @Test
    public void testUserDelete() {
        connector.delete(ObjectClass.ACCOUNT, new Uid("1234"), (new OperationOptionsBuilder()).build());
    }

    @Test
    public void testUsersGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = (ConnectorObject connectorObject)->{
            idValues.add(connectorObject.getUid().getUidValue());
            nameValues.add(connectorObject.getName().getNameValue());
            return true;
        };

        connector.executeQuery(ObjectClass.ACCOUNT, "", resultsHandler, (new OperationOptionsBuilder()).build());
        assertEquals(2, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void testUserGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = (ConnectorObject connectorObject)->{
            idValues.add(connectorObject.getUid().getUidValue());
            nameValues.add(connectorObject.getName().getNameValue());
            return true;
        };

        connector.executeQuery(ObjectClass.ACCOUNT, "1234", resultsHandler, (new OperationOptionsBuilder()).build());
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void testGroupCreate() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue("Avengers").build());

        Uid newId = connector.create(ObjectClass.GROUP, attributes, (new OperationOptionsBuilder()).build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testGroupCreateBadDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue(new BigDecimal(5)).build());

        connector.create(ObjectClass.GROUP, attributes, (new OperationOptionsBuilder()).build());
    }

    @Test
    public void testGroupModify() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue("Avengers").build());

        Uid newId = connector.update(ObjectClass.GROUP, new Uid("1234"), attributes, (new OperationOptionsBuilder()).build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void testGroupModifyDataType() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue(new BigDecimal(5)).build());

        connector.update(ObjectClass.GROUP, new Uid("1234"), attributes, (new OperationOptionsBuilder()).build());
    }

    @Test
    public void testGroupDelete() {
        connector.delete(ObjectClass.GROUP, new Uid("1234"), (new OperationOptionsBuilder()).build());
    }

    @Test
    public void testGroupsGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = (ConnectorObject connectorObject)->{
            idValues.add(connectorObject.getUid().getUidValue());
            nameValues.add(connectorObject.getName().getNameValue());
            return true;
        };

        connector.executeQuery(ObjectClass.GROUP, "", resultsHandler, (new OperationOptionsBuilder()).build());
        assertEquals(2, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void testGroupGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = (ConnectorObject connectorObject)->{
            idValues.add(connectorObject.getUid().getUidValue());
            nameValues.add(connectorObject.getName().getNameValue());
            return true;
        };

        connector.executeQuery(ObjectClass.GROUP, "1234", resultsHandler, (new OperationOptionsBuilder()).build());
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }
}
