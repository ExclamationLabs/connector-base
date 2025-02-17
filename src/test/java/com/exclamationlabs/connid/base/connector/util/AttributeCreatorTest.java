package com.exclamationlabs.connid.base.connector.util;

import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_CREATABLE;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.identityconnectors.framework.common.objects.Attribute;
import org.junit.jupiter.api.Test;

public class AttributeCreatorTest {
  @Test
  public void testCreateAttribute() {

    System.out.println("Testing List Attributes");
    String[] ignore = {"OBJECT_IGNORE_VALUE"};
    AttributeCreator.printAttributes(ignore, "OBJECT", AttributeTestObject.class);
    var list = AttributeCreator.listAttributes(ignore, "OBJECT", AttributeTestObject.class);
    assertEquals(7, list.size());
    System.out.println("Testing Create Attribute");
    Set<ConnectorAttribute> result = new HashSet<>();
    String[] notUpdateable = {"OBJECT_STRING_VALUE"};
    String[] notCreateable = {"OBJECT_INTEGER_VALUE"};
    AttributeCreator.createAttributes(
        result, ignore, notUpdateable, notCreateable, "OBJECT", AttributeTestObject.class);
    var stringval =
        result.stream().filter(a -> a.getName().equals("OBJECT_STRING_VALUE")).findFirst();
    var intval =
        result.stream().filter(a -> a.getName().equals("OBJECT_INTEGER_VALUE")).findFirst();
    assertEquals(true, stringval.get().getFlags().contains(NOT_UPDATEABLE));
    assertEquals(true, intval.get().getFlags().contains(NOT_CREATABLE));
    assertEquals(true, result.size() == 7);
    System.out.println("Testing Construct Attribute");
    ZonedDateTime now = ZonedDateTime.now();
    var attributes = new HashSet<Attribute>();
    var o = new AttributeTestObject();
    o.setBooleanValue(true);
    o.setDateValue(now);
    o.setIntegerValue(1);
    o.setLongValue(2L);
    o.setStringValue("Test");
    o.setDoubleValue(0.001);
    o.setFloatValue(1.1F);
    AttributeCreator.constructAttributes(attributes, ignore, "OBJECT", o);
    int countNotNull = 0;
    for (Attribute a : attributes) {
      if (a.getValue() != null) countNotNull++;
    }
    assertEquals(true, countNotNull == 7);
    o = new AttributeTestObject();
    AttributeCreator.constructModel(attributes, ignore, "OBJECT", o);
    assertEquals(true, o.getIgnoreValue() == null);
    assertEquals(true, o.getStringValue().equals("Test"));
    assertEquals(true, o.getIntegerValue() == 1);
    assertEquals(true, o.getLongValue() == 2L);
    assertEquals(true, o.getDoubleValue() == 0.001);
    assertEquals(true, o.getFloatValue() == 1.1F);
    assertEquals(true, o.getDateValue().equals(now));
    assertEquals(true, o.getBooleanValue() == true);
  }
}
