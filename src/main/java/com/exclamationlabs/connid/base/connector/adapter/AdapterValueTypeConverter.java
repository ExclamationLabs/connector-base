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

import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

/**
 * This class contains static utility methods that allow us to efficiently convert ConnId Attribute values
 * (often stored as List<Object>) to specific supported concrete Java class types.
 */
public class AdapterValueTypeConverter {

    private static final Map<Class<?>, Function<String, ?>> conversionMap;

    static {
        conversionMap = new HashMap<>();
        conversionMap.put(String.class, s -> s);
        conversionMap.put(Integer.class, wrapDataType(Integer::parseInt));
        conversionMap.put(BigDecimal.class, wrapDataType(BigDecimal::new));
        conversionMap.put(BigInteger.class, wrapDataType(BigInteger::new));
        conversionMap.put(Boolean.class, Boolean::parseBoolean);
        conversionMap.put(Byte.class, Byte::parseByte);
        conversionMap.put(Double.class, wrapDataType(Double::parseDouble));
        conversionMap.put(Long.class, wrapDataType(Long::parseLong));
        conversionMap.put(Float.class, wrapDataType(Float::parseFloat));
        conversionMap.put(Character.class, AdapterValueTypeConverter::toChar);
    }

    private AdapterValueTypeConverter() {}

    public static <T> T getSingleAttributeValue(Class<T> returnType, Set<Attribute> attributes, Enum<?> enumValue) {
        return getAttributeValue(returnType, attributes, enumValue.toString(), true);
    }

    public static <T> T getMultipleAttributeValue(Class<T> returnType, Set<Attribute> attributes, Enum<?> enumValue) {
        return getAttributeValue(returnType, attributes, enumValue.toString(), false);
    }

    public static String getIdentityIdAttributeValue(Set<Attribute> attributes) {
        return getIdentityFixedAttributeValue(attributes, Uid.NAME);
    }

    public static String getIdentityNameAttributeValue(Set<Attribute> attributes) {
        return getIdentityFixedAttributeValue(attributes, Name.NAME);
    }

    static <T> T getAttributeValue(Class<T> returnType, Set<Attribute> attributes, String attributeName, boolean singleValue) {
        if (attributes == null) {
            return null;
        }
        Optional<Attribute> correctAttribute =
                attributes.stream().filter(current -> current.getName().equals(attributeName)).findFirst();
        Function<Attribute, Object> readAttributeFunction = singleValue ? AdapterValueTypeConverter::readSingleAttributeValue : AdapterValueTypeConverter::readMultipleAttributeValue;
        Object value = correctAttribute.map(readAttributeFunction).orElse(null);
        if (value == null) {
            return null;
        } else {
            if (value instanceof String) {
                Object obj = AdapterValueTypeConverter.convertStringToType(returnType, value.toString());
                return returnType.cast(obj);
            }
            if (returnType.isInstance(value)) {
                return returnType.cast(value);
            }
            throw new InvalidAttributeValueException("Invalid data type for attribute " + attributeName + "; received " + value.getClass().getName() + ", expected " + returnType.getName());
        }
    }

    static Object readMultipleAttributeValue(Attribute input) {
        return readAttributeValue(input, false);
    }

    static Object readSingleAttributeValue(Attribute input) {
        return readAttributeValue(input, true);
    }

    static Object readAttributeValue(Attribute input, boolean readSingleValue) {
        if (input != null && input.getValue() != null && input.getValue().get(0) != null) {
            return readSingleValue ? input.getValue().get(0) : input.getValue();
        } else {
            return null;
        }
    }

    static <T> Object convertStringToType(Class<T> returnType, String value) throws InvalidAttributeValueException {
        Function<String, ?> stringConverter = buildStringConverter(returnType);
        return stringConverter.apply(value);
    }

    private static <T> Function<String, ?> buildStringConverter(Class<T> klass) {
        if (conversionMap.containsKey(klass))
            return conversionMap.get(klass);
        throw new InvalidAttributeValueException("Unexpected return type " + klass);
    }

    private static Function<String, ?> wrapDataType(Function<String, ?> func) {
        return s -> {
            try {
                return func.apply(s);
            } catch (NumberFormatException ex) {
                throw new InvalidAttributeValueException(ex);
            }
        };
    }

    private static Character toChar(String value) {
        if (value != null && (!value.isEmpty())) {
            char[] characters = new char[1];
            value.getChars(0, 1, characters, 0);
            return characters[0];
        }
        throw new InvalidAttributeValueException("Empty/null string cannot convert to character type");
    }

    private static String getIdentityFixedAttributeValue(Set<Attribute> attributes, String identitiyName) {
        if (attributes == null) {
            return null;
        } else {
            Optional<Attribute> correctAttribute = attributes.stream().filter((current) -> current.getName().equals(identitiyName)).findFirst();
            Object value = correctAttribute.map(AdapterValueTypeConverter::readSingleAttributeValue).orElse(null);
            return value == null ? null : value.toString();
        }
    }
}
