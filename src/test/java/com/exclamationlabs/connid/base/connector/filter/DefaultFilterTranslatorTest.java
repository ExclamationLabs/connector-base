package com.exclamationlabs.connid.base.connector.filter;

import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultFilterTranslatorTest {

    private DefaultFilterTranslator filter;

    @Before
    public void setup() {
        filter = new DefaultFilterTranslator();
    }

    @Test
    public void createEqualsExpressionNullFilter() {
        assertNull(filter.createEqualsExpression(null, false));
    }

    @Test
    public void createEqualsExpressionNot() {
        assertNull(filter.createEqualsExpression(
                new EqualsFilter(AttributeBuilder.build("x")), true));
    }

    @Test
    public void createEqualsExpressionNotUidOrName() {
        assertNull(filter.createEqualsExpression(
                new EqualsFilter(AttributeBuilder.build("x")), false));
    }

    @Test
    public void createEqualsExpressionUid() {
        final String idValue = "id1";
        List<String> data = Collections.singletonList(idValue);
        assertEquals(idValue, filter.createEqualsExpression(
                new EqualsFilter(AttributeBuilder.build(Uid.NAME, data)), false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEqualsExpressionIllegalStar() {
        final String idValue = "*";
        List<String> data = Collections.singletonList(idValue);
        filter.createEqualsExpression(
                new EqualsFilter(AttributeBuilder.build(Uid.NAME, data)), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEqualsExpressionIllegalAmpersand() {
        final String idValue = "&";
        List<String> data = Collections.singletonList(idValue);
        filter.createEqualsExpression(
                new EqualsFilter(AttributeBuilder.build(Uid.NAME, data)), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createEqualsExpressionIllegalPipe() {
        final String idValue = "|";
        List<String> data = Collections.singletonList(idValue);
        filter.createEqualsExpression(
                new EqualsFilter(AttributeBuilder.build(Uid.NAME, data)), false);
    }

}
