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
 * Configuration properties for connectors that need to provide a PFX file as part of their
 * credentials for authentication.
 */
public interface PfxConfiguration extends ConnectorConfiguration {
  String getPfxFile();

  void setPfxFile(String input);

  GuardedString getPfxPassword();

  void setPfxPassword(GuardedString input);

  GuardedString getKeyPassword();

  void setKeyPassword(GuardedString input);

  String getKeyAlias();

  void setKeyAlias(String keyAlias);

  String getTlsVersion();

  void setTlsVersion(String tlsVersion);
}
