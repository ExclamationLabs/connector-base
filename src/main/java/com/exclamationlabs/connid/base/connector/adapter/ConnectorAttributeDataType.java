package com.exclamationlabs.connid.base.connector.adapter;

import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * This is an enum to denote which Java object types are
 * supported within ConnId.
 *
 * Derived From ConnId source of org.identityconnectors.framework.common.FrameworkUtil
 * version 1.5.0.0
 *
 *         ATTR_SUPPORTED_TYPES.add(String.class);
 *         ATTR_SUPPORTED_TYPES.add(Long.TYPE);
 *         ATTR_SUPPORTED_TYPES.add(Long.class);
 *         ATTR_SUPPORTED_TYPES.add(Character.TYPE);
 *         ATTR_SUPPORTED_TYPES.add(Character.class);
 *         ATTR_SUPPORTED_TYPES.add(Double.TYPE);
 *         ATTR_SUPPORTED_TYPES.add(Double.class);
 *         ATTR_SUPPORTED_TYPES.add(Float.TYPE);
 *         ATTR_SUPPORTED_TYPES.add(Float.class);
 *         ATTR_SUPPORTED_TYPES.add(Integer.TYPE);
 *         ATTR_SUPPORTED_TYPES.add(Integer.class);
 *         ATTR_SUPPORTED_TYPES.add(Boolean.TYPE);
 *         ATTR_SUPPORTED_TYPES.add(Boolean.class);
 *         ATTR_SUPPORTED_TYPES.add(Byte.TYPE);
 *         ATTR_SUPPORTED_TYPES.add(Byte.class);
 *         ATTR_SUPPORTED_TYPES.add(byte[].class);
 *         ATTR_SUPPORTED_TYPES.add(BigDecimal.class);
 *         ATTR_SUPPORTED_TYPES.add(BigInteger.class);
 *         ATTR_SUPPORTED_TYPES.add(GuardedByteArray.class);
 *         ATTR_SUPPORTED_TYPES.add(GuardedString.class);
 *         ATTR_SUPPORTED_TYPES.add(Map.class);
 *         ATTR_SUPPORTED_TYPES.add(ZonedDateTime.class);
 */
public enum ConnectorAttributeDataType {
    BIG_DECIMAL(BigDecimal.class),
    BIG_INTEGER(BigInteger.class),
    BOOLEAN(Boolean.class),
    BYTE(Byte.class),
    CHARACTER(String.class),
    DOUBLE(Double.class),
    FLOAT(Float.class),
    GUARDED_BYTE_ARRAY(GuardedByteArray.class),
    GUARDED_STRING(GuardedString.class),
    INTEGER(Integer.class),
    LONG(Long.class),
    MAP(Map.class),
    STRING(String.class),
    ZONED_DATE_TIME(ZonedDateTime.class);

    Class<?> classType;

    Class<?> getClassType() {
       return classType;
    }

    ConnectorAttributeDataType(Class<?> in) {
        classType = in;
    }


}
