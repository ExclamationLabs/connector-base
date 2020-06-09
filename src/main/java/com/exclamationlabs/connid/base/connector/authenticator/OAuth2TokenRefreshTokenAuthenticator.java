package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.io.IOException;
import java.util.*;

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

public class OAuth2TokenRefreshTokenAuthenticator extends AbstractOAuth2TokenAuthenticator {

    private static final Set<ConnectorProperty> PROPERTY_NAMES;
    protected static GsonBuilder gsonBuilder;

    static {
        gsonBuilder = new GsonBuilder();
        PROPERTY_NAMES = new HashSet<>(Arrays.asList(
                CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL,
                CONNECTOR_BASE_AUTH_OAUTH2_REFRESH_TOKEN,
                CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_ID,
                CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_SECRET));
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public String authenticate(BaseConnectorConfiguration configuration) throws ConnectorSecurityException {
        HttpClient client = createClient();

        try {
            HttpPost request = new HttpPost(configuration.getProperty(CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL));
            List<NameValuePair> form = new ArrayList<>();
            form.add(new BasicNameValuePair("grant_type", "refresh_token"));
            form.add(new BasicNameValuePair("refresh_token", configuration.getProperty(CONNECTOR_BASE_AUTH_OAUTH2_REFRESH_TOKEN)));

            if (StringUtils.isNotBlank(configuration.getProperty(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_ID))) {
                form.add(new BasicNameValuePair("client_id", configuration.getProperty(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_ID)));
            }
            if (StringUtils.isNotBlank(configuration.getProperty(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_SECRET))) {
                form.add(new BasicNameValuePair("client_secret", configuration.getProperty(CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_SECRET)));
            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            return executeRequest(configuration, client, request, entity, gsonBuilder);

        } catch (IOException e) {
            throw new ConnectorSecurityException(
                    "Unexpected error occurred during OAuth2 call: " + e.getMessage(), e);
        }

    }

    protected HttpClient createClient() {
        return HttpClients.createDefault();
    }
}