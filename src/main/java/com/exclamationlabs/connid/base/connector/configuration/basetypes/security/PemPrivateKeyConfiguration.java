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
 * Configuration properties for connectors that need to provide a PEM file and a Private key in
 * order to connect for authentication.
 */
public interface PemPrivateKeyConfiguration extends ConnectorConfiguration {
  GuardedString getPemFile();

  void setPemFile(GuardedString input);

  GuardedString getPrivateKeyFile();

  void setPrivateKeyFile(GuardedString input);

  GuardedString getKeyPassword();

  void setKeyPassword(GuardedString input);

  GuardedString getKeyStorePassword();

  void setKeyStorePassword(GuardedString input);
}
