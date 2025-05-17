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

package com.exclamationlabs.connid.base.edition.neo.annotation.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that this field is a nested object that should be scanned for its own
 * ModelAttribute annotations placed on fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModelAttributeHolder {

  /**
   * Will only include attributes belonging to this class in the schema and perform
   * serialization/deserialization if (1) no modes are defined (default) or (2) the connector's
   * defined mode array (using Connector modesFor() method) contains all the required modes defined
   * by this method.
   *
   * @return Array of String modes that this attribute is required to match fully.
   */
  String[] modes() default {};
}
