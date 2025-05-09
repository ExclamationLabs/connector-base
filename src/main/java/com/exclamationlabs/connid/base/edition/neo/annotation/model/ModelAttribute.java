/*
    Copyright 2025 Exclamation Labs

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

package com.exclamationlabs.connid.base.edition.neo.annotation.model;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.edition.neo.IamType;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.Direction;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.identityconnectors.framework.common.objects.AttributeInfo;

/**
 * Annotation to define a model attribute for data to be represented in the IGA system. This should
 * be placed on fields that should be mapped to attributes in the IGA system.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModelAttribute {
  /**
   * The string name of the attribute that will be used in the IGA system. An alphanumeric string
   * preferably not using spaces, whitespace or special characters. If the value is empty ("") the
   * name of the attribute will be the same as the field name.
   *
   * @return the name of the attribute
   */
  String value() default "";

  /**
   * This is used to denote if the attribute is a unique identifier, a name, or neither for ConnId
   * framework purposes.
   *
   * @return the ConnIdType of the attribute (default is NONE)
   */
  ConnIdType identifier() default ConnIdType.NONE;

  /**
   * The native name of the attribute. This is the name of the attribute in the source system. If
   * not supplied the value (attribute name) will be used.
   *
   * @return the native name of the attribute
   */
  String nativeName() default "";

  /**
   * The direction of transmission for this attribute. INBOUND means the attribute is read from the
   * resource system into the IGA system. OUTBOUND means the attribute is written from the IGA
   * system to the resource system. BOTH means the attribute is read from the resource system into
   * the IGA system and written from the IGA system to the resource system.
   *
   * @return Direction of INBOUND, OUTBOUND, or BOTH (default if not specified)
   */
  Direction direction() default Direction.BOTH;

  /**
   * The data type of the attribute to be used in the IGA.
   *
   * @return ConnectorAttributeDataType of the attribute (STRING is default if not specified)
   */
  ConnectorAttributeDataType type() default ConnectorAttributeDataType.STRING;

  /**
   * The flags of the attribute to be used in the IGA.
   *
   * @return AttributeInfo.Flags of the attribute
   */
  AttributeInfo.Flags[] flags() default {};

  /**
   * The constraints of the attribute to be used in the IGA. These are often used to enforce data
   * validation rules, either in an inbound or outbound direction. See
   * com.exclamationlabs.connid.base.connector.attribute.meta.AttributeConstraint class for
   * structure.
   *
   * @return String containing the constraints of the attribute in JSON format as a list of
   *     AttributeConstraint objects
   */
  String metaInfoJson() default "";

  /**
   * Specify whether the destination system is able to filter on this attribute using an equals
   * condition when multiple records are being returned. Example: For a model attribute named TITLE,
   * a filter is received asking to return only records with TITLE = "Manager" exactly. Returning
   * 'true' indicates that your destination API can accommodate this filter, and that
   * driver/invocator involved will facilitate this filtering (using supplied ResultsFilter object).
   * The default of false is presumed if not supplied.
   *
   * @return true if this attribute supports equals filter natively, false otherwise.
   */
  boolean supportsNativeEqualsFilter() default false;

  /**
   * Specify whether the destination system is able to filter on this attribute using a contains
   * condition when multiple records are being returned. Example: For a model attribute named TITLE,
   * a filter is received asking to return only records with TITLE containing the String "Manager",
   * such that "Store Manager", "Manager of IT" and "Senior Manager of Materials" would all match.
   * Returning 'true' indicates that your destination API can accommodate this filter, and that
   * driver/invocator involved will facilitate this filtering (using supplied ResultsFilter object).
   * The default of false is presumed if not supplied.
   *
   * @return true if this attribute supports contains filter natively, false otherwise.
   */
  boolean supportsNativeContainsFilter() default false;

  /**
   * Will only include the attribute in the schema and perform serialization/deserialization if (1)
   * no modes are defined (default) or (2) the connector's defined mode array (using Connector
   * modesFor() method) contains all the required modes defined by this method.
   *
   * @return Array of String modes that this attribute is required to match fully.
   */
  String[] modes() default {};

  /**
   * The applicable object type that this attribute is associated with. Default is UNDEFINED.
   *
   * @return IamType of the attribute (UNDEFINED is default if not specified)
   */
  IamType forType() default IamType.UNDEFINED;
}
