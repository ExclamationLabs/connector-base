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

import org.identityconnectors.common.security.GuardedString;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.*;

public class ConfigurationReaderTest {

    @Test
    public void prepareTestConfigurationLocalTesting() {
        ConnectorConfiguration configuration = new TestingConfiguration("dummy");
        ConfigurationReader.prepareTestConfiguration(configuration);

        assertEquals("dummy", configuration.getName());
        assertEquals("src/test/resources/dummy.properties", configuration.getSource());
        assertNull(configuration.getActive());
    }

    @Test
    public void prepareTestConfigurationJenkinsTesting() {
        setEnvForTesting();

        ConnectorConfiguration configuration = new TestingConfiguration("dummy");
        ConfigurationReader.prepareTestConfiguration(configuration);

        assertEquals("dummy", configuration.getName());
        assertEquals("/dummy_path/dummy.properties", configuration.getSource());
        assertNull(configuration.getActive());
    }

    @Test
    public void readPropertiesFromSource() {
        TestingConfiguration configuration = new TestingConfiguration("testing");
        ConfigurationReader.prepareTestConfiguration(configuration);

        assertEquals("testing", configuration.getName());
        assertEquals("src/test/resources/testing.properties", configuration.getSource());
        assertNull(configuration.getActive());

        ConfigurationReader.readPropertiesFromSource(configuration);
        assertEquals("customValue", configuration.getThing1());
        assertEquals(Integer.valueOf(15), configuration.getRestIoErrorRetries());
        assertEquals(Long.valueOf(5555566666L), configuration.getThing2());
        assertNotNull(configuration.getThing3());
        assertTrue(configuration.getThing3());
        assertTrue(configuration.getActive());
        GuardedString hiddenString = configuration.getGuardedValue();
        hiddenString.access(clearChars ->
                assertEquals("testMe", new String(clearChars)));
        assertNotNull(configuration.getArrayValue());
        assertEquals(3, configuration.getArrayValue().length);
        assertEquals("Uno", configuration.getArrayValue()[0]);
    }

    @Test
    public void readPropertiesFromSourceSadPath() {
        TestingConfiguration configuration = new TestingConfiguration("testing_sad");
        ConfigurationReader.prepareTestConfiguration(configuration);

        assertEquals("testing_sad", configuration.getName());
        assertEquals("src/test/resources/testing_sad.properties", configuration.getSource());
        assertNull(configuration.getActive());

        ConfigurationReader.readPropertiesFromSource(configuration);
        assertNull(configuration.getThing1());
        assertNull(configuration.getRestIoErrorRetries());
        assertNull(configuration.getThing2());
        assertFalse(configuration.getThing3());
        assertTrue(configuration.getActive());
    }



    private static void setEnvForTesting() {
        try {
            Map<String, String> env = System.getenv();
            Class<?> cl = env.getClass();
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Map<String, String> writableEnv = (Map<String, String>) field.get(env);
            writableEnv.put("dummy", "/dummy_path/dummy.properties");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set environment variable", e);
        }
    }

}
