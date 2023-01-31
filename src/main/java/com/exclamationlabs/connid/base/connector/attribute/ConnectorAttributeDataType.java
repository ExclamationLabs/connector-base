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

package com.exclamationlabs.connid.base.connector.attribute;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Map;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;

/**
 * This is an enum to denote which Java object types are supported within ConnId.
 *
 * <p>ASSIGNMENT_IDENTIFIER is used to represent a list of identifiers that are assigned to an
 * object that belong to a different object type (ex. a user with a list of different groups they
 * belong to)
 *
 * <p>Except for ASSIGNMENT_IDENTIFIER, these are derived From ConnId source of
 * org.identityconnectors.framework.common.FrameworkUtil version 1.5.0.0
 *
 * <p>ATTR_SUPPORTED_TYPES.add(String.class); ATTR_SUPPORTED_TYPES.add(Long.TYPE);
 * ATTR_SUPPORTED_TYPES.add(Long.class); ATTR_SUPPORTED_TYPES.add(Character.TYPE);
 * ATTR_SUPPORTED_TYPES.add(Character.class); ATTR_SUPPORTED_TYPES.add(Double.TYPE);
 * ATTR_SUPPORTED_TYPES.add(Double.class); ATTR_SUPPORTED_TYPES.add(Float.TYPE);
 * ATTR_SUPPORTED_TYPES.add(Float.class); ATTR_SUPPORTED_TYPES.add(Integer.TYPE);
 * ATTR_SUPPORTED_TYPES.add(Integer.class); ATTR_SUPPORTED_TYPES.add(Boolean.TYPE);
 * ATTR_SUPPORTED_TYPES.add(Boolean.class); ATTR_SUPPORTED_TYPES.add(Byte.TYPE);
 * ATTR_SUPPORTED_TYPES.add(Byte.class); ATTR_SUPPORTED_TYPES.add(byte[].class);
 * ATTR_SUPPORTED_TYPES.add(BigDecimal.class); ATTR_SUPPORTED_TYPES.add(BigInteger.class);
 * ATTR_SUPPORTED_TYPES.add(GuardedByteArray.class); ATTR_SUPPORTED_TYPES.add(GuardedString.class);
 * ATTR_SUPPORTED_TYPES.add(Map.class); ATTR_SUPPORTED_TYPES.add(ZonedDateTime.class);
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
  MAP(Map.class), // NOTE: Not supported well at all by Midpoint 4.x
  STRING(String.class),
  ZONED_DATE_TIME(ZonedDateTime.class),

  ASSIGNMENT_IDENTIFIER(String.class);

  Class<?> classType;

  public Class<?> getClassType() {
    return classType;
  }

  ConnectorAttributeDataType(Class<?> in) {
    classType = in;
  }
}
