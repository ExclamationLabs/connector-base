package com.exclamationlabs.connid.base.connector.authenticator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.util.*;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;
import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.CONNECTOR_BASE_AUTH_JWT_EXPIRATION_PERIOD;

public class JWTHS256Authenticator extends JWTAuthenticator {

    private static final Set<ConnectorProperty> PROPERTY_NAMES;

    static {
        PROPERTY_NAMES = new HashSet<>(Arrays.asList(CONNECTOR_BASE_AUTH_JWT_ISSUER,
                CONNECTOR_BASE_AUTH_JWT_SECRET,
                CONNECTOR_BASE_AUTH_JWT_EXPIRATION_PERIOD));
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return PROPERTY_NAMES;
    }

    /**
     * This implementation implements thw HS256 strategy.
     * JWS: HS256
     * Algorithm: HMAC256
     * Description: HMAC with SHA-256
     */
    @Override
    public String authenticate(BaseConnectorConfiguration configuration) throws ConnectorSecurityException {
        try {
            long expirationLength = Long.parseLong(configuration.getProperty(CONNECTOR_BASE_AUTH_JWT_EXPIRATION_PERIOD));
            Date expirationDate = new Date(System.currentTimeMillis() + expirationLength);
            Algorithm algorithm = Algorithm.HMAC256(configuration.getProperty(CONNECTOR_BASE_AUTH_JWT_SECRET));
            Map<String,Object> headerClaims = new HashMap<>();
            headerClaims.put("alg", "HS256");
            headerClaims.put("typ", "JWT");

            return JWT.create()
                    .withHeader(headerClaims)
                    .withIssuer(configuration.getProperty(CONNECTOR_BASE_AUTH_JWT_ISSUER))
                    .withExpiresAt(expirationDate)
                    .sign(algorithm);

        } catch (JWTCreationException authE) {
            throw new ConnectorSecurityException("Unable to generate bearer token for authentication.", authE);
        }

    }
}
