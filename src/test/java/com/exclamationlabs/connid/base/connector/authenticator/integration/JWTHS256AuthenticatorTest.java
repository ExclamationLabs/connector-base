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

package com.exclamationlabs.connid.base.connector.authenticator.integration;

import com.exclamationlabs.connid.base.connector.authenticator.JWTHS256Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.*;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.JwtHs256Configuration;
import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Test for JWTHS256Authenticator, using Dev ELabs Zoom configuration
 */
@Ignore // TODO: put back in place once Zoom is upgraded to 2.0 base
public class JWTHS256AuthenticatorTest extends IntegrationTest {

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(() -> "ZOOM").build();
    }
    @Test
    public void test() {
        JwtHs256Configuration configuration = new TestConfiguration(getConfigurationName());
        ConfigurationReader.setupTestConfiguration(configuration);
        setup(configuration);
        String response = new JWTHS256Authenticator().authenticate(configuration);
        assertNotNull(response);
    }

    protected static class TestConfiguration extends DefaultConnectorConfiguration
        implements JwtHs256Configuration {

        public TestConfiguration(String nameIn) {
            name = nameIn;
        }

        @ConfigurationInfo(path = "security.authenticator.jwtHs256.issuer")
        private String issuer;

        @ConfigurationInfo(path = "security.authenticator.jwtHs256.secret")
        private String secret;

        @ConfigurationInfo(path = "security.authenticator.jwtHs256.expirationPeriod")
        private Long expirationPeriod;

        @Override
        public String getIssuer() {
            return issuer;
        }

        @Override
        public void setIssuer(String input) {
            issuer = input;
        }

        @Override
        public String getSecret() {
            return secret;
        }

        @Override
        public void setSecret(String input) {
            secret = input;
        }

        @Override
        public Long getExpirationPeriod() {
            return expirationPeriod;
        }

        @Override
        public void setExpirationPeriod(Long input) {
            expirationPeriod = input;
        }
    }


}
