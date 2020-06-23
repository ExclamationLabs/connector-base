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

import org.apache.commons.lang3.StringUtils;

public class ConfigurationNameBuilder {

    private String environment = ConfigurationEnvironment.DEVELOPMENT.name();
    private String owner = "EXCLAMATION_LABS";
    private String connector = "UNKNOWN";

    public ConfigurationNameBuilder() {}

    public ConfigurationNameBuilder withEnvironment(ConfigurationEnvironment input) {
        environment = input.name();
        return this;
    }

    public ConfigurationNameBuilder withEnvironment(String input) {
        environment = input;
        return this;
    }

    public ConfigurationNameBuilder withOwner(ConfigurationOwner input) {
        owner = input.getName();
        return this;
    }

    public ConfigurationNameBuilder withOwner(String input) {
        owner = input;
        return this;
    }

    public ConfigurationNameBuilder withConnector(ConfigurationConnector input) {
        connector = input.getName();
        return this;
    }

    public ConfigurationNameBuilder withConnector(String input) {
        connector = input;
        return this;
    }

    public String build() {
        return "__bcon__" + StringUtils.lowerCase(environment) + "__" +
                StringUtils.lowerCase(owner) + "__" +
                StringUtils.lowerCase(connector);
    }
}
