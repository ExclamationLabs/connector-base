package com.exclamationlabs.connid.base.connector.adapter;

import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class AdapterValueTypeConverter {

    private AdapterValueTypeConverter() {}

    public static <T> T getSingleAttributeValue(Class<T> returnType, Set<Attribute> attributes, Enum<?> enumValue) {
        return getAttributeValue(returnType, attributes, enumValue.toString(), true);
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
                return (T) AdapterValueTypeConverter.convertStringToType(returnType, value.toString());
            }
            if (returnType != value.getClass()) {
                throw new InvalidAttributeValueException("Invalid data type for attribute " + attributeName + "; received " +
                        value.getClass().getName() + ", expected " + returnType.getName());
            }
            return (T) value; // Have to cast, last resort
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

    static <T> Object convertStringToType(Class<T> returnType, String value)
        throws InvalidAttributeValueException {

        if (returnType == String.class) {
            return value;
        }
        try {
            if (returnType == Integer.class) {
                return Integer.parseInt(value);
            }
            if (returnType == BigDecimal.class) {
                return new BigDecimal(value);
            }
            if (returnType == BigInteger.class) {
                return new BigInteger(value);
            }
            if (returnType == Boolean.class) {
                return Boolean.parseBoolean(value);
            }
            if (returnType == Byte.class) {
                return Byte.parseByte(value);
            }
            if (returnType == Character.class) {
                if (value != null && (!value.isEmpty())) {
                    char[] characters = new char[1];
                    value.getChars(0, 1, characters, 0);
                    return characters[0];
                }
                throw new InvalidAttributeValueException("Empty/null string cannot convert to character type");
            }
            if (returnType == Double.class) {
                return Double.parseDouble(value);
            }
            if (returnType == Long.class) {
                return Long.parseLong(value);
            }
            if (returnType == Float.class) {
                return Float.parseFloat(value);
            }
        } catch (NumberFormatException nfe) {
            throw new InvalidAttributeValueException("Invalid type " + returnType + " cannot be parsed from string", nfe);
        }
        throw new InvalidAttributeValueException("Unexpected return type " + returnType);
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
