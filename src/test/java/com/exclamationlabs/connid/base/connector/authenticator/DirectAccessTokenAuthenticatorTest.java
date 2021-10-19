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

import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.DirectAccessTokenConfiguration;
import org.identityconnectors.framework.common.objects.ConnectorMessages;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DirectAccessTokenAuthenticatorTest {

    protected static DirectAccessTokenConfiguration configuration;

    static {

        configuration = new DirectAccessTokenConfiguration() {

            @Override
            public ConnectorMessages getConnectorMessages() {
                return null;
            }

            @Override
            public void setConnectorMessages(ConnectorMessages messages) {

            }

            @Override
            public String getCurrentToken() {
                return null;
            }

            @Override
            public void setCurrentToken(String input) {

            }

            @Override
            public String getSource() {
                return null;
            }

            @Override
            public void setSource(String input) {

            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void setName(String input) {

            }

            @Override
            public Boolean getActive() {
                return null;
            }

            @Override
            public void setActive(Boolean input) {

            }

            @Override
            public String getToken() {
                return "cooltoken";
            }

            @Override
            public void setToken(String input) {

            }
        };
    }

    @Test
    public void test() {
        Authenticator<DirectAccessTokenConfiguration> authenticator = new DirectAccessTokenAuthenticator();
        String response = authenticator.authenticate(configuration);
        assertNotNull(response);
        assertEquals("cooltoken", response);
    }
}
