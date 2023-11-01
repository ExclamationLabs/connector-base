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
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.PemPrivateKeyConfiguration;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.bc.BcPEMDecryptorProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

/** Implementation to load a KeyStore from a PEM file and a private key. */
public class PEMAndPrivateKeyStoreLoader implements KeyStoreLoader<PemPrivateKeyConfiguration> {

  private static final String IN_MEMORY_KEY_PASSWORD = "inMemory";

  @Override
  public KeyStore load(PemPrivateKeyConfiguration configuration) throws ConnectorSecurityException {
    try {
      String pemFile =
          FileLoaderUtil.getFileLocation(
              configuration.getName(), "pemFile", configuration.getPemFile());
      Certificate clientCertificate = loadCertificate(Files.newInputStream(Paths.get(pemFile)));

      String privateKeyFile =
          FileLoaderUtil.getFileLocation(
              configuration.getName(), "privateKeyFile", configuration.getPrivateKeyFile());
      PrivateKey privateKey =
          readPKCS8PrivateKey(Files.newInputStream(Paths.get(privateKeyFile)), configuration);

      KeyStore keyStore = KeyStore.getInstance("JKS");
      keyStore.load(null, null);
      keyStore.setCertificateEntry("client-cert", clientCertificate);
      keyStore.setKeyEntry(
          "client-key",
          privateKey,
          IN_MEMORY_KEY_PASSWORD.toCharArray(),
          new Certificate[] {clientCertificate});
      return keyStore;
    } catch (GeneralSecurityException | IOException e) {
      throw new ConnectorSecurityException("Cannot build keystore from PEM and Private Key", e);
    }
  }

  private Certificate loadCertificate(InputStream certificatePem)
      throws IOException, GeneralSecurityException {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
    final byte[] content = readPemContent(certificatePem);
    return certificateFactory.generateCertificate(new ByteArrayInputStream(content));
  }

  private byte[] readPemContent(InputStream pem) throws IOException {
    final byte[] content;
    try (PemReader pemReader = new PemReader(new InputStreamReader(pem))) {
      final PemObject pemObject = pemReader.readPemObject();
      content = pemObject.getContent();
    }
    return content;
  }

  private PrivateKey readPKCS8PrivateKey(
      InputStream contents, PemPrivateKeyConfiguration configuration) throws IOException {

    PEMParser pemParser = new PEMParser(new InputStreamReader(contents));
    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
    Object thingy = pemParser.readObject();
    PrivateKeyInfo infoObj =
        ((PEMEncryptedKeyPair) thingy)
            .decryptKeyPair(
                new BcPEMDecryptorProvider(
                    GuardedStringUtil.read(configuration.getKeyPassword()).toCharArray()))
            .getPrivateKeyInfo();

    return converter.getPrivateKey(infoObj);
  }
}
