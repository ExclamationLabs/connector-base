package com.exclamationlabs.connid.base.connector.configuration;

import org.apache.commons.lang3.StringUtils;

public class ConfigurationNameBuilder {

    private String environment = ConfigurationEnvironment.DEVELOPMENT.name();
    private String owner = ConfigurationOwner.EXCLAMATION_LABS.name();;
    private String connector = "UNKNOWN";

    public ConfigurationNameBuilder() {}

    public ConfigurationNameBuilder withEnvironment(ConfigurationEnvironment input) {
        environment = input.name();
        return this;
    }

    public ConfigurationNameBuilder withOwner(ConfigurationOwner input) {
        owner = input.name();
        return this;
    }

    public ConfigurationNameBuilder withConnector(ConfigurationConnector input) {
        connector = input.name();
        return this;
    }

    public ConfigurationNameBuilder withEnvironment(String input) {
        environment = input;
        return this;
    }

    public ConfigurationNameBuilder withOwner(String input) {
        owner = input;
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
