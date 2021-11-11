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
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.TrustStoreConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.RestConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.HttpBasicAuthConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.ProxyConfiguration;
import com.exclamationlabs.connid.base.connector.driver.BaseDriver;
import com.exclamationlabs.connid.base.connector.driver.exception.DriverRenewableTokenExpiredException;
import com.exclamationlabs.connid.base.connector.driver.exception.DriverTokenExpiredException;
import com.exclamationlabs.connid.base.connector.driver.rest.util.CustomConnectionSocketFactory;
import com.exclamationlabs.connid.base.connector.driver.rest.util.HttpDeleteWithBody;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectionBrokenException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Abstract class for drivers that need to make calls to RESTful web services
 * to manage user and group information.
 */
public abstract class BaseRestDriver<U extends ConnectorConfiguration> extends BaseDriver<U> {

    private static final Log LOG = Log.getLog(BaseRestDriver.class);

    protected static GsonBuilder gsonBuilder;

    protected U configuration;
    protected Authenticator<U> authenticator;

    protected HttpClientContext socksProxyClientContext;

    @Override
    public void initialize(U config, Authenticator<U> auth)
            throws ConnectorException {
        TrustStoreConfiguration.clearJdkProperties();
        authenticator = auth;
        if (!(config instanceof RestConfiguration)) {
            throw new ConnectorException("RestConfiguration not setup for connector");
        }
        configuration = config;
        gsonBuilder = new GsonBuilder();
    }

    /**
     * Return the fault processor that will be used to analyze and respond
     * to HTTP error responses.
     */
    abstract protected RestFaultProcessor getFaultProcessor();

    /**
     * Setup HTTP Client.
     *
     * If configuration implements HttpBasicAuthConfiguration, a Client
     * with HTTP Basic Authentication support with configured username and password will be constructed.
     *
     * If configuration implements ProxyConfiguration, a Client
     * with Proxy connection information will be established.  Supported
     * proxy types are 'socks5' and 'http'.
     *
     */
    protected HttpClient createClient() {
        boolean usesHttpBasicAuth = getConfiguration() instanceof HttpBasicAuthConfiguration;
        boolean usesProxy = getConfiguration() instanceof ProxyConfiguration;
        boolean socksProxy = false;
        PoolingHttpClientConnectionManager socksProxyConnectionManager = null;
        DefaultProxyRoutePlanner httpProxyRoutePlanner = null;
        CredentialsProvider basicAuthProvider = null;
        if (usesProxy) {
            ProxyConfiguration proxyConfiguration = (ProxyConfiguration) getConfiguration();
            socksProxy = StringUtils.equalsIgnoreCase("socks", proxyConfiguration.getProxyType());
            if (socksProxy) {
                socksProxyConnectionManager = setupSocksProxyConnectionManager(proxyConfiguration);
                socksProxyClientContext = setupSocksProxyContext(proxyConfiguration);
            } else {
                httpProxyRoutePlanner = setupHttpProxyRouteManager(proxyConfiguration);
            }
        }
        if (usesHttpBasicAuth) {
            basicAuthProvider = setupBasicAuth((HttpBasicAuthConfiguration) getConfiguration());
        }

        if (usesHttpBasicAuth) {
            if (usesProxy) {
                if (socksProxy) {
                    return HttpClients.custom()
                            .setConnectionManager(socksProxyConnectionManager)
                            .setDefaultCredentialsProvider(basicAuthProvider)
                            .build();
                } else {
                    return HttpClients.custom()
                            .setRoutePlanner(httpProxyRoutePlanner)
                            .setDefaultCredentialsProvider(basicAuthProvider)
                            .build();
                }
            } else {
                return HttpClients.custom()
                        .setDefaultCredentialsProvider(basicAuthProvider)
                        .build();
            }

        } else {
            if (usesProxy) {
                if (socksProxy) {
                    return HttpClients.custom()
                            .setConnectionManager(socksProxyConnectionManager)
                            .build();
                } else {
                    return HttpClients.custom()
                            .setRoutePlanner(httpProxyRoutePlanner)
                            .build();
                }
            } else {
                return HttpClients.custom()
                        .build();
            }
        }
    }

    protected DefaultProxyRoutePlanner setupHttpProxyRouteManager(ProxyConfiguration configuration) {
        HttpHost proxyHost = new HttpHost(configuration.getProxyHost(),
                configuration.getProxyPort());
        return new DefaultProxyRoutePlanner(proxyHost);
    }

