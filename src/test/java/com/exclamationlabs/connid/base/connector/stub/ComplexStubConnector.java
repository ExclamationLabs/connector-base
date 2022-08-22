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
import com.exclamationlabs.connid.base.connector.authenticator.JWTRS256Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenJWTAuthenticator;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.JwtRs256Configuration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2JwtConfiguration;
import com.exclamationlabs.connid.base.connector.stub.adapter.*;
import com.exclamationlabs.connid.base.connector.stub.configuration.ComplexStubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.driver.ComplexStubDriver;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;
import org.identityconnectors.framework.spi.ConnectorClass;

import java.security.interfaces.RSAPrivateKey;


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
@ConnectorClass(displayNameKey = "test.display", configurationClass = ComplexStubConfiguration.class)
public class ComplexStubConnector extends BaseFullAccessConnector<ComplexStubConfiguration> {

    public ComplexStubConnector() {
        super(ComplexStubConfiguration.class);
        JWTRS256Authenticator innerAuthenticator = new JWTRS256Authenticator() {

            @Override
            protected RSAPrivateKey getPrivateKey() {
                return null;
            }

            @Override
            public String authenticate(JwtRs256Configuration configuration) throws ConnectorSecurityException {
                return "yang";
            }
        };

        OAuth2TokenJWTAuthenticator outerAuthenticator = new OAuth2TokenJWTAuthenticator(innerAuthenticator) {
            @Override
            public String authenticate(Oauth2JwtConfiguration configuration) throws ConnectorSecurityException {
                return "ying-" + jwtAuthenticator.authenticate((JwtRs256Configuration) configuration);
            }
        };

        setAuthenticator((Authenticator) outerAuthenticator);
        setDriver(new ComplexStubDriver());

        setAdapters(new StubComplexUsersAdapter(), new StubComplexGroupsAdapter(),
                new StubClubAdapter(), new StubSupergroupAdapter());
    }

}
