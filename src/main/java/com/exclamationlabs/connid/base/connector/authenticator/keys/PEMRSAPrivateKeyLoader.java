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

import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.PemConfiguration;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
/**
 * Implementation to load a RSAPrivateKey from a PEM file.
 */
public class PEMRSAPrivateKeyLoader implements RSAPrivateKeyLoader<PemConfiguration> {

    @Override
    public RSAPrivateKey load(PemConfiguration configuration)
            throws ConnectorSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        String pemFile = configuration.getPemFile();
        try (PEMParser pemParser = new PEMParser(new FileReader(pemFile))) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            Object object = pemParser.readObject();
            KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
            return (RSAPrivateKey) kp.getPrivate();
        } catch (FileNotFoundException fnfe) {
            throw new ConnectorSecurityException("File not found while loading " +
                    "PEM file " + pemFile,
                    fnfe);
        } catch (IOException ioe) {
            throw new ConnectorSecurityException("IO error while reading " +
                    "PEM file " + pemFile,
                    ioe);
        }
    }

}
