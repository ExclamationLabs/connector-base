package com.exclamationlabs.connid.base.edition.neo.driver.rest;

import java.net.http.HttpHeaders;
import lombok.Getter;

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
