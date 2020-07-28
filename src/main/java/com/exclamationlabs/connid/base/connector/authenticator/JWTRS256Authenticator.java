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

package com.exclamationlabs.connid.base.connector.authenticator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.security.interfaces.RSAPrivateKey;
import java.util.*;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

/**
 * This JWT authenticator implementation performs the HS256 strategy.
 *
 * Private key is required. This implementation does not utilize a
 * public key since only the private key is needed for signing.
 *
 * Since a private key is required, this Authenticator must be subclassed
 * and getPrivateKey() overriden to get access to the required private key.
 *
 * JWS: RS256
 * Algorithm: RSA256
 * Description: RSASSA-PKCS1-v1_5 with SHA-256
 */
public abstract class JWTRS256Authenticator extends JWTAuthenticator {

    /**
     * NOTE: SUBJECT AND AUDIENCE must be supplied as configuration properties,
     * but if left blank they will not be included in the invocation.
     */
    private static final Set<ConnectorProperty> PROPERTY_NAMES;

    static {
        PROPERTY_NAMES = new HashSet<>(Arrays.asList(CONNECTOR_BASE_AUTH_JWT_ISSUER,
                CONNECTOR_BASE_AUTH_JWT_EXPIRATION_PERIOD,
                CONNECTOR_BASE_AUTH_JWT_SUBJECT,
                CONNECTOR_BASE_AUTH_JWT_AUDIENCE,
                CONNECTOR_BASE_AUTH_JWT_USE_ISSUED_AT));
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        Set<ConnectorProperty> names = new HashSet<>();
        if (getPrivateKeyLoaderPropertyNames() != null) {
            names.addAll(getPrivateKeyLoaderPropertyNames());
        }
        names.addAll(PROPERTY_NAMES);
        return names;
    }

    protected abstract RSAPrivateKey getPrivateKey();

    protected abstract Set<ConnectorProperty> getPrivateKeyLoaderPropertyNames();

    @Override
    public String authenticate(ConnectorConfiguration configuration) throws ConnectorSecurityException {
        try {
            long expirationLength = Long.parseLong(configuration.getProperty(CONNECTOR_BASE_AUTH_JWT_EXPIRATION_PERIOD));
            Date expirationDate = new Date(System.currentTimeMillis() + expirationLength);
            Map<String,Object> headerClaims = new HashMap<>();
            headerClaims.put("alg", "RS256");
            headerClaims.put("typ", "JWT");

            Algorithm algorithm = Algorithm.RSA256(null, getPrivateKey());

            JWTCreator.Builder builder = JWT.create()
                    .withHeader(headerClaims)
                    .withIssuer(configuration.getProperty(CONNECTOR_BASE_AUTH_JWT_ISSUER))
                    .withExpiresAt(expirationDate);

            if (StringUtils.isNotBlank(configuration.getProperty(CONNECTOR_BASE_AUTH_JWT_SUBJECT))) {
                builder = builder.withSubject(configuration.getProperty(CONNECTOR_BASE_AUTH_JWT_SUBJECT));
            }
            if (StringUtils.isNotBlank(configuration.getProperty(CONNECTOR_BASE_AUTH_JWT_AUDIENCE))) {
                builder = builder.withAudience(configuration.getProperty(CONNECTOR_BASE_AUTH_JWT_AUDIENCE));
            }
            if (StringUtils.equalsIgnoreCase("Y", configuration.getProperty(CONNECTOR_BASE_AUTH_JWT_USE_ISSUED_AT))) {
                builder.withIssuedAt(new Date());
            }

            if (configuration.getExtraJWTClaimData() != null && (!configuration.getExtraJWTClaimData().isEmpty())) {
                configuration.getExtraJWTClaimData().forEach(builder::withClaim);
            }

            return builder.sign(algorithm);

        } catch (JWTCreationException authE) {
            throw new ConnectorSecurityException("Unable to generate bearer token for authentication.", authE);
        }

    }
}
