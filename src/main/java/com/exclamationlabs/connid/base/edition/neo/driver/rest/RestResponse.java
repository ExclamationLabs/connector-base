package com.exclamationlabs.connid.base.edition.neo.driver.rest;

import lombok.Getter;

import java.net.http.HttpHeaders;
@Getter
public class RestResponse<T> {

    protected HttpHeaders headers;
    protected int statusCode;
    protected T responseBody;

    public RestResponse(HttpHeaders headers, int statusCode, T responseBody) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
