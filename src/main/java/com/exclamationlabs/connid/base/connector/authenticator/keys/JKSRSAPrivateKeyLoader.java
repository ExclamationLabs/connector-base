package com.exclamationlabs.connid.base.connector.authenticator.keys;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

public class JKSRSAPrivateKeyLoader implements RSAPrivateKeyLoader {
    private static final Set<ConnectorProperty> PROPERTY_NAMES;

    static {
        PROPERTY_NAMES = new HashSet<>(Arrays.asList(CONNECTOR_BASE_AUTH_JKS_PASSWORD,
                CONNECTOR_BASE_AUTH_JKS_FILE, CONNECTOR_BASE_AUTH_JKS_ALIAS));
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public RSAPrivateKey load(BaseConnectorConfiguration configuration)
            throws ConnectorSecurityException {
        KeyStore keystore;
        RSAPrivateKey privateKey;
        try {
            keystore = KeyStore.getInstance("JKS");
            char[] keystorePassword = configuration.getProperty(CONNECTOR_BASE_AUTH_JKS_PASSWORD).toCharArray();
            keystore.load(new FileInputStream(
                            configuration.getProperty(CONNECTOR_BASE_AUTH_JKS_FILE)),
                    keystorePassword);

            privateKey = (RSAPrivateKey) keystore.getKey(
                    configuration.getProperty(CONNECTOR_BASE_AUTH_JKS_ALIAS), keystorePassword);

        } catch (CertificateException ce) {
            throw new ConnectorSecurityException("Certificate error occurred while trying to load JKS file " +
                    configuration.getProperty(CONNECTOR_BASE_AUTH_JKS_FILE) + " with keystore password.", ce);
        } catch (IOException ioe) {
            throw new ConnectorSecurityException("IO error occurred while trying to load JKS file " +
                    configuration.getProperty(CONNECTOR_BASE_AUTH_JKS_FILE) + " with keystore password.", ioe);
        } catch (KeyStoreException kse) {
            throw new ConnectorSecurityException("Unable to obtain JKS Keystore instance (before attempting " +
                "actual JKS loading)", kse);
        } catch (NoSuchAlgorithmException nsae) {
            throw new ConnectorSecurityException("Unable to determine internal algorithm while attempting " +
                    "keystore load", nsae);
        } catch (UnrecoverableKeyException uke) {
            throw new ConnectorSecurityException("Unable to obtain private key from keystore due to " +
                    "unrecoverable key issue", uke);
        }

        if (privateKey == null) {
            throw new ConnectorSecurityException("Unable to read private key from configuration information.");
        }

        return privateKey;
    }
}
