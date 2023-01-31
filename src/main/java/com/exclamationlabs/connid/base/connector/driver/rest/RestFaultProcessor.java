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

import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

/**
 * Interface to handle faults that occur during execution of subclasses of BaseRestDriver. The
 * purpose of the processor is to diagnose the problem and choose where or not to log it, and
 * whether or not to throw an appropriate ConnId exception (ConnectorException or one of its
 * subclases). In certain cases, it might be ideal to ignore the exception and just proceed normally
 * with no exception (for Midpoint, this is often true with HTTP 404 Not found conditions).
 */
public interface RestFaultProcessor {

  /**
   * Process an HTTP response and handle error conditions. Normally BaseRestDriver will invoke this
   * method after it's determined the response for a web service call has resulted in a non
   * 200-level HTTP response.
   *
   * @param response HTTP response object to inspect
   * @param gsonBuilder GsonBuilder that can be used, if needed, to read JSON response from the body
   *     if it might contain error details.
   * @throws ConnectorException Throw this exception or one of its more specific subclass exceptions
   */
  void process(HttpResponse response, GsonBuilder gsonBuilder) throws ConnectorException;
}
