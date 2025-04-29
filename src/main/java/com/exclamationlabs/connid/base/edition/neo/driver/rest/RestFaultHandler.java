package com.exclamationlabs.connid.base.edition.neo.driver.rest;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

/**
 * Interface for handling REST faults. Implementations should inspect the request and response
 * information to handle the error accordingly. The HTTP status code from the response is available
 * using response.statusCode().
 *
 * @param <T> The type of connector configuration.
 */
public interface RestFaultHandler<T extends ConnectorConfiguration> {

  /**
   * Handle the fault.
   *
   * @param configuration Connector configuration object applicable to this connector.
   * @param request The HTTP request object.
   * @param response The HTTP response object. The HTTP status code from the response is available *
   *     using response.statusCode(). The raw JSON response body, if present, is available using
   *     response.body().
   * @throws ConnectorException Based on the nature of the error response and how the implementation
   *     chooses to handle it, a ConnectorException may be thrown.
   */
  void process(T configuration, HttpRequest request, HttpResponse<String> response)
      throws ConnectorException;

  /**
   * Indicate whether this fault handler receives and supports HTTP status code 404 Not Found
   * responses.
   *
   * @return True if the process() method will be invoked for 404 Not Found responses, false if it
   *     will not and the RestClient will throw a DriverDataNotFoundException.
   */
  boolean supportsNotFound();
}
