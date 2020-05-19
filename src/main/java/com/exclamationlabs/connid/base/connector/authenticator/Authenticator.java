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

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

public interface Authenticator {

    /**
     * Perform all required authentication needed for this connector.
     * @param configuration The configuration object respective to this connector
     * @return String containing an access token or other value produced as a result of
     * authentication.  In some implementations this could return a String flag of some kind
     * or null if authenticator has no meaningful output or actual authentication.
     * @throws ConnectorSecurityException
     */
    String authenticate(ConnectorConfiguration configuration) throws ConnectorSecurityException;
}
