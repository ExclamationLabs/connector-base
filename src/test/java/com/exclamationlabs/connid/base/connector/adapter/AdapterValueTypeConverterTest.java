package com.exclamationlabs.connid.base.connector.adapter;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.jupiter.api.Test;

public class AdapterValueTypeConverterTest {

  private static final String TEST_ID1 = "testId1";
  private static final String TEST_NAME = "someName";
  private static final String MULTI_ID1 = "multi1";
  private static final String MULTI_ID2 = "multi2";
  private static final String UID_VALUE = "testUid";
  private static final String NAME_VALUE = "testName";
  private static final String INT_VALUE_STR = "55";
  private static final Integer INT_VALUE = 55;
  private static final String BIGDEC_VALUE_STR = "111166.77222";
  private static final BigDecimal BIGDEC_VALUE = new BigDecimal(BIGDEC_VALUE_STR);
  private static final String BIGINT_VALUE_STR = "88885554444";
  private static final BigInteger BIGINT_VALUE = new BigInteger(BIGINT_VALUE_STR);
  private static final String BOOL_VALUE_STR = "true";
  private static final Boolean BOOL_VALUE = true;
  private static final String BYTE_VALUE_STR = "5";
  private static final Byte BYTE_VALUE = new Byte(BYTE_VALUE_STR);
  private static final String DOUBLE_VALUE_STR = "23456789.1234";
  private static final Double DOUBLE_VALUE = new Double(DOUBLE_VALUE_STR);
  private static final String LONG_VALUE_STR = "90123456";
  private static final Long LONG_VALUE = new Long(LONG_VALUE_STR);
  private static final String CHAR_VALUE_STR = "F";
  private static final Character CHAR_VALUE = 'F';
  private Set<Attribute> attributeSet;

  @Test
  public void conversionMapValid() {
    setupStringTypes();
    assertNotNull(AdapterValueTypeConverter.conversionMap);
    assertFalse(AdapterValueTypeConverter.conversionMap.isEmpty());
    assertEquals(10, AdapterValueTypeConverter.conversionMap.size());
    assertTrue(AdapterValueTypeConverter.conversionMap.containsKey(String.class));
    assertTrue(AdapterValueTypeConverter.conversionMap.containsKey(Integer.class));
    assertTrue(AdapterValueTypeConverter.conversionMap.containsKey(BigDecimal.class));
    assertTrue(AdapterValueTypeConverter.conversionMap.containsKey(BigInteger.class));
    assertTrue(AdapterValueTypeConverter.conversionMap.containsKey(Boolean.class));
    assertTrue(AdapterValueTypeConverter.conversionMap.containsKey(Byte.class));
    assertTrue(AdapterValueTypeConverter.conversionMap.containsKey(Double.class));
    assertTrue(AdapterValueTypeConverter.conversionMap.containsKey(Long.class));
    assertTrue(AdapterValueTypeConverter.conversionMap.containsKey(Float.class));
    assertTrue(AdapterValueTypeConverter.conversionMap.containsKey(Character.class));
  }

  @Test
  public void getSingleAttributeValueString() {
    setupStringTypes();
    assertEquals(
        TEST_ID1,
        AdapterValueTypeConverter.getSingleAttributeValue(
            String.class, attributeSet, TestAttribute.TEST_ID));
  }

