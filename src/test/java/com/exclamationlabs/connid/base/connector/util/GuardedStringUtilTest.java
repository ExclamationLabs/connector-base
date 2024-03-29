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

package com.exclamationlabs.connid.base.connector.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.identityconnectors.common.security.GuardedString;
import org.junit.jupiter.api.Test;

public class GuardedStringUtilTest {

  @Test
  public void testRead() {
    final String SECRET_VALUE = "I am a secret!";
    GuardedString guardedString = new GuardedString(SECRET_VALUE.toCharArray());
    assertEquals(SECRET_VALUE, GuardedStringUtil.read(guardedString));
  }
}
