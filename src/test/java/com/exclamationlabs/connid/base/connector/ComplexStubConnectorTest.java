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

import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.JwtRs256Configuration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2JwtConfiguration;
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.stub.ComplexStubConnector;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubClubAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubGroupAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubSupergroupAttribute;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute;
import com.exclamationlabs.connid.base.connector.stub.driver.StubDriver;
import com.exclamationlabs.connid.base.connector.stub.model.StubClub;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubSupergroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class ComplexStubConnectorTest {

    private BaseFullAccessConnector connector;
    private StubDriver driver;
    private OperationOptions testOperationOptions;

    @Before
    public void setup() {
        connector = new ComplexStubConnector();
        JwtRs256Configuration configuration = new DualConfiguration();
        connector.init(configuration);
        driver = (StubDriver) connector.getDriver();
        testOperationOptions = new OperationOptionsBuilder().build();
    }

    @Test
    public void testUserCreateAllAttributeTypes() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());

        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_BIG_DECIMAL.name()).addValue(BigDecimal.ONE).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_BIG_INTEGER.name()).addValue(BigInteger.ONE).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_BOOLEAN.name()).addValue(Boolean.TRUE).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_BYTE.name()).addValue(64).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_CHARACTER.name()).addValue('X').build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_DOUBLE.name()).addValue(12345678.12345678).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_FLOAT.name()).addValue(1234.1234).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_GUARDED_BYTE_ARRAY.name()).addValue(
                new GuardedByteArray("test".getBytes())).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_GUARDED_STRING.name()).addValue(
                new GuardedString("test2".toCharArray())).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_INTEGER.name()).addValue(7890).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_LONG.name()).addValue(78907890).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_MAP.name()).addValue(
                Collections.singletonMap("testkey", "testvalue")).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_TEST_ZONED_DATE_TIME.name()).addValue(
                ZonedDateTime.now()).build());

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
    public void testUserCreateClub() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubClubAttribute.CLUB_ID.name()).addValue("Club Ninja").build());
        attributes.add(new AttributeBuilder().setName(StubClubAttribute.CLUB_NAME.name()).addValue("club@ninja.com").build());

        Uid newId = connector.create(new ObjectClass("CLUB"), attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("club create", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof StubClub);
        IdentityModel userParameter = (IdentityModel) driver.getMethodParameter1();
        assertNull(userParameter.getIdentityIdValue());
        assertNotNull(userParameter.getIdentityNameValue());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testUserCreateSupergroup() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubSupergroupAttribute.SUPERGROUP_ID.name()).addValue("Travelling Wilburys").build());
        attributes.add(new AttributeBuilder().setName(StubSupergroupAttribute.SUPERGROUP_NAME.name()).addValue("wilburys@yahoo.com").build());

        Uid newId = connector.create(new ObjectClass("SUPERGROUP"), attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("supergroup create", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof StubSupergroup);
        IdentityModel userParameter = (IdentityModel) driver.getMethodParameter1();
        assertNull(userParameter.getIdentityIdValue());
        assertNotNull(userParameter.getIdentityNameValue());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testUserCreateWithGroupsAndClubs() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.USER_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.EMAIL.name()).addValue("dummy@dummy.com").build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.GROUP_IDS.name()).addValue(
                Arrays.asList("id1", "id2")).build());
        attributes.add(new AttributeBuilder().setName(StubUserAttribute.CLUB_IDS.name()).addValue(
                Arrays.asList("club1", "club2")).build());

        Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("user create with group and club ids", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof StubUser);
        IdentityModel userParameter = (IdentityModel) driver.getMethodParameter1();
        assertNull(userParameter.getIdentityIdValue());
        assertNotNull(userParameter.getIdentityNameValue());
        assertNull(driver.getMethodParameter2());
    }

    @Test
    public void testGroupCreateWithSupergroups() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.GROUP_NAME.name()).addValue("Dummy").build());
        attributes.add(new AttributeBuilder().setName(StubGroupAttribute.SUPERGROUP_IDS.name()).addValue(
                Arrays.asList("id1", "id2")).build());

        Uid newId = connector.create(ObjectClass.GROUP, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("group create with supergroup ids", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof StubGroup);
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

        Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        assertTrue(driver.isInitializeInvoked());
        assertEquals("user create with group ids", driver.getMethodInvoked());
        assertNotNull(driver.getMethodParameter1());
        assertTrue(driver.getMethodParameter1() instanceof StubUser);
        IdentityModel userParameter = (IdentityModel) driver.getMethodParameter1();
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
        assertEquals("user update with group ids", driver.getMethodInvoked());
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
    public void testConstruction() {
        assertNotNull(connector.getConnectorFilterTranslator(ObjectClass.ACCOUNT));
        assertTrue(connector.getConnectorFilterTranslator(ObjectClass.ACCOUNT) instanceof DefaultFilterTranslator);

        assertNotNull(connector.getAdapter(ObjectClass.GROUP).getConnectorAttributes());
        assertNotNull(connector.getAdapter(ObjectClass.ACCOUNT).getConnectorAttributes());

        assertNotNull(connector.getConnectorSchemaBuilder());
        assertNotNull(connector.getAuthenticator());
        assertNotNull(connector.getConnectorConfiguration());


        assertEquals("ComplexStubConnector", connector.getName());
    }

    @Test
    public void testAuthentication() {
        assertEquals("ying-yang", connector.getAuthenticator().authenticate(connector.getConnectorConfiguration()));
    }

    static public class DualConfiguration implements JwtRs256Configuration, Oauth2JwtConfiguration {

        @Override
        public String getIssuer() {
            return null;
        }

        @Override
        public void setIssuer(String input) {

        }

        @Override
        public String getSubject() {
            return null;
        }

        @Override
        public void setSubject(String input) {

        }

        @Override
        public Long getExpirationPeriod() {
            return null;
        }

        @Override
        public void setExpirationPeriod(Long input) {

        }

        @Override
        public String getAudience() {
            return null;
        }

        @Override
        public void setAudience(String input) {

        }

        @Override
        public Boolean getUseIssuedAt() {
            return null;
        }

        @Override
        public void setUseIssuedAt(Boolean input) {

        }

        @Override
        public Map<String, String> getExtraClaimData() {
            return null;
        }

        @Override
        public void setExtraClaimData(Map<String, String> data) {

        }

        @Override
        public String getTokenUrl() {
            return null;
        }

        @Override
        public void setTokenUrl(String input) {

        }

        @Override
        public Map<String, String> getOauth2Information() {
            return null;
        }

        @Override
        public void setOauth2Information(Map<String, String> info) {

        }

        @Override
        public String getCurrentToken() {
            return null;
        }

        @Override
        public void setCurrentToken(String input) {

        }

        @Override
        public String getSource() {
            return null;
        }

        @Override
        public void setSource(String input) {

        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void setName(String input) {

        }

        @Override
        public Boolean getActive() {
            return null;
        }

        @Override
        public void setActive(Boolean input) {

        }

        @Override
        public ConnectorMessages getConnectorMessages() {
            return null;
        }

        @Override
        public void setConnectorMessages(ConnectorMessages messages) {

        }
    }
}
