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

package com.exclamationlabs.connid.base.connector.authenticator.keys;

import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.PfxConfiguration;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.ConnectorMessages;
import org.junit.Test;

import java.security.KeyStore;

import static org.junit.Assert.assertNotNull;

public class PFXKeyStoreLoaderTest {

    @Test
    public void test() {
        PFXKeyStoreLoader loader = new PFXKeyStoreLoader();
        PfxConfiguration configuration = new TestConfiguration();

        KeyStore result = loader.load(configuration);
        assertNotNull(result);
    }

    static class TestConfiguration implements PfxConfiguration {

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
            return "test";
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
        public String getPfxFile() {
            return "resource:fis-default.pfx";
        }

        @Override
        public void setPfxFile(String input) {

        }

        @Override
        public GuardedString getPfxPassword() {
            return new GuardedString("4ZWTYblwadSTqAnoLlGx".toCharArray());
        }

        @Override
        public void setPfxPassword(GuardedString input) {

        }

        @Override
        public ConnectorMessages getConnectorMessages() {
            return null;
        }

        @Override
        public void setConnectorMessages(ConnectorMessages messages) {

        }
    }
}
