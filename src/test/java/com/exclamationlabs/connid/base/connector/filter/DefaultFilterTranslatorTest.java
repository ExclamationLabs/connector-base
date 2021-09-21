package com.exclamationlabs.connid.base.connector.filter;

import com.exclamationlabs.connid.base.connector.BaseConnector;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
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
    public void createContainsExpressionNoFilterIdFilter() {
        final String idValue = "id1";
        List<String> data = Collections.singletonList(idValue);
        String response = filter.createContainsExpression(
                new ContainsFilter(AttributeBuilder.build(Uid.NAME, data)), false);
        assertEquals(Uid.NAME + BaseConnector.FILTER_SEPARATOR + idValue,
                response);
    }

    @Test
    public void createContainsExpressionWithFilterIdFilter() {
        filter = new DefaultFilterTranslator(new HashSet<>(Collections.singleton("FRANK")));
        final String idValue = "id1";
        List<String> data = Collections.singletonList(idValue);
        String response = filter.createContainsExpression(
                new ContainsFilter(AttributeBuilder.build(Uid.NAME, data)), false);
        assertEquals(Uid.NAME + BaseConnector.FILTER_SEPARATOR + idValue,
                response);
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void createContainsExpressionNoFilterCannotFilter() {
        final String idValue = "test";
        List<String> data = Collections.singletonList(idValue);
        filter.createContainsExpression(
                new ContainsFilter(AttributeBuilder.build("BUBBA", data)), false);
    }

    @Test(expected = InvalidAttributeValueException.class)
    public void createContainsExpressionFilteringCannotFilterAttribute() {
        filter = new DefaultFilterTranslator(new HashSet<>(Collections.singleton("FRANK")));
        final String idValue = "test";
        List<String> data = Collections.singletonList(idValue);
        filter.createContainsExpression(
                new ContainsFilter(AttributeBuilder.build("BUBBA", data)), false);
    }

    @Test
    public void createContainsExpressionFilteringCanFilterAttribute() {
        filter = new DefaultFilterTranslator(new HashSet<>(Collections.singleton("FRANK")));
        final String idValue = "test";
        List<String> data = Collections.singletonList(idValue);
        String response = filter.createContainsExpression(
                new ContainsFilter(AttributeBuilder.build("FRANK", data)), false);
        assertEquals("FRANK" + BaseConnector.FILTER_SEPARATOR + idValue,
                response);
    }

    @Test
    public void createEqualsExpressionNullFilter() {
        assertNull(filter.createEqualsExpression(null, false));
    }

    @Test
    public void createEqualsExpressionNotReturnsNull() {
        final String idValue = "id1";
        List<String> data = Collections.singletonList(idValue);
        assertNull(filter.createEqualsExpression(
                new EqualsFilter(AttributeBuilder.build(Uid.NAME, data)), true));
    }

    @Test
    public void createEqualsExpressionNotUidOrNameReturnsNull() {
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
