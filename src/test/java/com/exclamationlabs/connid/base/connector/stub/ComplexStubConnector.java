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

package com.exclamationlabs.connid.base.connector.stub;

import com.exclamationlabs.connid.base.connector.BaseFullAccessConnector;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.JWTAuthenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenJWTAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.stub.adapter.StubClubAdapter;
import com.exclamationlabs.connid.base.connector.stub.adapter.StubGroupsAdapter;
import com.exclamationlabs.connid.base.connector.stub.adapter.StubSupergroupAdapter;
import com.exclamationlabs.connid.base.connector.stub.adapter.StubUsersAdapter;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.driver.StubDriver;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;
import org.identityconnectors.framework.spi.ConnectorClass;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

/**
 * Test of complex connector tests
 * - deep authenticator setup
 * - nested hierarchy
 *  Users
 *      - Groups
 *          - Supergroups (object class "SUPERGROUP")
 *      - Clubs (object class "CLUB")
 *
 */
@ConnectorClass(displayNameKey = "test.display", configurationClass = StubConfiguration.class)
public class ComplexStubConnector extends BaseFullAccessConnector {

    public ComplexStubConnector() {
        Authenticator innerAuthenticator = new JWTAuthenticator() {
            @Override
            public Set<ConnectorProperty> getRequiredPropertyNames() {
                return new HashSet<>(Arrays.asList(
                        CONNECTOR_BASE_AUTH_PFX_FILE,
                        CONNECTOR_BASE_AUTH_PFX_PASSWORD));
            }

            @Override
            public String authenticate(ConnectorConfiguration configuration) throws ConnectorSecurityException {
                return "yang";
            }
        };

        Authenticator outerAuthenticator = new OAuth2TokenJWTAuthenticator(innerAuthenticator) {
            @Override
            public String authenticate(ConnectorConfiguration configuration) throws ConnectorSecurityException {
                return "ying-" + jwtAuthenticator.authenticate(configuration);
            }
        };

        setAuthenticator(outerAuthenticator);
        setDriver(new StubDriver() {
            @Override
            public Set<ConnectorProperty> getRequiredPropertyNames() {
                return new HashSet<>(Arrays.asList(
                        CONNECTOR_BASE_AUTH_JKS_ALIAS,
                        CONNECTOR_BASE_AUTH_JKS_FILE,
                        CONNECTOR_BASE_AUTH_JKS_PASSWORD));
            }
        });

        setAdapters(new StubUsersAdapter(), new StubGroupsAdapter(),
                new StubClubAdapter(), new StubSupergroupAdapter());
    }

}
