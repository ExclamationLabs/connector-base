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

package com.exclamationlabs.connid.base.connector.driver.rest.util;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for connectors to use to help build and execute JSON Patch requests. (for
 * RestRequest withJsonPatch())
 */
public class JsonPatchSupport {

  private JsonPatchSupport() {}

  public static JsonPatchOperation[] buildReplacementRequest(
      IdentityModel model, Set<String> exclusions)
      throws IllegalAccessException, InvocationTargetException {
    List<JsonPatchOperation> operationList = new ArrayList<>();
    for (Method method : model.getClass().getMethods()) {
      String methodNameWithoutGet =
          StringUtils.uncapitalize(StringUtils.substringAfter(method.getName(), "get"));
      if (method.getName().startsWith("get")
          && (!exclusions.contains(methodNameWithoutGet))
          && (!method.getName().equalsIgnoreCase("getClass"))
          && (!method.getName().equalsIgnoreCase("getValueBySearchableAttributeName"))
          && (!method.getName().equalsIgnoreCase("getIdentityIdValue"))
          && (!method.getName().equalsIgnoreCase("getIdentityNameValue"))) {
        Object value = method.invoke(model, null);
        if (value != null) {
          String fieldName =
              "/" + StringUtils.uncapitalize(StringUtils.substringAfter(method.getName(), "get"));
          operationList.add(new JsonPatchOperation(fieldName, value.toString()));
        }
      }
    }
    return operationList.toArray(new JsonPatchOperation[0]);
  }

  public static class JsonPatchOperation {
    @SerializedName("op")
    private String operationType;

    private String path;
    private String value;

    public JsonPatchOperation(String path, String value) {
      setPath(path);
      setValue(value);
      setOperationType("replace");
    }

    public String getOperationType() {
      return operationType;
    }

    public void setOperationType(String operationType) {
      this.operationType = operationType;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
