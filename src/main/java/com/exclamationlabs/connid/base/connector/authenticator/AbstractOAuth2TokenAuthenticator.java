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

import com.exclamationlabs.connid.base.connector.authenticator.model.OAuth2AccessTokenContainer;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.io.IOException;

public abstract class AbstractOAuth2TokenAuthenticator implements Authenticator {

    private static final Log LOG = Log.getLog(AbstractOAuth2TokenAuthenticator.class);

    protected static String executeRequest(BaseConnectorConfiguration configuration, HttpClient client, HttpPost request, UrlEncodedFormEntity entity, GsonBuilder gsonBuilder) throws IOException {
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        LOG.info("OAuth2 Received {0} response for {1} {2}", statusCode,
                request.getMethod(), request.getURI());
        String rawJson = EntityUtils.toString(response.getEntity(), Charsets.UTF_8.name());
        LOG.info("Received raw JSON: {0}", rawJson);
        if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
            throw new ConnectorSecurityException("OAuth2 assertion failed, status code is HTTP " +
                    statusCode);
        }

        OAuth2AccessTokenContainer authResponse = gsonBuilder.create().fromJson(rawJson,
                OAuth2AccessTokenContainer.class);

        if (authResponse != null && authResponse.getAccessToken() != null) {
            configuration.setOauth2Information(authResponse);
            return authResponse.getAccessToken();
        } else {
            throw new ConnectorSecurityException("Invalid/empty response received from OAuth2: " + rawJson);
        }
    }
}
