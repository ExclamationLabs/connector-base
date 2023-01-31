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

import org.identityconnectors.common.security.GuardedString;

/** Utility methods to read ConnId GuardedString object into a Java String */
public class GuardedStringUtil {

  public static String read(GuardedString gString) {
    if (gString == null) {
      return null;
    }

    final String[] accessSecret = new String[1];
    gString.access(clearChars -> accessSecret[0] = new String(clearChars));
    return accessSecret[0];
  }
}
