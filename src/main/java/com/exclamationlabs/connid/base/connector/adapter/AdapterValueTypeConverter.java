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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;

/**
 * This class contains static utility methods that allow us to efficiently convert ConnId Attribute
 * values (often stored as List of Object) to specific supported concrete Java class types.
 */
public class AdapterValueTypeConverter {

  static final Map<Class<?>, Function<String, ?>> conversionMap;

  static {
    conversionMap = new HashMap<>();
    conversionMap.put(String.class, s -> s);
    conversionMap.put(Integer.class, Integer::parseInt);
    conversionMap.put(BigDecimal.class, BigDecimal::new);
    conversionMap.put(BigInteger.class, BigInteger::new);
    conversionMap.put(Boolean.class, Boolean::parseBoolean);
    conversionMap.put(Byte.class, Byte::parseByte);
    conversionMap.put(Double.class, Double::parseDouble);
    conversionMap.put(Long.class, Long::parseLong);
    conversionMap.put(Float.class, Float::parseFloat);
    conversionMap.put(Character.class, AdapterValueTypeConverter::toChar);
  }

  private AdapterValueTypeConverter() {}

  public static <T> T getSingleAttributeValue(
      Class<T> returnType, Set<Attribute> attributes, Enum<?> enumValue) {
    if (enumValue == null) {
      return null;
    }
    return getAttributeValue(returnType, attributes, enumValue.toString(), true);
  }

  public static <T> T getMultipleAttributeValue(
      Class<T> returnType, Set<Attribute> attributes, Enum<?> enumValue) {
    if (enumValue == null) {
      return null;
    }
    return getAttributeValue(returnType, attributes, enumValue.toString(), false);
  }

  public static String getIdentityIdAttributeValue(Set<Attribute> attributes) {
    return getIdentityFixedAttributeValue(attributes, Uid.NAME);
  }

  public static String getIdentityNameAttributeValue(Set<Attribute> attributes) {
    return getIdentityFixedAttributeValue(attributes, Name.NAME);
  }

  private static <T> T getAttributeValue(
      Class<T> returnType, Set<Attribute> attributes, String attributeName, boolean singleValue) {
    if (attributes == null || returnType == null || attributeName == null) {
      return null;
    }
    Optional<Attribute> correctAttribute = findFirstNameMatchIn(attributes).apply(attributeName);
    Function<Attribute, Object> readAttributeFunction =
        singleValue
            ? AdapterValueTypeConverter::readSingleAttributeValue
            : AdapterValueTypeConverter::readMultipleAttributeValue;

    Object value = correctAttribute.map(readAttributeFunction).orElse(null);

    if (value == null) {
      return null;
    } else {
      try {
        if (value instanceof String) {
          Object obj = AdapterValueTypeConverter.convertStringToType(returnType, value.toString());
          return returnType.cast(obj);
        }
        if (returnType.isInstance(value)) {
          return returnType.cast(value);
        }
        if (returnType == Set.class && value instanceof List) {
          return returnType.cast(new HashSet<T>((List<T>) value));
        }
        throw new InvalidAttributeValueException(
            "Invalid data type for attribute "
                + attributeName
                + "; received "
                + value.getClass().getName()
                + ", expected "
                + returnType.getName());

      } catch (NumberFormatException nfe) {
        throw new InvalidAttributeValueException(
            "Unable to convert numeric value for attribute "
                + attributeName
                + "; value: "
                + value
                + "; class type: "
                + returnType.getSimpleName(),
            nfe);
      } catch (IllegalArgumentException illE) {
        throw new InvalidAttributeValueException(
            "Invalid data type for attribute "
                + attributeName
                + "; value: "
                + value
                + "; class type: "
                + returnType.getSimpleName(),
            illE);
      }
    }
  }

  private static Object readMultipleAttributeValue(Attribute input) {
    return readAttributeValue(input, false);
  }

  static Object readSingleAttributeValue(Attribute input) {
    return readAttributeValue(input, true);
  }

  static String readSingleAttributeValueAsString(Attribute input) {
    Object value = readAttributeValue(input, true);
    return value == null ? null : value.toString();
  }

  private static Object readAttributeValue(Attribute input, boolean readSingleValue) {
    if (input != null
        && input.getValue() != null
        && (!input.getValue().isEmpty())
        && input.getValue().get(0) != null) {
      return readSingleValue ? input.getValue().get(0) : input.getValue();
    } else {
      return null;
    }
  }

  private static <T> Object convertStringToType(Class<T> returnType, String value)
      throws IllegalArgumentException {
    Function<String, ?> stringConverter = buildStringConverter(returnType);
    return stringConverter.apply(value);
  }

  private static <T> Function<String, ?> buildStringConverter(Class<T> klass)
      throws IllegalArgumentException {
    if (conversionMap.containsKey(klass)) {
      return conversionMap.get(klass);
    }
    throw new IllegalArgumentException("Unexpected return type " + klass);
  }

  private static Character toChar(String value) throws IllegalArgumentException {
    if (!value.isEmpty()) {
      char[] characters = new char[1];
      value.getChars(0, 1, characters, 0);
      return characters[0];
    }
    throw new IllegalArgumentException("Empty string cannot convert to character type");
  }

  private static String getIdentityFixedAttributeValue(
      Set<Attribute> attributes, String identitiyName) {
    if (attributes == null) {
      return null;
    } else {
      Optional<Attribute> correctAttribute = findFirstNameMatchIn(attributes).apply(identitiyName);
      Object value =
          correctAttribute.map(AdapterValueTypeConverter::readSingleAttributeValue).orElse(null);
      return value == null ? null : value.toString();
    }
  }

  private static Function<String, Optional<Attribute>> findFirstNameMatchIn(
      Set<Attribute> attributes) {
    return attributeName ->
        attributes.stream().filter(a -> a.getName().equals(attributeName)).findFirst();
  }
}
