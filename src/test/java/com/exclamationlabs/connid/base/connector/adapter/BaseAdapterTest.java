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

package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.stub.adapter.StubUsersAdapter;
import com.exclamationlabs.connid.base.connector.stub.attribute.StubUserAttribute;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.apache.commons.lang3.StringUtils;
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

public class BaseAdapterTest {

    private StubUsersAdapter adapter;

    @Before
    public void setup() {
        adapter = new StubUsersAdapter();
    }

    @Test
    public void queryAllRecordsAll() {
        assertTrue(adapter.queryAllRecords("ALL"));
    }

    @Test
    public void queryAllRecordsNull() {
        assertTrue(adapter.queryAllRecords(null));
    }

    @Test
    public void queryAllRecordsEmpty() {
        assertTrue(adapter.queryAllRecords(""));
    }

    @Test
    public void queryAllRecordsWhite() {
        assertTrue(adapter.queryAllRecords(" "));
    }

    @Test
    public void queryAllRecordsIdValue() {
        assertFalse(adapter.queryAllRecords("myid"));
    }

    @Test
    public void getConnectorObjectBuilder() {
        StubUser user = new StubUser();
        user.setId("user123");
        user.setUserName("name123");
        ConnectorObjectBuilder builder =
                adapter.getConnectorObjectBuilder(user);
        ConnectorObject object = builder.build();
        assertEquals(ObjectClass.ACCOUNT, object.getObjectClass());
        assertEquals("user123",
                AdapterValueTypeConverter.getIdentityIdAttributeValue(object.getAttributes()));
        assertEquals("name123",
                AdapterValueTypeConverter.getIdentityNameAttributeValue(object.getAttributes()));
    }

    @Test
    public void testAttributeConstruction() {
        StubUser user = new StubUser();
        user.setId("user123");
        user.setUserName("name123");
        user.setEmail("test@test.com");
        user.setGroupIds(Collections.singletonList("group1"));
        user.setClubIds(Arrays.asList("club1", "club2"));
        user.setUserTestBigDecimal(new BigDecimal("1234.1234"));
        user.setUserTestBigInteger(new BigInteger("12345678"));
        user.setUserTestBoolean(Boolean.TRUE);
        user.setUserTestByte((byte) 'c');
        user.setUserTestCharacter('D');
        user.setUserTestDouble(123456.123456);
        user.setUserTestFloat((float)-101.101);
        user.setUserTestGuardedByteArray(new GuardedByteArray("guard1".getBytes()));
        user.setUserTestGuardedString(new GuardedString("guard2".toCharArray()));
        user.setUserTestInteger(-654321);
        user.setUserTestLong(-654321654321L);

        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("key1", "value1");
        inputMap.put("key2", "value2");
        user.setUserTestMap(inputMap);

        ZonedDateTime nowTime = ZonedDateTime.now();
        user.setUserTestZonedDateTime(nowTime);

        List<Attribute> output = adapter.constructAttributesTestAccess(user);

        assertEquals(18, output.size());

        assertEquals(StubUserAttribute.USER_ID.name(), output.get(0).getName());
        assertEquals("user123", output.get(0).getValue().get(0).toString());

        assertEquals(StubUserAttribute.USER_NAME.name(), output.get(1).getName());
        assertEquals("name123", output.get(1).getValue().get(0).toString());

        assertEquals(StubUserAttribute.EMAIL.name(), output.get(2).getName());
        assertEquals("test@test.com", output.get(2).getValue().get(0).toString());

        assertEquals(StubUserAttribute.GROUP_IDS.name(), output.get(3).getName());
        assertEquals("group1", output.get(3).getValue().get(0).toString());

        assertEquals(StubUserAttribute.CLUB_IDS.name(), output.get(4).getName());
        assertEquals("club1", output.get(4).getValue().get(0).toString());
        assertEquals("club2", output.get(4).getValue().get(1).toString());

        assertEquals(StubUserAttribute.USER_TEST_BIG_DECIMAL.name(), output.get(5).getName());
        assertEquals(new BigDecimal("1234.1234"), output.get(5).getValue().get(0));

        assertEquals(StubUserAttribute.USER_TEST_BIG_INTEGER.name(), output.get(6).getName());
        assertEquals(new BigInteger("12345678"), output.get(6).getValue().get(0));

        assertEquals(StubUserAttribute.USER_TEST_BOOLEAN.name(), output.get(7).getName());
        assertEquals(Boolean.TRUE, output.get(7).getValue().get(0));

        assertEquals(StubUserAttribute.USER_TEST_BYTE.name(), output.get(8).getName());
        assertEquals((byte) 'c', output.get(8).getValue().get(0));

        assertEquals(StubUserAttribute.USER_TEST_CHARACTER.name(), output.get(9).getName());
        assertEquals('D', output.get(9).getValue().get(0));

        assertEquals(StubUserAttribute.USER_TEST_DOUBLE.name(), output.get(10).getName());
        assertEquals(123456.123456, output.get(10).getValue().get(0));

        assertEquals(StubUserAttribute.USER_TEST_FLOAT.name(), output.get(11).getName());
        assertEquals((float)-101.101, output.get(11).getValue().get(0));

        assertEquals(StubUserAttribute.USER_TEST_GUARDED_BYTE_ARRAY.name(), output.get(12).getName());
        assertTrue(StringUtils.containsIgnoreCase(
                output.get(12).getValue().get(0).toString(),
                "org.identityconnectors.common.security.GuardedByteArray@"
                ));

        assertEquals(StubUserAttribute.USER_TEST_GUARDED_STRING.name(), output.get(13).getName());
        assertTrue(StringUtils.containsIgnoreCase(
                output.get(13).getValue().get(0).toString(),
                "org.identityconnectors.common.security.GuardedString@"
        ));

        assertEquals(StubUserAttribute.USER_TEST_INTEGER.name(), output.get(14).getName());
        assertEquals(-654321, output.get(14).getValue().get(0));

        assertEquals(StubUserAttribute.USER_TEST_LONG.name(), output.get(15).getName());
        assertEquals(-654321654321L, output.get(15).getValue().get(0));

        assertEquals(StubUserAttribute.USER_TEST_MAP.name(), output.get(16).getName());
        Map<?,?> lookMap = (Map<?,?>) output.get(16).getValue().get(0);
        assertEquals("value1", lookMap.get("key1"));
        assertEquals("value2", lookMap.get("key2"));

        assertEquals(StubUserAttribute.USER_TEST_ZONED_DATE_TIME.name(), output.get(17).getName());
        assertEquals(nowTime, output.get(17).getValue().get(0));

    }


}
