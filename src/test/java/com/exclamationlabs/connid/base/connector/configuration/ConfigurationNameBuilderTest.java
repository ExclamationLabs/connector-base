package com.exclamationlabs.connid.base.connector.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationNameBuilderTest {

    @Test
    public void connectorWithDefaults() {
        ConfigurationNameBuilder builder = new ConfigurationNameBuilder();
        String actualName = builder.withEnvironment("UAT")
                .withConnector("BUBBA").withOwner("ME").build();
        String expectedName = "__bcon__uat__me__bubba";
        assertEquals(expectedName, actualName);
    }

    @Test
    public void connectorWithEnvironmentDefault() {
        ConfigurationNameBuilder builder = new ConfigurationNameBuilder();
        String actualName = builder.withConnector("BUBBA").withOwner("ME").build();
        String expectedName = "__bcon__development__me__bubba";
        assertEquals(expectedName, actualName);
    }

    @Test
    public void connectorFullStrings() {
        ConfigurationNameBuilder builder = new ConfigurationNameBuilder();
        String actualName = builder.withEnvironment("UAT").
                withConnector("BUBBA").withOwner("ME").build();
        String expectedName = "__bcon__uat__me__bubba";
        assertEquals(expectedName, actualName);
    }

    @Test
    public void connectorWithTypes() {
        ConfigurationNameBuilder builder = new ConfigurationNameBuilder();
        String actualName = builder.withEnvironment(ConfigurationEnvironment.PRODUCTION).
                withConnector(() -> "awesome").withOwner(() -> "bob").build();
        String expectedName = "__bcon__production__bob__awesome";
        assertEquals(expectedName, actualName);
    }

}
