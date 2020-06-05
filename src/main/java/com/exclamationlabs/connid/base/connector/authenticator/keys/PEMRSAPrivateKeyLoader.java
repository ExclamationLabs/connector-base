package com.exclamationlabs.connid.base.connector.authenticator.keys;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

public class PEMRSAPrivateKeyLoader implements RSAPrivateKeyLoader {
    private static final Set<ConnectorProperty> PROPERTY_NAMES;

    static {
        PROPERTY_NAMES = new HashSet<>(Collections.singletonList(
                CONNECTOR_BASE_AUTH_PEM_FILE));
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public RSAPrivateKey load(BaseConnectorConfiguration configuration)
            throws ConnectorSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        String pemFile = configuration.getProperty(CONNECTOR_BASE_AUTH_PEM_FILE);
        try {
            PEMParser pemParser = new PEMParser(new FileReader(pemFile));
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
