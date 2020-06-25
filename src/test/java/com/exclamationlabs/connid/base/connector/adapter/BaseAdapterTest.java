package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.identityconnectors.framework.common.objects.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class BaseAdapterTest {

    private BaseAdapter<StubUser, StubGroup> adapter;

    @Before
    public void setup() {
        adapter = new TestAdapter();
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

    static class TestAdapter extends BaseAdapter<StubUser, StubGroup> {

        @Override
        protected ObjectClass getType() {
            return ObjectClass.ACCOUNT;
        }

        @Override
        public void delete(Uid uid) {
        }

        @Override
        public void get(String query, ResultsHandler resultsHandler) {
        }

        @Override
        public Uid update(Uid uid, Set<Attribute> set) {
            return null;
        }

        @Override
        public Uid create(Set<Attribute> set) {
            return null;
        }
    }

}
