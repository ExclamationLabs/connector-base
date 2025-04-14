package com.exclamationlabs.connid.base.edition.neo.internal;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.Direction;
import java.lang.reflect.Method;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.identityconnectors.framework.common.objects.AttributeInfo;

@Getter
@Setter
public class FieldAccessInfo {
  String attributeName;
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

  public FieldAccessInfo() {}
}
