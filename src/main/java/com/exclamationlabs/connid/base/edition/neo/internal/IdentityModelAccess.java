package com.exclamationlabs.connid.base.edition.neo.internal;

import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentityModelAccess {
  private Class<? extends IdentityModel> identityModelClass;
  private Map<String, FieldAccessInfo> fieldAccessInfoMap;
  private Method getUidMethod;
  private Method getNameMethod;

  public IdentityModelAccess() {
    fieldAccessInfoMap = new HashMap<>();
  }
}
