package com.exclamationlabs.connid.base.connector.driver.rest;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectionBrokenException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public abstract class BaseRestDriver<U extends UserIdentityModel, G extends GroupIdentityModel> implements Driver<U,G> {

    private static final Log LOG = Log.getLog(BaseRestDriver.class);

    protected static GsonBuilder gsonBuilder;

    protected ConnectorConfiguration configuration;
    protected Authenticator authenticator;

    protected String baseServiceUrl;

    @Override
    public void initialize(BaseConnectorConfiguration config, Authenticator auth)
            throws ConnectorException {
        TrustStoreConfiguration.clearJdkProperties();
        authenticator = auth;
        configuration = config;
        gsonBuilder = new GsonBuilder();
    }

    /**
     * Return the fault processor that will be used to analyze and respond
     * to HTTP error responses.
     * @return
     */
    abstract protected RestFaultProcessor getFaultProcessor();

    /**
     * Normal HTTP Client.
     * If need arises for a secure HTTPS Client backed by a cert/keystore, etc.
     * such as with FIS, a separate utility class should be added to possibly provide
     * that support (look at FIS codebase for an example).
     * @return
     */
    protected HttpClient createClient() {
        return HttpClients.createDefault();
    }

    /**
     * Override this and return true if the RESTful services you are calling
     * require Authorization: Bearer [token] in the HTTP header. If true,
     * this header will be added to all requests, with the token coming from
     * value stored in ConnectorConfiguration getCredentialAccessToken().
     * @return
     */
    protected boolean usesBearerAuthorization() {
        return false;
    }

    /**
     * This method should return the base Service URL
     * for RESTful endpoints that this driver invokes.
     * If not used and full URL needs to be fully reconstructed for every diffect
     * RESTful API call, have this method return empty string (not null).
     * @return Empty string or String containing a beginning portion of the RESTful
     * URL that will need to be invoked
     */
    abstract protected String getBaseServiceUrl();

    /**
     * If the RESTful service returns a JSON response type that is different than and not
     * embedded in normal service response types, supply the JSON-serializable data type
     * here.
     *
     * If not overriden, then this will remain null and any error handling will be left up
     * to HTTP response codes, or you'll need to read error response body on your own (if you
     * expect it to be embedded within normal JSON types.
     */
    protected <T> Class<T> getErrorResponseType() {
        return null;
    }


    public <T>T executeRequest(HttpRequestBase request, Class<T> returnType) {
        if (gsonBuilder == null || getFaultProcessor() == null || configuration == null) {
            throw new ConnectionBrokenException("Connection invalidated or disposed, request cannot " +
                    "be performed.  Gsonbuilder: " + gsonBuilder + "; faultProcessor: " +
                    getFaultProcessor() + "; configuration: " + configuration);
        }

        HttpClient client = createClient();
        HttpResponse response;
        T result = null;

        try {

            LOG.info("Request details: {0} to {1}", request.getMethod(),
                    request.getURI());
            response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            LOG.info("Response status code is {0}", statusCode);

            if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                LOG.info("request execution failed; status code is {0}", statusCode);
                getFaultProcessor().process(response, gsonBuilder);
            }

            // TODO: improve exception handling of response.getEntity()
            // and gson.fromJson and handful empty responses and other conditions more gracefully
            if (returnType != null && statusCode != HttpStatus.SC_NOT_FOUND) {
                String rawJson = EntityUtils.toString(response.getEntity(), Charsets.UTF_8.name());
                LOG.info("Received {0} response for {1} {2}, raw JSON: {3}", statusCode,
                        request.getMethod(), request.getURI(), rawJson);

                Gson gson = gsonBuilder.create();
                result = gson.fromJson(rawJson, returnType);
            }

        } catch (ClientProtocolException e) {
            throw new ConnectorException(
                    "Unexpected ClientProtocolException occurred while attempting call: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ConnectorException(
                    "Unexpected IOException occurred while attempting call: " + e.getMessage(), e);
        }

        return result;
    }

    protected <T>T executeGetRequest(String restUri, Class<T> expectedResponseType) {
        HttpGet get = createGetRequest(restUri);
        return executeRequest(get, expectedResponseType);
    }

    protected <T>T executePostRequest(String restUri, Class<T> expectedResponseType, Object requestBody) {
        HttpPost post = createPostRequest(restUri, requestBody);
        return executeRequest(post, expectedResponseType);
    }

    protected <T>T executePutRequest(String restUri, Class<T> expectedResponseType, Object requestBody) {
        HttpPut post = createPutRequest(restUri, requestBody);
        return executeRequest(post, expectedResponseType);
    }

    protected <T>T executePatchRequest(String restUri, Class<T> expectedResponseType, Object requestBody) {
        HttpPatch post = createPatchRequest(restUri, requestBody);
        return executeRequest(post, expectedResponseType);
    }

    protected <T>T executeDeleteRequest(String restUri, Class<T> expectedResponseType) {
        HttpDelete delete = createDeleteRequest(restUri);
        return executeRequest(delete, expectedResponseType);
    }

    private HttpGet createGetRequest(String restUri) {
        HttpGet request = new HttpGet(getBaseServiceUrl() + restUri);
        prepareHeaders(request);
        return request;
    }

    private HttpDelete createDeleteRequest(String restUri) {
        HttpDelete request = new HttpDelete(getBaseServiceUrl() + restUri);
        prepareHeaders(request);
        return request;
    }

    private HttpPost createPostRequest(String restUri, Object requestBody) {
        HttpPost request = new HttpPost(getBaseServiceUrl() + restUri);
        prepareHeaders(request);
        setupJsonRequestBody(request, requestBody);
        return request;
    }

    private HttpPut createPutRequest(String restUri, Object requestBody) {
        HttpPut request = new HttpPut(getBaseServiceUrl() + restUri);
        prepareHeaders(request);
        setupJsonRequestBody(request, requestBody);
        return request;
    }

    private HttpPatch createPatchRequest(String restUri, Object requestBody) {
        HttpPatch request = new HttpPatch(getBaseServiceUrl() + restUri);
        prepareHeaders(request);
        setupJsonRequestBody(request, requestBody);
        return request;
    }

    private void prepareHeaders(HttpRequestBase request) {
        // Normally, RESTful services only transmit JSON
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        if (usesBearerAuthorization()) {
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " +
                    configuration.getCredentialAccessToken());
        }

    }

    private void setupJsonRequestBody(HttpEntityEnclosingRequestBase request, Object requestBody) {
        if (requestBody != null) {
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(requestBody);
            LOG.info("JSON formatted request for {0}: {1}", requestBody.getClass().getName(), json);
            try {
                request.setEntity(new StringEntity(json));
            } catch (UnsupportedEncodingException e) {
                throw new ConnectorException("Request body encoding failed for data: " + json, e);
            }
        }
    }

}
