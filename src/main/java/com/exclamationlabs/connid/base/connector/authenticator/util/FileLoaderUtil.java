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

package com.exclamationlabs.connid.base.connector.authenticator.util;

/**
 * Special utility to allow Jenkins/CI to load file using environment variable
 * value in lieu of an actual file path reference, since Jenkins/CI does not hardcode
 * file pathing.
 */
public class FileLoaderUtil {

    private FileLoaderUtil() {}

    public static String getFileLocation(final String configurationName, final String propertyName,
                                  final String propertyValue) {
        if (configurationName != null) {
            String fullIdentifier = configurationName + "__" + propertyName;
            if (System.getenv(fullIdentifier) != null) {
                // Concatenated environment variable found on Jenkins/CI - use this environment
                // variable value in lieu of file path
                return System.getenv(fullIdentifier);
            }
        }
        // Environment variable not found, use configured property value as file location
        return propertyValue;
    }
}
