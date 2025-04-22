package com.exclamationlabs.connid.base.edition.neo.driver.rest;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.RestConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.HttpBasicAuthConfiguration;
import com.exclamationlabs.connid.base.connector.driver.exception.DriverDataNotFoundException;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.github.resilience4j.ratelimiter.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.*;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

public class RestClient<T extends RestConfiguration> {

    public static final String JSON_PATCH_JSON_CONTENT_TYPE = "application/json-patch+json";

    protected Authenticator<T> authenticator;
    protected T configuration;
    protected RestBehavior<T> behavior;

    private RestClient() {}

    public RestClient(T configuration, Authenticator<T> authenticator, RestBehavior<T> behavior) {
        this.authenticator = authenticator;
        this.configuration = configuration;
        this.behavior = behavior;
    }

    public <U> RestResponse<U> get(String uri, Class<U> responseType) {
        return get(uri, responseType, Collections.emptyMap());
    }

    public <U> RestResponse<U> get(String uri, Class<U> responseType, Map<String, String> additionalHeaders) {
        HttpRequest request = constructCoreRequest(uri, additionalHeaders).GET().build();
        return executeRequest(request, responseType);
    }

    public <U> RestResponse<U> post(String uri, Object requestBody, Class<U> responseType) {
        return post(uri, requestBody, responseType, Collections.emptyMap());
    }

    public <U> RestResponse<U> post(String uri, Object requestBody, Class<U> responseType, Map<String, String> additionalHeaders) {
        if (requestBody != null) {
            String requestBodyString = setupJsonRequestBody(requestBody);
            HttpRequest request = constructCoreRequest(uri, additionalHeaders).
                    POST(HttpRequest.BodyPublishers.ofString(requestBodyString)).build();
            return executeRequest(request, responseType);
        } else {
            HttpRequest request = constructCoreRequest(uri, additionalHeaders).
                    POST(HttpRequest.BodyPublishers.ofString("")).build();
            return executeRequest(request, responseType);
        }
    }

    public <U> RestResponse<U> put(String uri, Object requestBody, Class<U> responseType) {
        return put(uri, requestBody, responseType, Collections.emptyMap());
    }

    public <U> RestResponse<U> put(String uri, Object requestBody, Class<U> responseType, Map<String, String> additionalHeaders) {
        if (requestBody != null) {
            String requestBodyString = setupJsonRequestBody(requestBody);
            HttpRequest request = constructCoreRequest(uri, additionalHeaders).
                    PUT(HttpRequest.BodyPublishers.ofString(requestBodyString)).build();
            return executeRequest(request, responseType);
        } else {
            HttpRequest request = constructCoreRequest(uri, additionalHeaders).
                    PUT(HttpRequest.BodyPublishers.ofString("")).build();
            return executeRequest(request, responseType);
        }
    }

    public <U> RestResponse<U> patch(String uri, Object requestBody, Class<U> responseType) {
        return patch(uri, requestBody, responseType, Collections.emptyMap());
    }

    public <U> RestResponse<U> patch(String uri, Object requestBody, Class<U> responseType, Map<String, String> additionalHeaders) {
        Map<String,String> requestHeaders = new HashMap<>();
        requestHeaders.put("Content-Type", JSON_PATCH_JSON_CONTENT_TYPE);
        if (additionalHeaders != null) {
            requestHeaders.putAll(additionalHeaders);
        }
        if (requestBody != null) {
            String requestBodyString = setupJsonRequestBody(requestBody);
            HttpRequest request = constructCoreRequest(uri, requestHeaders).
                    method("PATCH", HttpRequest.BodyPublishers.ofString(requestBodyString)).build();
            return executeRequest(request, responseType);
        } else {
            HttpRequest request = constructCoreRequest(uri, requestHeaders).
                    method("PATCH", HttpRequest.BodyPublishers.ofString("{}")).build();
            return executeRequest(request, responseType);
        }
    }

    public <U> RestResponse<U> delete(String uri, Object requestBody, Class<U> responseType) {
        return delete(uri, requestBody, responseType, Collections.emptyMap());
    }

    public <U> RestResponse<U> delete(String uri, Object requestBody, Class<U> responseType, Map<String, String> additionalHeaders) {
        HttpRequest request = constructCoreRequest(uri, additionalHeaders).GET().build();
        return executeRequest(request, responseType);
    }

    protected <U> RestResponse<U> executeRequest(HttpRequest request, Class<U> responseType) {
        return executeRequest(request, responseType, 0);
    }


    protected String setupJsonRequestBody(Object requestBody) {
        String bodyData;
        if (requestBody instanceof String) {
            bodyData = requestBody.toString();
        } else {
            Gson gson;
            if (behavior.getSerializationExclusionStrategy(configuration) != null) {
                gson = new GsonBuilder()
                        .addSerializationExclusionStrategy(behavior.getSerializationExclusionStrategy(configuration))
                        .create();
            } else {
                gson = new GsonBuilder().create();
            }
            bodyData = gson.toJson(requestBody);
        }

        return bodyData;
    }

