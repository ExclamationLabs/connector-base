package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

public class OAuth2TokenClientCredentialsAuthenticator extends AbstractOAuth2TokenAuthenticator {

    private static final Set<ConnectorProperty> PROPERTY_NAMES;
    protected static GsonBuilder gsonBuilder;

    static {
        gsonBuilder = new GsonBuilder();
        PROPERTY_NAMES = new HashSet<>(Arrays.asList(
                CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL,
                CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_ID,
                CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_SECRET));
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public String authenticate(BaseConnectorConfiguration configuration) throws ConnectorSecurityException {
        try {
            HttpPost request = new HttpPost(configuration.getProperty(CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL));

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "client_credentials"));
            request.setEntity(new UrlEncodedFormEntity(params));

            String auth =
                    configuration.getProperty(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_ID) + ":" +
                            configuration.getProperty(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_SECRET);

            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.ISO_8859_1));
            String authHeader = "Basic " + new String(encodedAuth);
            request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
            return executeRequest(configuration, getHttpClient(), request,
                    new UrlEncodedFormEntity(params), gsonBuilder);

        } catch (IOException e) {
            throw new ConnectorSecurityException(
                    "Unexpected error occurred during OAuth2 call: " + e.getMessage(), e);
        }
    }

    protected HttpClient getHttpClient() {
        return HttpClients.createDefault();
    }
}