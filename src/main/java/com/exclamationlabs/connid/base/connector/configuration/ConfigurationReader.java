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

import com.exclamationlabs.connid.base.connector.logging.Logger;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;

public class ConfigurationReader {

  private ConfigurationReader() {}

  public static void setupTestConfiguration(ConnectorConfiguration configuration) {
    prepareTestConfiguration(configuration);
    readPropertiesFromSource(configuration);
  }

  public static void prepareTestConfiguration(ConnectorConfiguration configuration) {
    String configurationName = configuration.getName();
    Logger.debug(
        ConfigurationReader.class,
        String.format("Test Configuration with location %s specified", configurationName));
    configuration.setName(configurationName);
    if (System.getenv(configurationName) != null) {
      // Found environment variable match for configuration name,
      // indicating this must be a Jenkins build.  Read configuration file path from
      // environment variable in Jenkins with this configurationName
      configuration.setSource(System.getenv(configurationName));
    } else {
      // This appears to be an integration test run from local workstation
      // Use project test properties file path matching this configuration name
      configuration.setSource("src/test/resources/" + configurationName + ".properties");
    }
  }

  public static void readPropertiesFromSource(ConnectorConfiguration configuration)
      throws ConfigurationException {
    Logger.info(
        ConfigurationReader.class,
        String.format(
            "invoking configuration setup for %s", configuration.getClass().getSimpleName()));
    if (configuration.getSource() != null) {
      Logger.info(
          ConfigurationReader.class,
          String.format("using configuration path %s", configuration.getSource()));
      Properties connectorProperties = new Properties();
      try {
        connectorProperties.load(new FileReader(configuration.getSource()));
        Logger.info(
            ConfigurationReader.class,
            String.format(
                "configuration properties loaded from %s for configuration %s",
                configuration.getSource(), configuration.getClass().getSimpleName()));
      } catch (IOException ex) {
        throw new ConfigurationException(
            "Failed to read configuration file from location " + configuration.getSource(), ex);
      }
      loadPropertiesInfoConfiguration(configuration, connectorProperties);

      Logger.info(
          ConfigurationReader.class,
          String.format(
              "configuration properties loaded into configuration object from properties "
                  + "for configuration %s",
              configuration.getClass().getSimpleName()));
      // Note: validation is done later via BaseConnector init()
    }
  }

  protected static void loadPropertiesInfoConfiguration(
      ConnectorConfiguration configuration, Properties properties) {

    Class<?> configClass = configuration.getClass();

    loadPropertiesWithinClass(configClass, configuration, properties);
    if (configClass.getSuperclass() != null) {
      loadPropertiesWithinClass(configClass.getSuperclass(), configuration, properties);
    }
  }

  protected static void loadPropertiesWithinClass(
      Class<?> configClass, ConnectorConfiguration configuration, Properties properties) {
    for (Field field : configClass.getDeclaredFields()) {
      ConfigurationInfo configInfo = field.getAnnotation(ConfigurationInfo.class);
      if (configInfo != null) {
        String configPath = configInfo.path();
        if (properties.getProperty(configPath) != null) {
          // property key matches current configuration path
          // set field value to property value
          String propertyValue = properties.getProperty(configPath);
          Class<?> fieldType = field.getType();
          try {
            switch (fieldType.getSimpleName()) {
              case "String[]":
                field.setAccessible(true);
                String result = propertyValue.replace("[", "");
                result = result.replace("]", "");
                String[] splitData = result.split(",");
                field.set(configuration, splitData);
                Logger.debug(
                    ConfigurationReader.class,
                    String.format(
                        "Loaded String array data %s into field %s using path %s",
                        propertyValue, field.getName(), configPath));
                break;
              case "String":
                field.setAccessible(true);
                field.set(configuration, propertyValue);
                Logger.debug(
                    ConfigurationReader.class,
                    String.format(
                        "Loaded String data %s into field %s using path %s",
                        propertyValue, field.getName(), configPath));
                break;
              case "GuardedString":
                field.setAccessible(true);
                field.set(configuration, new GuardedString(propertyValue.toCharArray()));
                Logger.debug(
                    ConfigurationReader.class,
                    String.format(
                        "Loaded Guarded String data %s into field %s using path %s",
                        propertyValue, field.getName(), configPath));
                break;
              case "Boolean":
                field.setAccessible(true);
                field.set(configuration, Boolean.parseBoolean(propertyValue));
                Logger.debug(
                    ConfigurationReader.class,
                    String.format(
                        "Loaded Boolean data %s into field %s using path %s",
                        propertyValue, field.getName(), configPath));
                break;
              case "Integer":
                field.setAccessible(true);
                field.set(configuration, Integer.parseInt(propertyValue));
                Logger.debug(
                    ConfigurationReader.class,
                    String.format(
                        "Loaded Integer data %s into field %s using path %s",
                        propertyValue, field.getName(), configPath));
                break;
              case "Long":
                field.setAccessible(true);
                field.set(configuration, Long.parseLong(propertyValue));
                Logger.debug(
                    ConfigurationReader.class,
                    String.format(
                        "Loaded Long data %s into field %s using path %s",
                        propertyValue, field.getName(), configPath));
                break;
              default:
                Logger.warn(
                    ConfigurationReader.class,
                    String.format(
                        "Ignoring field %s ; unrecognized field type %s",
                        configPath, fieldType.getName()));
                break;
            }
          } catch (IllegalAccessException | NumberFormatException failed) {
            Logger.error(
                ConfigurationReader.class, "Error while setting field " + configPath, failed);
          }

        } else {
          Logger.debug(
              ConfigurationReader.class,
              String.format(
                  "Data not set.  No property data found for field %s using path %s",
                  field.getName(), configPath));
        }
      }
    } // end for fields
  }
}
