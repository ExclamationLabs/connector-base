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

package com.exclamationlabs.connid.base.connector.stub.attribute;

public enum StubUserAttribute {
  USER_ID,
  USER_NAME,
  EMAIL,
  GROUP_IDS,
  CLUB_IDS,

  // Test all other data-types
  USER_TEST_BIG_DECIMAL,
  USER_TEST_BIG_INTEGER,
  USER_TEST_BOOLEAN,
  USER_TEST_BYTE,
  USER_TEST_CHARACTER,
  USER_TEST_DOUBLE,
  USER_TEST_FLOAT,
  USER_TEST_GUARDED_BYTE_ARRAY,
  USER_TEST_GUARDED_STRING,
  USER_TEST_INTEGER,
  USER_TEST_LONG,
  USER_TEST_MAP
}
