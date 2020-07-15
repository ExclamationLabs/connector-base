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

package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An Authenticator is an object that might be needed by a connector implementation
 * in order to obtain some kind of access token (String) required to
 * authenticate to its destination system (usually via a Driver).
 */
public interface Authenticator {

    /**
     * Returns the names of the properties for properties
     * that must be present in order for this Authenticator to function.
     * This method should return the required property names for any sub-authenticators
     * or key loaders that it uses.
     * @return Set containing property names, represented as a set of enum values.
     * Returning null or an empty set is also allowed if there are no properties for
     * this driver.
     */
    Set<ConnectorProperty> getRequiredPropertyNames();

    /**
     * Perform all required authentication needed for this connector.
     * @param configuration The configuration object respective to this connector
     * @return String containing an access token or other value produced as a result of
     * authentication.  In some implementations this could return a String flag of some kind
     * or null if authenticator has no meaningful output or actual authentication.
     * @throws ConnectorSecurityException if could not authenticate or permissions was denied
     */
    String authenticate(BaseConnectorConfiguration configuration) throws ConnectorSecurityException;

    /**
     * Define any additional authentication headers needed during authentication.  Usually these are HTTP
     * headers.
     * @param configuration Reference to Configuration to aid implementing authentications setup proper
     *                      authentication values.
     * @return Map containg key/value pairs of additional authentication values.
     */
    default Map<String, String> getAdditionalAuthenticationHeaders(BaseConnectorConfiguration configuration) {
        return new HashMap<>();
    }
}
