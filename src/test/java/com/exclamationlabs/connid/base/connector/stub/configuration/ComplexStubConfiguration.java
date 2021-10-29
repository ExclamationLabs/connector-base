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

package com.exclamationlabs.connid.base.connector.stub.configuration;

import com.exclamationlabs.connid.base.connector.configuration.basetypes.ResultsConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.ServiceConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.JwtRs256Configuration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.authenticator.Oauth2JwtConfiguration;
import org.identityconnectors.framework.spi.ConfigurationClass;

import java.util.Map;

@ConfigurationClass(skipUnsupported = true)
public class ComplexStubConfiguration extends StubConfiguration
        implements ServiceConfiguration, ResultsConfiguration, JwtRs256Configuration, Oauth2JwtConfiguration {

    private Boolean deepGet = true;
    private Boolean deepImport = true;
    private Integer importBatchSize = 5;
    private Boolean pagination = true;

    @SuppressWarnings("unused")
    public ComplexStubConfiguration(String configName) {
        super();
    }

    public ComplexStubConfiguration() {
        super();
    }


    @Override
    public Boolean getDeepGet() {
        return deepGet;
    }

    @Override
    public void setDeepGet(Boolean input) {
        deepGet = input;
    }

    @Override
    public Boolean getDeepImport() {
        return deepImport;
    }

    @Override
    public void setDeepImport(Boolean input) {
        deepImport = input;
    }

    @Override
    public Integer getImportBatchSize() {
        return importBatchSize;
    }

    @Override
    public void setImportBatchSize(Integer input) {
        importBatchSize = input;
    }

    @Override
    public Boolean getPagination() {
        return pagination;
    }

    @Override
    public void setPagination(Boolean input) {
        pagination = input;
    }

    @Override
    public String getServiceServiceUrl() {
        return "testurl";
    }

    @Override
    public void setServiceServiceUrl(String input) {

    }

    @Override
    public String getIssuer() {
        return null;
    }

    @Override
    public void setIssuer(String input) {

    }

    @Override
    public String getSubject() {
        return null;
    }

    @Override
    public void setSubject(String input) {

    }

    @Override
    public Long getExpirationPeriod() {
        return null;
    }

    @Override
    public void setExpirationPeriod(Long input) {

    }

    @Override
    public String getAudience() {
        return null;
    }

    @Override
    public void setAudience(String input) {

    }

    @Override
    public Boolean getUseIssuedAt() {
        return null;
    }

    @Override
    public void setUseIssuedAt(Boolean input) {

    }

    @Override
    public Map<String, String> getExtraClaimData() {
        return null;
    }

    @Override
    public void setExtraClaimData(Map<String, String> data) {

    }

    @Override
    public String getTokenUrl() {
        return null;
    }

    @Override
    public void setTokenUrl(String input) {

    }

    @Override
    public Map<String, String> getOauth2Information() {
        return null;
    }

    @Override
    public void setOauth2Information(Map<String, String> info) {

    }
}