  @Test
  public void getSingleAttributeValueInteger() {
    setupStringTypes();
    assertEquals(
        INT_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Integer.class, attributeSet, TestAttribute.INT_ATTR));
  }

  @Test
  public void getSingleAttributeValueIntegerRealType() {
    setupRawTypes();
    assertEquals(
        INT_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Integer.class, attributeSet, TestAttribute.INT_ATTR));
  }

  @Test
  public void getSingleAttributeValueBigDecimal() {
    setupStringTypes();
    assertEquals(
        BIGDEC_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            BigDecimal.class, attributeSet, TestAttribute.BIGDEC_ATTR));
  }

  @Test
  public void getSingleAttributeValueBigDecimalRealType() {
    setupRawTypes();
    assertEquals(
        BIGDEC_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            BigDecimal.class, attributeSet, TestAttribute.BIGDEC_ATTR));
  }

  @Test
  public void getSingleAttributeValueBigInteger() {
    setupStringTypes();
    assertEquals(
        BIGINT_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            BigInteger.class, attributeSet, TestAttribute.BIGINT_ATTR));
  }

  @Test
  public void getSingleAttributeValueBigIntegerRealType() {
    setupRawTypes();
    assertEquals(
        BIGINT_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            BigInteger.class, attributeSet, TestAttribute.BIGINT_ATTR));
  }

  @Test
  public void getSingleAttributeValueBoolean() {
    setupStringTypes();
    assertEquals(
        BOOL_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Boolean.class, attributeSet, TestAttribute.BOOL_ATTR));
  }

  @Test
  public void getSingleAttributeValueBooleanRealType() {
    setupRawTypes();
    assertEquals(
        BOOL_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Boolean.class, attributeSet, TestAttribute.BOOL_ATTR));
  }

  @Test
  public void getSingleAttributeValueByte() {
    setupStringTypes();
    assertEquals(
        BYTE_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Byte.class, attributeSet, TestAttribute.BYTE_ATTR));
  }

  @Test
  public void getSingleAttributeValueByteRealType() {
    setupRawTypes();
    assertEquals(
        BYTE_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Byte.class, attributeSet, TestAttribute.BYTE_ATTR));
  }

  @Test
  public void getSingleAttributeValueDouble() {
    setupStringTypes();
    assertEquals(
        DOUBLE_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Double.class, attributeSet, TestAttribute.DOUBLE_ATTR));
  }

  @Test
  public void getSingleAttributeValueDoubleRealType() {
    setupRawTypes();
    assertEquals(
        DOUBLE_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Double.class, attributeSet, TestAttribute.DOUBLE_ATTR));
  }

  @Test
  public void getSingleAttributeValueLong() {
    setupStringTypes();
    assertEquals(
        LONG_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Long.class, attributeSet, TestAttribute.LONG_ATTR));
  }

  @Test
  public void getSingleAttributeValueLongRealType() {
    setupRawTypes();
    assertEquals(
        LONG_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Long.class, attributeSet, TestAttribute.LONG_ATTR));
  }

  @Test
  public void getSingleAttributeValueCharacter() {
    setupStringTypes();
    assertEquals(
        CHAR_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Character.class, attributeSet, TestAttribute.CHAR_ATTR));
  }

  @Test
  public void getSingleAttributeValueCharacterRealType() {
    setupRawTypes();
    assertEquals(
        CHAR_VALUE,
        AdapterValueTypeConverter.getSingleAttributeValue(
            Character.class, attributeSet, TestAttribute.CHAR_ATTR));
  }

  @Test
  public void getSingleAttributeValueUnmatchedAttribute() {
    setupStringTypes();
    assertNull(
        AdapterValueTypeConverter.getSingleAttributeValue(
            String.class, attributeSet, TestAttribute.BOGUS));
  }

  @Test
  public void badTypeInteger() {
    setupBadTypes();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                Integer.class, attributeSet, TestAttribute.INT_ATTR));
  }

  @Test
  public void badTypeBigDecimal() {
    setupBadTypes();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                BigDecimal.class, attributeSet, TestAttribute.BIGDEC_ATTR));
  }

  @Test
  public void badTypeBigInteger() {
    setupBadTypes();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                BigInteger.class, attributeSet, TestAttribute.BIGINT_ATTR));
  }

  @Test
  public void badTypeBoolean() {
    setupBadTypes();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                Boolean.class, attributeSet, TestAttribute.BOOL_ATTR));
  }

  @Test
  public void badTypeByte() {
    setupBadTypes();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                Byte.class, attributeSet, TestAttribute.BYTE_ATTR));
  }

  @Test
  public void badTypeDouble() {
    setupBadTypes();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                Double.class, attributeSet, TestAttribute.DOUBLE_ATTR));
  }

  @Test
  public void badTypeLong() {
    setupBadTypes();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                Long.class, attributeSet, TestAttribute.LONG_ATTR));
  }

  @Test
  public void badTypeFromMethodCall() {
    setupStringTypes();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                List.class, attributeSet, TestAttribute.LONG_ATTR));
  }

  @Test
  public void wrongType() {
    setupRawTypes();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                Boolean.class, attributeSet, TestAttribute.LONG_ATTR));
  }

  @Test
  public void wrongType2() {
    setupStringTypes();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                Integer.class, attributeSet, TestAttribute.DOUBLE_ATTR));
  }

  @Test
  public void emptyCharacterInvalid() {
    setupStringTypes();
    attributeSet = new HashSet<>();
    attributeSet.add(AttributeBuilder.build(TestAttribute.CHAR_ATTR.name(), ""));
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            AdapterValueTypeConverter.getSingleAttributeValue(
                Character.class, attributeSet, TestAttribute.CHAR_ATTR));
  }

  @Test
  public void nullInputReturnType() {
    setupStringTypes();
    assertNull(
        AdapterValueTypeConverter.getSingleAttributeValue(
            null, attributeSet, TestAttribute.CHAR_ATTR));
  }

  @Test
  public void nullInputEnumType() {
    setupStringTypes();
    assertNull(
        AdapterValueTypeConverter.getSingleAttributeValue(Character.class, attributeSet, null));
  }

  @Test
  public void nullInputEnumTypeMultiple() {
    setupStringTypes();
    assertNull(
        AdapterValueTypeConverter.getMultipleAttributeValue(Character.class, attributeSet, null));
  }

  @Test
  public void emptyCollectionTypeSingleResultsInNull() {
    attributeSet = new HashSet<>();
    attributeSet.add(
        AttributeBuilder.build(TestAttribute.CHAR_ATTR.name(), Collections.emptyList()));
    assertNull(
        AdapterValueTypeConverter.getSingleAttributeValue(
            Character.class, attributeSet, TestAttribute.CHAR_ATTR));
  }

  @Test
  public void emptyCollectionTypeMultipleResultsInNull() {
    attributeSet = new HashSet<>();
    attributeSet.add(
        AttributeBuilder.build(TestAttribute.CHAR_ATTR.name(), Collections.emptyList()));
    assertNull(
        AdapterValueTypeConverter.getMultipleAttributeValue(
            Character.class, attributeSet, TestAttribute.CHAR_ATTR));
  }

  @Test
  public void getMultipleAttributeValueString() {
    setupStringTypes();
    @SuppressWarnings("unchecked")
    List<String> response =
        AdapterValueTypeConverter.getMultipleAttributeValue(
            List.class, attributeSet, TestAttribute.MULTI);

    assertNotNull(response);
    assertEquals(2, response.size());
    assertEquals(MULTI_ID1, response.get(0));
    assertEquals(MULTI_ID2, response.get(1));
  }

  @Test
  public void getIdentityIdAttributeValue() {
    setupStringTypes();
    assertEquals(UID_VALUE, AdapterValueTypeConverter.getIdentityIdAttributeValue(attributeSet));
  }

  @Test
  public void getIdentityNameAttributeValue() {
    setupStringTypes();
    assertEquals(NAME_VALUE, AdapterValueTypeConverter.getIdentityNameAttributeValue(attributeSet));
  }

  private void setupStringTypes() {
    attributeSet = new HashSet<>();
    attributeSet.add(AttributeBuilder.build(TestAttribute.TEST_ID.name(), TEST_ID1));
    attributeSet.add(
        AttributeBuilder.build(TestAttribute.MULTI.name(), Arrays.asList(MULTI_ID1, MULTI_ID2)));
    attributeSet.add(AttributeBuilder.build(Uid.NAME, UID_VALUE));
    attributeSet.add(AttributeBuilder.build(Name.NAME, NAME_VALUE));
    attributeSet.add(AttributeBuilder.build(TestAttribute.TEST_NAME.name(), TEST_NAME));
    attributeSet.add(AttributeBuilder.build(TestAttribute.INT_ATTR.name(), INT_VALUE_STR));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BIGDEC_ATTR.name(), BIGDEC_VALUE_STR));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BIGINT_ATTR.name(), BIGINT_VALUE_STR));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BOOL_ATTR.name(), BOOL_VALUE_STR));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BYTE_ATTR.name(), BYTE_VALUE_STR));
    attributeSet.add(AttributeBuilder.build(TestAttribute.DOUBLE_ATTR.name(), DOUBLE_VALUE_STR));
    attributeSet.add(AttributeBuilder.build(TestAttribute.LONG_ATTR.name(), LONG_VALUE_STR));
    attributeSet.add(AttributeBuilder.build(TestAttribute.CHAR_ATTR.name(), CHAR_VALUE_STR));
  }

  private void setupRawTypes() {
    attributeSet = new HashSet<>();
    attributeSet.add(AttributeBuilder.build(TestAttribute.INT_ATTR.name(), INT_VALUE));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BIGDEC_ATTR.name(), BIGDEC_VALUE));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BIGINT_ATTR.name(), BIGINT_VALUE));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BOOL_ATTR.name(), BOOL_VALUE));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BYTE_ATTR.name(), BYTE_VALUE));
    attributeSet.add(AttributeBuilder.build(TestAttribute.DOUBLE_ATTR.name(), DOUBLE_VALUE));
    attributeSet.add(AttributeBuilder.build(TestAttribute.LONG_ATTR.name(), LONG_VALUE));
    attributeSet.add(AttributeBuilder.build(TestAttribute.CHAR_ATTR.name(), CHAR_VALUE));
  }

  private void setupBadTypes() {
    attributeSet = new HashSet<>();
    attributeSet.add(AttributeBuilder.build(TestAttribute.INT_ATTR.name(), "55.88"));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BIGDEC_ATTR.name(), "xyz"));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BIGINT_ATTR.name(), "333.222"));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BOOL_ATTR.name(), -44));
    attributeSet.add(AttributeBuilder.build(TestAttribute.BYTE_ATTR.name(), "484848"));
    attributeSet.add(AttributeBuilder.build(TestAttribute.DOUBLE_ATTR.name(), "abc"));
    attributeSet.add(AttributeBuilder.build(TestAttribute.LONG_ATTR.name(), "9090.55555"));
    attributeSet.add(AttributeBuilder.build(TestAttribute.CHAR_ATTR.name(), "XYZ"));
  }

  private enum TestAttribute {
    BOGUS,
    MULTI,
    TEST_ID,
    TEST_NAME,
    INT_ATTR,
    BIGDEC_ATTR,
    BIGINT_ATTR,
    BOOL_ATTR,
    BYTE_ATTR,
    DOUBLE_ATTR,
    LONG_ATTR,
    CHAR_ATTR
  }
}
