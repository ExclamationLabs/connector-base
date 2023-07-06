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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class JsonPatchSupportTest {

  @Test
  public void testBuild() throws InvocationTargetException, IllegalAccessException {
    MyModel myModel = new MyModel();
    myModel.setField1("test1");
    myModel.setField2("test2");
    myModel.setExcludeIt("eeee");
    Set<String> exclusions = Collections.singleton("excludeIt");
    JsonPatchSupport.JsonPatchOperation[] response =
        JsonPatchSupport.buildReplacementRequest(myModel, exclusions);
    assertNotNull(response);
    assertEquals(2, response.length);
    assertEquals("replace", response[0].getOperationType());
    assertEquals("replace", response[1].getOperationType());
  }

  private class MyModel implements IdentityModel {

    private String field1;
    private String field2;
    private String excludeIt;

    public String getField1() {
      return field1;
    }

    public void setField1(String field1) {
      this.field1 = field1;
    }

    public String getField2() {
      return field2;
    }

    public void setField2(String field2) {
      this.field2 = field2;
    }

    public String getExcludeIt() {
      return excludeIt;
    }

    public void setExcludeIt(String excludeIt) {
      this.excludeIt = excludeIt;
    }

    @Override
    public String getIdentityIdValue() {
      return null;
    }

    @Override
    public String getIdentityNameValue() {
      return null;
    }
  }
}
