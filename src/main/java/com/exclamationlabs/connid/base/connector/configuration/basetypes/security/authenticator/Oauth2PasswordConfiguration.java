/*
    Copyright 2021 Exclamation Labs

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

package com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;

public interface Oauth2PasswordConfiguration extends ConnectorConfiguration, Oauth2Configuration {
    String getTokenUrl();
    void setTokenUrl(String input);
    String getEncodedSecret();
    void setEncodedSecret(String input);
    String getUsername();
    void setUsername(String input);
    String getPassword();
    void setPassword(String input);
}