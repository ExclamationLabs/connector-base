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
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Implementation to load a KeyStore from a PFX file.
 */
public class PFXKeyStoreLoader implements KeyStoreLoader<PfxConfiguration> {

    @Override
    public KeyStore load(PfxConfiguration configuration) throws ConnectorSecurityException {
        final String KEYSTORE_PASSWORD = configuration.getPassword();
        final String KEYSTORE_PATH = configuration.getFile();

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
