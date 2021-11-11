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

package com.exclamationlabs.connid.base.connector.authenticator.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Model holding possible OAuth2 response data.
 * Not all fields may be populated from response, but accessToken
 * should be expected to have a value from an OAuth2 response.
 */
public class OAuth2AccessTokenContainer {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("instance_url")
    private String instanceUrl;

    @SerializedName("expires_in")
    private Long expiresIn;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("refresh_token_expires_in")
    private String refreshTokenExpiresIn;

    private String id;

    private String scope;

    private String email;

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }

    public void setRefreshTokenExpiresIn(String refreshTokenExpiresIn) {
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    public Map<String, String> toMap() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("accessToken", accessToken);
        dataMap.put("tokenType", tokenType);
        dataMap.put("instanceUrl", instanceUrl);
        dataMap.put("expiresIn", expiresIn == null ? null : expiresIn.toString());
        dataMap.put("refreshToken", refreshToken);
        dataMap.put("refreshTokenExpiresIn", refreshTokenExpiresIn);
        dataMap.put("id", id);
        dataMap.put("scope", scope);
        dataMap.put("email", email);

        return dataMap;
    }
}
