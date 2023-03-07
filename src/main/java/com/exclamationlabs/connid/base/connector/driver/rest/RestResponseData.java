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
