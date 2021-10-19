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

import com.exclamationlabs.connid.base.connector.authenticator.util.OAuth2TokenExecution;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2PasswordConfiguration;
import com.google.gson.GsonBuilder;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.io.IOException;
import java.util.*;

/**
 * This implementation performs the OAuth2 "password" grant type.
 */
public class OAuth2TokenPasswordAuthenticator implements Authenticator<Oauth2PasswordConfiguration> {

    protected static GsonBuilder gsonBuilder;

    static {
        gsonBuilder = new GsonBuilder();
    }

    @Override
    public String authenticate(Oauth2PasswordConfiguration configuration) throws ConnectorSecurityException {
        OAuth2TokenExecution.initializeForHttp();
        HttpClient client = createClient();

        try {
            HttpPost request = new HttpPost(configuration.getTokenUrl());
            List<NameValuePair> form = new ArrayList<>();
            form.add(new BasicNameValuePair("grant_type", "password"));
            form.add(new BasicNameValuePair("username", configuration.getUsername()));
            form.add(new BasicNameValuePair("password", configuration.getPassword()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " +
                    configuration.getEncodedSecret());
            return OAuth2TokenExecution.executeRequest(this, configuration, client, request, entity, gsonBuilder);

        } catch (IOException e) {
            throw new ConnectorSecurityException(
                    "Unexpected error occurred during OAuth2 call: " + e.getMessage(), e);
        }

    }

    protected HttpClient createClient() {
        return HttpClients.createDefault();
    }

}
