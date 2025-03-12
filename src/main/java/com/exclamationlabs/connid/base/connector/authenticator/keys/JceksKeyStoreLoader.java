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

import com.exclamationlabs.connid.base.connector.authenticator.util.FileLoaderUtil;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.KeyStoreConfiguration;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

/** Implementation to load a KeyStore from a Keystore file. */
public class JceksKeyStoreLoader<T> implements KeyStoreLoader<KeyStoreConfiguration> {

  @Override
  public KeyStore load(KeyStoreConfiguration configuration) throws ConnectorSecurityException {
    final String KEYSTORE_PASSWORD = GuardedStringUtil.read(configuration.getKeystorePassword());
    final String KEYSTORE_PATH =
        FileLoaderUtil.getFileLocation(
            configuration.getName(), "keystoreFile", configuration.getKeystoreFile());
    InputStream keystoreInputStream;
    try {
      if (StringUtils.startsWith(KEYSTORE_PATH, "resource:")) {
        final String RESOURCE_NAME = StringUtils.substringAfter(KEYSTORE_PATH, "resource:");
        keystoreInputStream = getClass().getClassLoader().getResourceAsStream(RESOURCE_NAME);
        if (keystoreInputStream == null) {
          throw new ConnectorSecurityException("JCEKS File resource not found: " + RESOURCE_NAME);
        }
      } else {
        keystoreInputStream = new FileInputStream(KEYSTORE_PATH);
      }
      KeyStore keystore = KeyStore.getInstance("JCEKS");
      keystore.load(keystoreInputStream, KEYSTORE_PASSWORD.toCharArray());
      return keystore;
    } catch (FileNotFoundException fnfe) {
      throw new ConnectorSecurityException("Keystore file not found: " + KEYSTORE_PATH, fnfe);
    } catch (KeyStoreException kse) {
      throw new ConnectorSecurityException("Unexpected error initializing Keystore instance", kse);
    } catch (CertificateException | NoSuchAlgorithmException | IOException ee) {
      throw new ConnectorSecurityException("Error while loading Keystore", ee);
    }
  }
}
