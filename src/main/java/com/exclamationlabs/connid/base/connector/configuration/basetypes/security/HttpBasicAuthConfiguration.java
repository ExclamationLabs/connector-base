/*
    Copyright 2021 Exclamation Labs

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

package com.exclamationlabs.connid.base.connector.configuration.basetypes.security;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import org.identityconnectors.common.security.GuardedString;

/**
 * Configuration properties for connectors that require HTTP Basic Authentication at their
 * HTTP/HTTPS endpoint.
 */
public interface HttpBasicAuthConfiguration extends ConnectorConfiguration {
  String getBasicUsername();

  void setBasicUsername(String input);

  GuardedString getBasicPassword();

  void setBasicPassword(GuardedString input);
}
