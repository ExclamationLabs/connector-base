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
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.JwtRs256Configuration;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.security.interfaces.RSAPrivateKey;
import java.util.*;

/**
 * This JWT authenticator implementation performs the RS256 strategy.
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
public abstract class JWTRS256Authenticator implements Authenticator<JwtRs256Configuration> {

    // NOTE: SUBJECT AND AUDIENCE must be supplied as configuration properties,
    // but if left blank they will not be included in the invocation.

    protected abstract RSAPrivateKey getPrivateKey();

    protected Map<String, String> getExtraClaimData() {
        return Collections.emptyMap();
    }

    @Override
    public String authenticate(JwtRs256Configuration configuration) throws ConnectorSecurityException {
        try {
            long expirationLength = configuration.getExpirationPeriod();
            Date expirationDate = new Date(System.currentTimeMillis() + expirationLength);
            Map<String,Object> headerClaims = new HashMap<>();
            headerClaims.put("alg", "RS256");
            headerClaims.put("typ", "JWT");

            Algorithm algorithm = Algorithm.RSA256(null, getPrivateKey());

            JWTCreator.Builder builder = JWT.create()
                    .withHeader(headerClaims)
                    .withIssuer(configuration.getIssuer())
                    .withExpiresAt(expirationDate);

            if (StringUtils.isNotBlank(configuration.getSubject())) {
                builder = builder.withSubject(configuration.getSubject());
            }
            if (StringUtils.isNotBlank(configuration.getAudience())) {
                builder = builder.withAudience(configuration.getAudience());
            }
            if (configuration.getUseIssuedAt()) {
                builder.withIssuedAt(new Date());
            }

            if (getExtraClaimData() != null && (!getExtraClaimData().isEmpty())) {
                getExtraClaimData().forEach(builder::withClaim);
            }

            return builder.sign(algorithm);

        } catch (JWTCreationException authE) {
            throw new ConnectorSecurityException("Unable to generate bearer token for authentication.", authE);
        }

    }
}
