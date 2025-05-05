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

package com.exclamationlabs.connid.base.edition.neo.internal;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.Direction;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.identityconnectors.framework.common.objects.AttributeInfo;

@Getter
@Setter
public class FieldAccessInfo {
  String attributeName;
  Field field;
  Class<?> fieldClass;
  List<Method> getterAccess;
  List<Method> setterAccess;

  // @ModelAttribute info
  ConnIdType identifier;
  String nativeName;
  Direction direction;
  ConnectorAttributeDataType dataType;
  AttributeInfo.Flags[] flags;
  String metaInfoJson;
  boolean supportsNativeEqualsFilter;
  boolean supportsNativeContainsFilter;

  public FieldAccessInfo() {}
}
