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

import java.util.*;
import org.identityconnectors.framework.common.objects.AttributeInfo;

/**
 * Describes an immutable Connector Attribute definition - it's name, data type, ConnId Flags and
 * current value (held in Object). This needs to be in place so the ConnId Schema can be built, and
 * also inform the Adapter of the attribute types applicable to this connector.
 */
public final class ConnectorAttribute {

  private final String name;
  private final String nativeName;

  private final ConnectorAttributeDataType dataType;

  private final Set<AttributeInfo.Flags> flags;

  private Object value;

  /**
   * Provides JSON meta information about the connector attribute. In particular, this is used to
   * store data constraint information for cases where data constraints (allowed characters, data
   * length) need to be enforced. The actual enforcement is not handled by the base framework, and
   * is instead handled by connector and onboarding implementations. Using the
   * com.exclamationlabs.connid.base.connector.attribute.meta.AttributeMetaInfo structure is
   * advised, but not enforced by the base framework. More Info: - Supplying this meta JSON is
   * optional, and this value can be null. - The JSON supplied will be placed into the "subtype" for
   * the ConnId AttributeInfo stored for this attribute (belonging to the schema/object class).
   */
  private String metaInfoJson;

  public ConnectorAttribute(
      String attributeName,
      ConnectorAttributeDataType attributeDataType,
      Set<AttributeInfo.Flags> attributeFlags) {
    this(attributeName, attributeName, attributeDataType, attributeFlags);
  }

  public ConnectorAttribute(
      String attributeName,
      String nativeNameInput,
      ConnectorAttributeDataType attributeDataType,
      Set<AttributeInfo.Flags> attributeFlags) {
    name = attributeName;
    nativeName = nativeNameInput;
    dataType = attributeDataType;
    flags = attributeFlags;
  }

  public ConnectorAttribute(
      String attributeName,
      ConnectorAttributeDataType attributeDataType,
      AttributeInfo.Flags... attributeFlags) {
    this(attributeName, attributeName, attributeDataType, null, attributeFlags);
  }

  public ConnectorAttribute(
      String attributeName,
      ConnectorAttributeDataType attributeDataType,
      String metaInfoJson,
      AttributeInfo.Flags... attributeFlags) {
    this(attributeName, attributeName, attributeDataType, metaInfoJson, attributeFlags);
  }

  public ConnectorAttribute(
      String attributeName,
      String nativeNameInput,
      ConnectorAttributeDataType attributeDataType,
      AttributeInfo.Flags... attributeFlags) {
    this(attributeName, nativeNameInput, attributeDataType, null, attributeFlags);
  }

  public ConnectorAttribute(
      String attributeName,
      String nativeNameInput,
      ConnectorAttributeDataType attributeDataType,
      String metaInfoJsonInput,
      AttributeInfo.Flags... attributeFlags) {
    name = attributeName;
    nativeName = nativeNameInput;
    dataType = attributeDataType;
    metaInfoJson = metaInfoJsonInput;
    flags = new HashSet<>();
    flags.addAll(Arrays.asList(attributeFlags));
  }

  public String getName() {
    return name;
  }

  public ConnectorAttributeDataType getDataType() {
    return dataType;
  }

  public Set<AttributeInfo.Flags> getFlags() {
    return flags;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getNativeName() {
    return nativeName;
  }

  public String getMetaInfoJson() {
    return metaInfoJson;
  }

  @Override
  public String toString() {
    return String.format(
        "%s(%s) [%s] `%s` {%s}: %s",
        name,
        nativeName,
        dataType,
        metaInfoJson,
        flags != null ? flags.toString() : "",
        value != null ? value.toString() : "");
  }

  @Override
  public boolean equals(Object in) {
    return in != null
        && in.getClass().isInstance(ConnectorAttribute.class)
        && ((ConnectorAttribute) in).getName().equals(this.getName())
        && ((ConnectorAttribute) in).getNativeName().equals(this.getNativeName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getName(), this.getNativeName());
  }
}
