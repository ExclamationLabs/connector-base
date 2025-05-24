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

package com.exclamationlabs.connid.base.edition.neo.internal.model;

import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.edition.neo.internal.FieldAccessInfo;
import com.exclamationlabs.connid.base.edition.neo.internal.IdentityModelAccess;
import com.exclamationlabs.connid.base.edition.neo.model.AssignmentType;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.Direction;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.BooleanUtils;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo;

public class ModelReader {

  private ModelReader() {}

  public static Set<Attribute> execute(
      IdentityModel model, IdentityModelAccess identityModelAccess) {
    Set<Attribute> attributeInfoSet = new HashSet<>();

    for (var attributeName : identityModelAccess.getFieldAccessInfoMap().keySet()) {
      var fieldAccessInfo = identityModelAccess.getFieldAccessInfoMap().get(attributeName);

      if (fieldAccessInfo.getIdentifier() == ConnIdType.UID
          || fieldAccessInfo.getIdentifier() == ConnIdType.NAME
          || fieldAccessInfo.getDirection() == Direction.OUTBOUND_ONLY) {
        continue;
      }
      Attribute currentAttribute;
      Object readValue = readDepthValue(attributeName, fieldAccessInfo, model);
      boolean readMultiValue =
          Arrays.stream(fieldAccessInfo.getFlags())
                  .anyMatch(it -> it == AttributeInfo.Flags.MULTIVALUED)
              && readValue instanceof Iterable;
      if (readValue == null) {
        currentAttribute = AttributeBuilder.build(attributeName);
      } else {
        switch (fieldAccessInfo.getDataType()) {
          case BOOLEAN:
            if (readMultiValue) {
              List<Boolean> booleanValues = new ArrayList<>();
              for (Object currentValue : (Iterable<?>) readValue) {
                if (currentValue != null) {
                  booleanValues.add(readBooleanValue(currentValue));
                }
              }
              currentAttribute = AttributeBuilder.build(attributeName, booleanValues);
            } else {
              currentAttribute = AttributeBuilder.build(attributeName, readBooleanValue(readValue));
            }
            break;
          case INTEGER:
            if (readMultiValue) {
              List<Integer> intValues = new ArrayList<>();
              for (Object currentValue : (Iterable<?>) readValue) {
                if (currentValue != null) {
                  intValues.add(readIntegerValue(currentValue));
                }
              }
              currentAttribute = AttributeBuilder.build(attributeName, intValues);
            } else {
              currentAttribute = AttributeBuilder.build(attributeName, readIntegerValue(readValue));
            }
            break;
          case ASSIGNMENT_IDENTIFIER:
            var assignmentType = (AssignmentType) readValue;
            currentAttribute =
                AttributeBuilder.build(
                    attributeName, assignmentType.getCurrentInboundAssignments());
            break;
          case GUARDED_STRING:
            currentAttribute =
                AttributeBuilder.build(
                    attributeName, new GuardedString(readValue.toString().toCharArray()));
            break;
          default: // string/toString()
            if (readMultiValue) {
              List<String> stringValues = new ArrayList<>();
              for (Object currentValue : (Iterable<?>) readValue) {
                if (currentValue != null) {
                  stringValues.add(currentValue.toString());
                }
              }
              currentAttribute = AttributeBuilder.build(attributeName, stringValues);
            } else {
              currentAttribute = AttributeBuilder.build(attributeName, readValue.toString());
            }
            break;
        }
      }
      attributeInfoSet.add(currentAttribute);
    }
    return attributeInfoSet;
  }

  private static Object readDepthValue(
      final String attributeName, FieldAccessInfo fieldAccessInfo, IdentityModel model) {
    Object rawAttributeValue = null;

    Object depthDataObject = model;
    Object obtainedValue = null;

    for (var getterMethod : fieldAccessInfo.getGetterAccess()) {
      try {
        obtainedValue = getterMethod.invoke(depthDataObject);
        if (obtainedValue == null) {
          rawAttributeValue = null;
          break;
        } else {
          depthDataObject = obtainedValue;
          rawAttributeValue = obtainedValue;
        }
      } catch (IllegalArgumentException illE) {
        Logger.warn(
            ModelReader.class,
            "Unexpected reflection access for get of attribute" + attributeName,
            illE);
      } catch (ReflectiveOperationException roe) {
        Logger.warn(
            ModelReader.class,
            "Unexpected reflection access issue for get of attribute" + attributeName,
            roe);
      }
    }
    return rawAttributeValue;
  }

  static boolean readBooleanValue(Object readValue) {
    if (readValue instanceof Boolean) {
      return BooleanUtils.toBoolean((Boolean) readValue);
    } else if (readValue instanceof Integer) {
      return BooleanUtils.toBoolean((Integer) readValue);
    } else {
      return BooleanUtils.toBoolean(readValue.toString());
    }
  }

  static int readIntegerValue(Object readValue) {
    if (readValue instanceof Integer) {
      return (Integer) readValue;
    } else {
      return Integer.parseInt(readValue.toString());
    }
  }
}
