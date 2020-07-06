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

package com.exclamationlabs.connid.base.connector.authenticator.client;

import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import org.apache.http.client.HttpClient;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

import java.security.KeyStore;
import java.util.Set;

/**
 * A SecureClientLoader can be used to create a secure HttpClient, leveraging
 * a KeyStore and configuration information.
 */
public interface SecureClientLoader {
    Set<ConnectorProperty> getRequiredPropertyNames();

    HttpClient load(BaseConnectorConfiguration configuration, KeyStore keyStore) throws ConnectorSecurityException;
}
