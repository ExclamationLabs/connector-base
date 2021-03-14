package com.exclamationlabs.connid.base.connector.driver.rest;

import org.apache.http.Header;

public class RestResponseData<T> {

    private final T responseObject;
    private final Header[] responseHeaders;
    private final int responseStatusCode;

    public RestResponseData(T dataObject, Header[] headers, int code) {
        responseObject = dataObject;
        responseHeaders = headers;
        responseStatusCode = code;
    }

    public T getResponseObject() {
        return responseObject;
    }

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public int getResponseStatusCode() {
        return responseStatusCode;
    }
}