    protected <U> RestResponse<U> executeRequest(HttpRequest request, Class<U> responseType, int retryCount) {
        if (behavior.getRateLimiterConfig(configuration) != null) {
            RateLimiter limiter = RateLimiter.of(behavior.getName(), behavior.getRateLimiterConfig(configuration));
            RateLimiter.waitForPermission(limiter);
        }

        HttpClient client = createClient();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                if (response.statusCode() == SC_NO_CONTENT) {
                    return new RestResponse<>(response.headers(), response.statusCode(), null);
                } else {
                    // interpret response
                    U responseData = interpretResponse(response.body(), responseType);
                    return new RestResponse<>(response.headers(), response.statusCode(), responseData);
                }
            } else if (response.statusCode() == HTTP_UNAUTHORIZED && behavior.supportsReauthentication() && retryCount == 0) {
                String newToken = authenticator.authenticate(configuration);
                configuration.setCurrentToken(newToken);
                executeRequest(request, responseType, retryCount + 1);
            } else if (response.statusCode() == HTTP_NOT_FOUND && behavior.getFaultHandler() != null && behavior.getFaultHandler().supportsNotFound()) {
                behavior.getFaultHandler().process(configuration, request, response);
            } else if (response.statusCode() == HTTP_NOT_FOUND) {
                throw new DriverDataNotFoundException("Resource not found for URI: " + request.uri());
            } else if (behavior.getFaultHandler() != null) {
                // pass 400/500 series error to fault handler
                behavior.getFaultHandler().process(configuration, request, response);
            } else {
                // default pass 400/500 fault handling
                Logger.error(this, String.format("Error response code %d for %s URI %s.  Body: %s",
                        response.statusCode(), request.method(), request.uri(), response.body()));
                throw new ConnectorException(
                        String.format("Received response code %d for %s URI %s.",
                                response.statusCode(), request.method(), request.uri()));
            }

        } catch (IOException | InterruptedException e) {
            // Check retry
            if (configuration.getIoErrorRetries() != null && configuration.getIoErrorRetries() > 0 &&
                    retryCount < configuration.getIoErrorRetries()) {
                Logger.warn(this, String.format("Retrying request %s %s due to error: %s",
                        request.method(), request.uri(), e.getMessage()));
                return executeRequest(request, responseType, retryCount + 1);
            }
            throw new ConnectorException(String.format("IOException while executing %s %s request (retries executed: %d): %s",
                    request.method(), request.uri(), retryCount, e.getMessage()), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <U> U interpretResponse(String rawJson, Class<U> responseType) {
        U result;

        try {
            if (responseType == null || responseType == Void.class) {
                Logger.debug(
                        this, "No response expected or needed from this invocation, returning null type");
                return null;
            }

            if (responseType == String.class) {
                return (U) rawJson;
            }

            Gson gson;
            if (behavior.getDeserializationExclusionStrategy(configuration) != null) {
                gson =
                        new GsonBuilder()
                                .addDeserializationExclusionStrategy(
                                        behavior.getDeserializationExclusionStrategy(configuration))
                                .create();
            } else {
                gson = new GsonBuilder().create();
            }

            result = gson.fromJson(rawJson, responseType);
            Logger.debug(
                    this,
                    String.format(
                            "Successfully populated model type %s from JSON response body",
                            responseType.getName()));
            return result;
        } catch (JsonSyntaxException jse) {
            throw new ConnectorException(
                    String.format(
                            "JSON syntax error occurred while trying to interpret JSON response "
                                    + "into type %s.  Raw JSON: %s",
                            responseType == null ? "null" : responseType.getName(), rawJson),
                    jse);
        }
    }


    protected HttpRequest.Builder constructCoreRequest(String uri, Map<String, String> additionalHeadersForRequest) throws ConnectorException {
        final String REQUEST_URL = behavior.getBaseServiceUrl(configuration) + uri;
        URI requestUri;
        try {
            requestUri = new URI(REQUEST_URL);
        } catch (URISyntaxException e) {
            throw new ConnectorException("Invalid URI: " + REQUEST_URL, e);
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", behavior.getAcceptType());
        headers.put("Content-Type", behavior.getAcceptType());
        if (behavior.usesBearerAuthentication() && (!StringUtils.isNotBlank(configuration.getCurrentToken()))) {
            headers.put("Authorization", "Bearer " + configuration.getCurrentToken());
        }
        if (behavior.getAdditionalHeaders(configuration) != null && !behavior.getAdditionalHeaders(configuration).isEmpty()) {
            headers.putAll(behavior.getAdditionalHeaders(configuration));
        }
        if (additionalHeadersForRequest != null && !additionalHeadersForRequest.isEmpty()) {
            headers.putAll(additionalHeadersForRequest);
        }

        String[] flattenedHeaders = headers.entrySet().stream()
                .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                .toArray(String[]::new);
        try {
            return HttpRequest.newBuilder()
                    .uri(requestUri)
                    .headers(flattenedHeaders);
        } catch(IllegalArgumentException e) {
            throw new ConnectorException("Invalid Request URL: " + REQUEST_URL, e);
        }

    }


    protected HttpClient createClient() {
        if (configuration instanceof HttpBasicAuthConfiguration) {
            return setupBasicAuth((HttpBasicAuthConfiguration) configuration);
        } else {
            return HttpClient.newBuilder().build();
        }
    }

    protected HttpClient setupBasicAuth(HttpBasicAuthConfiguration configuration) {
        return HttpClient.newBuilder()
                .authenticator(new java.net.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(configuration.getBasicUsername(),
                                GuardedStringUtil.read(configuration.getBasicPassword()).toCharArray());
                    }
                }).build();
    }
}
