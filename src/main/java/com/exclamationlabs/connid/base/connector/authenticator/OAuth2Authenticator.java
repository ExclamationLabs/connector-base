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

package com.exclamationlabs.connid.base.connector.authenticator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/** Authenticator using OAuth2 conventions. */
public interface OAuth2Authenticator {

  /**
   * Return the grant type applicable to this OAuth2 authentication execution.
   *
   * @return Simple string identifying OAuth2 grant_type
   */
  String getGrantType();

  /**
   * Add any additional form fields for this OAuth2 authenticator to the form.
   *
   * @param fieldList Form field list in process of being constructed for OAuth2 HTTP Post request.
   */
  default void addAdditionalFormFields(List<NameValuePair> fieldList) {
    for (Map.Entry<String, String> entry : getAdditionalFormFields().entrySet()) {
      fieldList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
    }
  }

  /**
   * Define any additional form fields needed for specific OAuth2 implementation.
   *
   * @return Map of strings defining additional form field name to value pairs.
   */
  default Map<String, String> getAdditionalFormFields() {
    return Collections.emptyMap();
  }
}
