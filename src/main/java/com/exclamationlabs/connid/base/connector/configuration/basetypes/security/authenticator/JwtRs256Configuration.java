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

package com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import org.identityconnectors.common.security.GuardedString;

/**
 * Configuration properties for connectors that use the JWT authentication using the RS256 strategy.
 *
 * <p>JWS: RS256 Algorithm: RSA256 Description: RSASSA-PKCS1-v1_5 with SHA-256
 */
public interface JwtRs256Configuration extends ConnectorConfiguration {
  GuardedString getIssuer();

  void setIssuer(GuardedString input);

  GuardedString getSubject();

  void setSubject(GuardedString input);

  Long getExpirationPeriod();

  void setExpirationPeriod(Long input);

  String getAudience();

  void setAudience(String input);

  Boolean getUseIssuedAt();

  void setUseIssuedAt(Boolean input);
}
