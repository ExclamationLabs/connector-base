package com.exclamationlabs.connid.base.connector.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationNameBuilderTest {

    @Test
    public void connectorWithDefaults() {
        ConfigurationNameBuilder builder = new ConfigurationNameBuilder();
        assertEquals("__bcon__development__exclamation_labs__bubba",
                builder.withConnector("BUBBA").build());
    }

    @Test
    public void connectorWithEnvironmentDefault() {
        ConfigurationNameBuilder builder = new ConfigurationNameBuilder();
        assertEquals("__bcon__development__me__bubba",
                builder.withConnector("BUBBA").withOwner("ME").build());
    }

    @Test
    public void connectorFullStrings() {
        ConfigurationNameBuilder builder = new ConfigurationNameBuilder();
        assertEquals("__bcon__uat__me__bubba",
                builder.withEnvironment("UAT").withConnector("BUBBA").withOwner("ME").build());
    }

    @Test
    public void connectorWithTypees() {
        ConfigurationNameBuilder builder = new ConfigurationNameBuilder();
        assertEquals("__bcon__production__bob__awesome",
                builder.withEnvironment(ConfigurationEnvironment.PRODUCTION).
                        withConnector(new MyConnector()).withOwner(new MyOwner()).build());
    }

    static class MyOwner implements ConfigurationOwner {

        @Override
        public String getName() {
            return "bob";
        }
    }

    static class MyConnector implements ConfigurationConnector {

        @Override
        public String getName() {
            return "awesome";
        }
    }
}
