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
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.JksConfiguration;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

/** Implementation to load a RSAPrivateKey from JKS information (file, password, alias). */
public class JKSRSAPrivateKeyLoader implements RSAPrivateKeyLoader<JksConfiguration> {

  @Override
  public RSAPrivateKey load(JksConfiguration configuration) throws ConnectorSecurityException {
    KeyStore keystore;
    RSAPrivateKey privateKey;
    try {
      keystore = KeyStore.getInstance("JKS");
      char[] keystorePassword = configuration.getJksPassword().toCharArray();
      String jksFile =
          FileLoaderUtil.getFileLocation(
              configuration.getName(), "jksFile", configuration.getJksFile());
      keystore.load(new FileInputStream(jksFile), keystorePassword);

      privateKey = (RSAPrivateKey) keystore.getKey(configuration.getJksAlias(), keystorePassword);

    } catch (CertificateException ce) {
      throw new ConnectorSecurityException(
          "Certificate error occurred while trying to load JKS file "
              + configuration.getJksFile()
              + " with keystore password.",
          ce);
    } catch (IOException ioe) {
      throw new ConnectorSecurityException(
          "IO error occurred while trying to load JKS file "
              + configuration.getJksFile()
              + " with keystore password.",
          ioe);
    } catch (KeyStoreException kse) {
      throw new ConnectorSecurityException(
          "Unable to obtain JKS Keystore instance (before attempting " + "actual JKS loading)",
          kse);
    } catch (NoSuchAlgorithmException nsae) {
      throw new ConnectorSecurityException(
          "Unable to determine internal algorithm while attempting " + "keystore load", nsae);
    } catch (UnrecoverableKeyException uke) {
      throw new ConnectorSecurityException(
          "Unable to obtain private key from keystore due to " + "unrecoverable key issue", uke);
    }

    if (privateKey == null) {
      throw new ConnectorSecurityException(
          "Unable to read private key from configuration information.");
    }

    return privateKey;
  }
}