    protected PoolingHttpClientConnectionManager setupSocksProxyConnectionManager(ProxyConfiguration configuration) {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new CustomConnectionSocketFactory(SSLContexts.createSystemDefault()))
                .build();
        return new PoolingHttpClientConnectionManager(reg);
    }

    protected HttpClientContext setupSocksProxyContext(ProxyConfiguration configuration) {
        InetSocketAddress socksAddr = new InetSocketAddress(configuration.getProxyHost(),
                configuration.getProxyPort());
        HttpClientContext context = HttpClientContext.create();
        context.setAttribute("socks.address", socksAddr);
        return context;
    }

    protected CredentialsProvider setupBasicAuth(HttpBasicAuthConfiguration configuration) {
        CredentialsProvider basicAuthProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(
                configuration.getBasicUsername(),
                configuration.getBasicPassword());
        basicAuthProvider.setCredentials(AuthScope.ANY, credentials);
        return basicAuthProvider;
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
     * Override this method if needed to perform a custom action after
     * a new token has been successfully obtained for an expired security token,
     * and before the valid response of the original request is returned.
     */
    protected void performPostNewAccessTokenCustomAction() {
    }

    /**
     * This method should return the base Service URL
     * for RESTful endpoints that this driver invokes.
     * If not used and full URL needs to be fully reconstructed for every different
     * RESTful API call, have this method return empty string (not null).
     * @return Empty string or String containing a beginning portion of the RESTful
     * URL that will need to be invoked
     */
    abstract protected String getBaseServiceUrl();

    public <T> RestResponseData<T> executeRequest(HttpRequestBase request, Class<T> returnType) {
        return executeRequest(request, returnType, false, 0);
    }

    public <T> RestResponseData<T> executeRequest(HttpRequestBase request, Class<T> returnType, boolean isRetry,
        int retryCount) {
        if (gsonBuilder == null || getFaultProcessor() == null || configuration == null) {
            throw new ConnectionBrokenException("Connection invalidated or disposed, request cannot " +
                    "be performed.  Gsonbuilder: " + gsonBuilder + "; faultProcessor: " +
                    getFaultProcessor() + "; configuration: " + configuration);
        }

        HttpClient client = createClient();
        HttpResponse response;
        int responseStatusCode;
        Header[] responseHeaders;

        try {
            LOG.ok("Request details: {0} to {1}", request.getMethod(),
                    request.getURI());
            if (socksProxyClientContext != null) {
                response = client.execute(request, socksProxyClientContext);
            } else {
                response = client.execute(request);
            }

            LOG.ok("Received {0} response for {1} {2}", response.getStatusLine().getStatusCode(),
                    request.getMethod(), request.getURI());

            responseStatusCode = response.getStatusLine().getStatusCode();
            responseHeaders = response.getAllHeaders();

            LOG.ok("Response status code is {0}", responseStatusCode);
            if (responseStatusCode >= HttpStatus.SC_BAD_REQUEST) {
                LOG.ok("request execution failed; status code is {0}", responseStatusCode);
                getFaultProcessor().process(response, gsonBuilder);
            }

        } catch (DriverRenewableTokenExpiredException retryE) {
          if (isRetry) {
              LOG.error("Driver {0} token {1} still invalid after re-authentication, should investigate", this.getClass().getSimpleName(),
                      configuration.getCurrentToken());
              throw new ConnectorException("Service rejected re-authenticated token for driver", retryE);
          } else {
              LOG.info("Driver {0} encountered token expiration and will attempt to reauthenticate.", this.getClass().getSimpleName());
              configuration.setCurrentToken(authenticator.authenticate(configuration));
              LOG.info("Driver {0} acquired a new access token and will re-attempt original driver request once.", this.getClass().getSimpleName());
              prepareHeaders(request);
              RestResponseData<T> holdResult = executeRequest(request, returnType, true , 1);
              performPostNewAccessTokenCustomAction();
              return holdResult;
          }
        } catch (DriverTokenExpiredException tokenE) {
            LOG.info("Driver {0} token {1} is now invalid and will not retry.", this.getClass().getSimpleName(),
                    configuration.getCurrentToken());
            throw new ConnectorException("Token expired or rejected during driver usage", tokenE);
        } catch (ClientProtocolException e) {
            throw new ConnectorException(
                    "Unexpected ClientProtocolException occurred while attempting call: " + e.getMessage(), e);
        } catch (IOException e) {
            if (getIoErrorRetryCount() > 0) {
                if (isRetry) {
                    if (retryCount < getIoErrorRetryCount()) {
                        return executeRequest(request, returnType, true, ++retryCount);
                    } else {
                        throw new ConnectorException(
                                "Unexpected IOException occurred while attempting call: " + e.getMessage() +
                                ".  " + retryCount + " retries were attempted.", e);
                    }
                } else {
                    return executeRequest(request, returnType, true, 1);
                }
            } else {
                throw new ConnectorException(
                        "Unexpected IOException occurred while attempting call: " + e.getMessage(), e);
            }
        }

        T responseData = interpretResponse(response, returnType);
        return new RestResponseData<>(responseData, responseHeaders, responseStatusCode);
    }

    public <T> RestResponseData<T> executeGetRequest(String restUri, Class<T> expectedResponseType) {
        return executeGetRequest(restUri, expectedResponseType, null);
    }

    public <T> RestResponseData<T> executePostRequest(String restUri, Class<T> expectedResponseType, Object requestBody) {
        return executePostRequest(restUri, expectedResponseType, requestBody, null);
    }

    public <T> RestResponseData<T> executePutRequest(String restUri, Class<T> expectedResponseType, Object requestBody) {
        return executePutRequest(restUri, expectedResponseType, requestBody, null);
    }

    public <T> RestResponseData<T> executePatchRequest(String restUri, Class<T> expectedResponseType, Object requestBody) {
        return executePatchRequest(restUri, expectedResponseType, requestBody, null);
    }

    public <T> RestResponseData<T> executeDeleteRequest(String restUri, Class<T> expectedResponseType) {
        return executeDeleteRequest(restUri, expectedResponseType, null);
    }

    public <T> RestResponseData<T> executeDeleteRequest(String restUri, Class<T> expectedResponseType, Object requestBody) {
        return executeDeleteRequest(restUri, expectedResponseType, requestBody, null);
    }

    public <T> RestResponseData<T> executeGetRequest(String restUri, Class<T> expectedResponseType,
                                     Map<String, String> additionalHeaders) {
        HttpGet get = createGetRequest(restUri);
        setAdditionalHeaders(get, additionalHeaders);
        return executeRequest(get, expectedResponseType);
    }

    public <T> RestResponseData<T> executePostRequest(String restUri, Class<T> expectedResponseType, Object requestBody,
                                      Map<String, String> additionalHeaders) {
        HttpPost post = createPostRequest(restUri, requestBody);
        setAdditionalHeaders(post, additionalHeaders);
        return executeRequest(post, expectedResponseType);
    }

    public <T> RestResponseData<T> executePutRequest(String restUri, Class<T> expectedResponseType, Object requestBody,
                                     Map<String, String> additionalHeaders) {
        HttpPut put = createPutRequest(restUri, requestBody);
        setAdditionalHeaders(put, additionalHeaders);
        return executeRequest(put, expectedResponseType);
    }

    public <T> RestResponseData<T> executePatchRequest(String restUri, Class<T> expectedResponseType, Object requestBody,
                                       Map<String, String> additionalHeaders) {
        HttpPatch patch = createPatchRequest(restUri, requestBody);
        setAdditionalHeaders(patch, additionalHeaders);
        return executeRequest(patch, expectedResponseType);
    }

    public <T> RestResponseData<T> executeDeleteRequest(String restUri, Class<T> expectedResponseType,
                                        Map<String, String> additionalHeaders) {
        HttpDelete delete = createDeleteRequest(restUri);
        setAdditionalHeaders(delete, additionalHeaders);
        return executeRequest(delete, expectedResponseType);
    }

    public <T> RestResponseData<T> executeDeleteRequest(String restUri, Class<T> expectedResponseType,
                                                Object requestBody, Map<String, String> additionalHeaders) {
        HttpDeleteWithBody delete = createDeleteRequest(restUri, requestBody);
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

    private HttpDeleteWithBody createDeleteRequest(String restUri, Object requestBody) {
        HttpDeleteWithBody request = new HttpDeleteWithBody(getBaseServiceUrl() + restUri);
        prepareHeaders(request);
        setupJsonRequestBody(request, requestBody);
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
                    configuration.getCurrentToken());
        }
        else if (usesTokenAuthorization()) {
            request.setHeader(HttpHeaders.AUTHORIZATION, "Token token=" +
                    configuration.getCurrentToken());
        }

    }

    private void setAdditionalHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(request::addHeader);
        }
    }

    protected final void setupJsonRequestBody(HttpEntityEnclosingRequestBase request, Object requestBody) {
        if (requestBody != null) {
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(requestBody);
            LOG.ok("JSON formatted request for {0}: {1}", requestBody.getClass().getName(), json);
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
                LOG.ok("No response expected or needed from this invocation, returning null type");
                return null;
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                rawJson = EntityUtils.toString(response.getEntity(), Charsets.UTF_8.name());
                LOG.ok("Received raw JSON: {0}", rawJson);
            } else {
                LOG.ok("Received HTTP Not Found for call, returning empty response type");
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

    private int getIoErrorRetryCount() {
        return ( (RestConfiguration) configuration).getIoErrorRetries();
    }

    public U getConfiguration() {
        return configuration;
    }

}
