/*
    Copyright 2021 Exclamation Labs

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

import org.identityconnectors.common.logging.Log;

import java.lang.reflect.Field;

public class ConfigurationWriter {

    private static final Log LOG = Log.getLog(ConfigurationWriter.class);

    private ConfigurationWriter() {}

    public static String writeToString(ConnectorConfiguration configuration) {
        StringBuilder build = new StringBuilder();
        Class<?> configClass = configuration.getClass();

        writeFromClass(configClass, build, configuration);
        if (configClass.getSuperclass() != null) {
            writeFromClass(configClass.getSuperclass(), build, configuration);
        }
        return build.toString();
    }

    protected static void writeFromClass(Class<?> configClass, StringBuilder build,
                                         ConnectorConfiguration configuration) {
        for (Field field : configClass.getDeclaredFields()) {
            ConfigurationInfo configInfo = field.getAnnotation(ConfigurationInfo.class);
            if (configInfo != null) {
                String configPath = configInfo.path();
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(configuration);
                    build.append(configPath);
                    build.append("=");
                    build.append(fieldValue == null ? "" : fieldValue.toString());
                    build.append("\n");
                } catch (IllegalAccessException ill) {
                    LOG.warn("Error while reading field value " + field.getName(), ill);
                }
            }

        } // end for fields
    }
}