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

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

public class PFXKeyStoreLoader implements KeyStoreLoader {
    private static final Set<ConnectorProperty> PROPERTY_NAMES;

    static {
        PROPERTY_NAMES = new HashSet<>(Arrays.asList(
                CONNECTOR_BASE_AUTH_PFX_FILE,
                CONNECTOR_BASE_AUTH_PFX_PASSWORD));
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public KeyStore load(BaseConnectorConfiguration configuration) throws ConnectorSecurityException {
        final String KEYSTORE_PASSWORD = configuration.getProperty(CONNECTOR_BASE_AUTH_PFX_PASSWORD);
        final String KEYSTORE_PATH = configuration.getPropertyFile(CONNECTOR_BASE_AUTH_PFX_FILE);

        try(InputStream inputStream = new FileInputStream(new File(KEYSTORE_PATH))) {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(inputStream, KEYSTORE_PASSWORD.toCharArray());
            return keystore;
        } catch (FileNotFoundException fnfe) {
            throw new ConnectorSecurityException("PFX file not found for keystore: " + KEYSTORE_PATH, fnfe);
        } catch (KeyStoreException kse) {
            throw new ConnectorSecurityException("Unexpected error initializing PKCS12/PFX instance", kse);
        } catch ( CertificateException | NoSuchAlgorithmException | IOException ee) {
            throw new ConnectorSecurityException("Error while loading PKCS12/PFX keystore", ee);
        }
    }
}
