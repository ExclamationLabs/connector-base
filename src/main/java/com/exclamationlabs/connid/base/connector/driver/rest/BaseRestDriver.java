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

package com.exclamationlabs.connid.base.connector.driver.rest;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TrustStoreConfiguration;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
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
import java.util.Map;

/**
 * Abstract class for drivers that need to make calls to RESTful web services
 * to manage user and group information.
 */
public abstract class BaseRestDriver<U extends UserIdentityModel, G extends GroupIdentityModel> implements Driver<U,G> {

    private static final Log LOG = Log.getLog(BaseRestDriver.class);

    protected static GsonBuilder gsonBuilder;

    protected ConnectorConfiguration configuration;
    protected Authenticator authenticator;

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
     */
    abstract protected RestFaultProcessor getFaultProcessor();

    /**
     * Normal HTTP Client.
     * If need arises for a secure HTTPS Client backed by a cert/keystore, etc.
     * such as with FIS, a separate utility class should be added to possibly provide
     * that support (look at FIS codebase for an example).
     */
    protected HttpClient createClient() {
        return HttpClients.createDefault();
    }

    /**
     * Override this and return true if the RESTful services you are calling
     * require Authorization: Bearer [token] in the HTTP header. If true,
     * this header will be added to all requests, with the token coming from
     * value stored in ConnectorConfiguration getCredentialAccessToken().
     */
    protected boolean usesBearerAuthorization() {
        return false;
    }

    /**
     * Override this and return true if the RESTful services you are calling
     * require Authorization: Token token=[token] in the HTTP header. If true,
     * this header will be added to all requests, with the token coming from
     * value stored in ConnectorConfiguration getCredentialAccessToken().
     */
    protected boolean usesTokenAuthorization() {
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

    public <T>T executeRequest(HttpRequestBase request, Class<T> returnType) {
        if (gsonBuilder == null || getFaultProcessor() == null || configuration == null) {
            throw new ConnectionBrokenException("Connection invalidated or disposed, request cannot " +
                    "be performed.  Gsonbuilder: " + gsonBuilder + "; faultProcessor: " +
                    getFaultProcessor() + "; configuration: " + configuration);
        }

        HttpClient client = createClient();
        HttpResponse response;

        try {
            LOG.info("Request details: {0} to {1}", request.getMethod(),
                    request.getURI());
            response = client.execute(request);
            LOG.info("Received {0} response for {1} {2}", response.getStatusLine().getStatusCode(),
                    request.getMethod(), request.getURI());

            int statusCode = response.getStatusLine().getStatusCode();
            LOG.info("Response status code is {0}", statusCode);
            if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                LOG.info("request execution failed; status code is {0}", statusCode);
                getFaultProcessor().process(response, gsonBuilder);
            }

        } catch (ClientProtocolException e) {
            throw new ConnectorException(
                    "Unexpected ClientProtocolException occurred while attempting call: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ConnectorException(
                    "Unexpected IOException occurred while attempting call: " + e.getMessage(), e);
        }

        return interpretResponse(response, returnType);
    }

    protected <T>T executeGetRequest(String restUri, Class<T> expectedResponseType) {
        return executeGetRequest(restUri, expectedResponseType, null);
    }

    protected <T>T executePostRequest(String restUri, Class<T> expectedResponseType, Object requestBody) {
        return executePostRequest(restUri, expectedResponseType, requestBody, null);
    }

    protected <T>T executePutRequest(String restUri, Class<T> expectedResponseType, Object requestBody) {
        return executePutRequest(restUri, expectedResponseType, requestBody, null);
    }

    protected <T>T executePatchRequest(String restUri, Class<T> expectedResponseType, Object requestBody) {
        return executePatchRequest(restUri, expectedResponseType, requestBody, null);
    }

    protected <T>T executeDeleteRequest(String restUri, Class<T> expectedResponseType) {
        return executeDeleteRequest(restUri, expectedResponseType, null);
    }

    protected <T>T executeGetRequest(String restUri, Class<T> expectedResponseType,
                                     Map<String, String> additionalHeaders) {
        HttpGet get = createGetRequest(restUri);
        setAdditionalHeaders(get, additionalHeaders);
        return executeRequest(get, expectedResponseType);
    }

    protected <T>T executePostRequest(String restUri, Class<T> expectedResponseType, Object requestBody,
                                      Map<String, String> additionalHeaders) {
        HttpPost post = createPostRequest(restUri, requestBody);
        setAdditionalHeaders(post, additionalHeaders);
        return executeRequest(post, expectedResponseType);
    }

    protected <T>T executePutRequest(String restUri, Class<T> expectedResponseType, Object requestBody,
                                     Map<String, String> additionalHeaders) {
        HttpPut put = createPutRequest(restUri, requestBody);
        setAdditionalHeaders(put, additionalHeaders);
        return executeRequest(put, expectedResponseType);
    }

    protected <T>T executePatchRequest(String restUri, Class<T> expectedResponseType, Object requestBody,
                                       Map<String, String> additionalHeaders) {
        HttpPatch patch = createPatchRequest(restUri, requestBody);
        setAdditionalHeaders(patch, additionalHeaders);
        return executeRequest(patch, expectedResponseType);
    }

    protected <T>T executeDeleteRequest(String restUri, Class<T> expectedResponseType,
                                        Map<String, String> additionalHeaders) {
        HttpDelete delete = createDeleteRequest(restUri);
        setAdditionalHeaders(delete, additionalHeaders);
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
        else if (usesTokenAuthorization()) {
            request.setHeader(HttpHeaders.AUTHORIZATION, "Token token=" +
                    configuration.getCredentialAccessToken());
        }

    }

    private void setAdditionalHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(request::addHeader);
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

    private <T>T interpretResponse(HttpResponse response, Class<T> returnType) {
        T result;
        String rawJson;

        try {
            if (returnType == null) {
                LOG.info("No response expected or needed from this invocation, returning null type");
                return null;
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                rawJson = EntityUtils.toString(response.getEntity(), Charsets.UTF_8.name());
                LOG.info("Received raw JSON: {0}", rawJson);
            } else {
                LOG.info("Received HTTP Not Found for call, returning empty response type");
                return null;
            }
        } catch (ParseException pe) {
            throw new ConnectorException(
                    "ParseException while reading raw JSON body from response: " + pe.getMessage(), pe);
        } catch (IOException ioe) {
            throw new ConnectorException(
                    "IOException while reading raw JSON body from response:" + ioe.getMessage(), ioe);
        }

        Gson gson = gsonBuilder.create();
        try {
            result = gson.fromJson(rawJson, returnType);
            LOG.info("Successfully populated model type {0} from JSON response body", returnType.getName());
        } catch (JsonSyntaxException jse) {
            throw new ConnectorException("JSON syntax error occurred while trying to interpret JSON response " +
                    "into type " + returnType.getName(), jse);
        }

        return result;
    }

}
