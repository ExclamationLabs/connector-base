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

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationWriterTest {

    @Test
    public void writeToString() {
        TestingConfiguration configuration = new TestingConfiguration("testing");
        ConfigurationReader.prepareTestConfiguration(configuration);

        assertEquals("testing", configuration.getName());
        assertEquals("src/test/resources/testing.properties", configuration.getSource());
        assertNull(configuration.getActive());

        ConfigurationReader.readPropertiesFromSource(configuration);

        final String expectedOutput = "currentToken=\n" +
            "source=src/test/resources/testing.properties\n" +
            "name=testing\n" +
            "active=true\n" +
            "custom.thing1=customValue\n" +
            "custom.thing2=5555566666\n" +
            "custom.thing3=true\n" +
            "rest.ioErrorRetries=15\n" +
            "custom.guardedValue=testMe\n" +
            "custom.arrayValue=[Uno, Dos, Tres]\n";
        String output = ConfigurationWriter.writeToString(configuration);
        assertEquals(expectedOutput, output);
    }

}
