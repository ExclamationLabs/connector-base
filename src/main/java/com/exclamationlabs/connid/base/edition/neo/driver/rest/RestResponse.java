package com.exclamationlabs.connid.base.edition.neo.driver.rest;

import java.net.http.HttpHeaders;
import lombok.Getter;

/**
 * Wrapper class for the response from a REST invocation. This is used to contain the headers,
 * status code, and body if present.
 *
 * @param <T> The object type that the JSON response body should be serialized to. This does not
 *     necessarily apply to HTTP 400/500 error conditions.
 */
@Getter
public class RestResponse<T> {

  protected HttpHeaders headers;
  protected int statusCode;
  protected T responseBody;

  private RestResponse() {}
  ; // Enforce usage of parameterized constructor

  /**
   * Constructor for a REST response.
   *
   * @param headers The HTTP response headers returned from RestClient invocation.
   * @param statusCode The HTTP status code returned from RestClient invocation.
   * @param responseBody The body of the response, if present. This is typically a JSON object that
   *     will be deserialized to the type specified by T.
   */
  public RestResponse(HttpHeaders headers, int statusCode, T responseBody) {
    this.headers = headers;
    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }
}
