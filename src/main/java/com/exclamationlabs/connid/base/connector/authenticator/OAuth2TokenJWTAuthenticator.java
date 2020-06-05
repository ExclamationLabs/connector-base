package com.exclamationlabs.connid.base.connector.authenticator;

import com.exclamationlabs.connid.base.connector.authenticator.model.OAuth2AccessTokenContainer;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
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

import static com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty.*;

public class OAuth2TokenJWTAuthenticator extends AbstractOAuth2TokenAuthenticator {
    private static final Set<ConnectorProperty> PROPERTY_NAMES;

    protected static GsonBuilder gsonBuilder;

    static {
        PROPERTY_NAMES = new HashSet<>(Collections.singletonList(
                CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL));

        gsonBuilder = new GsonBuilder();
    }

    private final Authenticator jwtAuthenticator;

    public OAuth2TokenJWTAuthenticator(Authenticator inputJwtAuthenticator) {
        jwtAuthenticator = inputJwtAuthenticator;
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public String authenticate(BaseConnectorConfiguration configuration) throws ConnectorSecurityException {
        String bearerToken = jwtAuthenticator.authenticate(configuration);

        HttpClient client = createClient();

        try {
            HttpPost request = new HttpPost(configuration.getProperty(CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL));
            List<NameValuePair> form = new ArrayList<>();
            form.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
            form.add(new BasicNameValuePair("assertion", bearerToken));
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
