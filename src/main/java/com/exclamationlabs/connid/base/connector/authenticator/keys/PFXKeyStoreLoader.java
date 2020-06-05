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
        final String KEYSTORE_PATH = configuration.getProperty(CONNECTOR_BASE_AUTH_PFX_FILE);

        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            InputStream inputStream = new FileInputStream(new File(KEYSTORE_PATH));
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
