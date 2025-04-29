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

package com.exclamationlabs.connid.base.edition.neo.model;

/**
 * Used by the ModelAttribute annotation's "identifier" parameter, this enum defines the type of
 * identifier that a data field might be. The default if not used is NONE (not an identifier).
 */
public enum ConnIdType {
  UID, // __UID__ unique identifier understood by the ConnID framework
  NAME, // __NAME__ unique name understood by the ConnID framework
  NONE // Any other attribute that is neither __UID__ nor __NAME__
}
