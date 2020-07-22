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

package com.exclamationlabs.connid.base.connector.configuration;

/**
 * An enumeration describing all possible connector properties that
 * identify configuration items that the Base Connector framework
 * actively supports.
 */
public enum ConnectorProperty {

    CONNECTOR_BASE_CONFIGURATION_ACTIVE,

    // Direct access token stored in configuration, no other auth
    CONNECTOR_BASE_AUTH_DIRECT_TOKEN,

    // JKS private key loading
    CONNECTOR_BASE_AUTH_JKS_ALIAS,
    CONNECTOR_BASE_AUTH_JKS_FILE,
    CONNECTOR_BASE_AUTH_JKS_PASSWORD,

    // JWT authentication
    CONNECTOR_BASE_AUTH_JWT_AUDIENCE,
    CONNECTOR_BASE_AUTH_JWT_EXPIRATION_PERIOD,
    CONNECTOR_BASE_AUTH_JWT_ISSUER,
    CONNECTOR_BASE_AUTH_JWT_SECRET,
    CONNECTOR_BASE_AUTH_JWT_SUBJECT,
    CONNECTOR_BASE_AUTH_JWT_USE_ISSUED_AT, // Y/N flag

    // OAuth2
    CONNECTOR_BASE_AUTH_OAUTH2_AUTHORIZATION_CODE,
    CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_ID,
    CONNECTOR_BASE_AUTH_OAUTH2_CLIENT_SECRET,
    CONNECTOR_BASE_AUTH_OAUTH2_ENCODED_SECRET,
    CONNECTOR_BASE_AUTH_OAUTH2_PASSWORD,
    CONNECTOR_BASE_AUTH_OAUTH2_REDIRECT_URI,
    CONNECTOR_BASE_AUTH_OAUTH2_REFRESH_TOKEN,
    CONNECTOR_BASE_AUTH_OAUTH2_TOKEN_URL,
    CONNECTOR_BASE_AUTH_OAUTH2_USERNAME,

    // PEM private key loading
    CONNECTOR_BASE_AUTH_PEM_FILE,

    // PFX key store loading
    CONNECTOR_BASE_AUTH_PFX_FILE,
    CONNECTOR_BASE_AUTH_PFX_PASSWORD,

    // Custom fields
    CONNECTOR_BASE_AUTH_CUSTOM_DOMAIN,
    CONNECTOR_BASE_AUTH_CUSTOM_SUBDOMAIN
}
