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

package com.exclamationlabs.connid.base.connector.model;

/**
 * Interface to describe model objects belonging
 * to this base connector implementation.
 */
public interface IdentityModel {

    /**
     * Implement this method to return the id value applicable to the
     * concrete model object.
     * @return String version of Id value recognized by model
     */
    String getIdentityIdValue();

    /**
     * Implement this method to return the name applicable to the
     * concrete model object.
     * @return String version of the name recognized by model
     */
    String getIdentityNameValue();

    default String identityToString() {
        return getIdentityIdValue() + ";" + getIdentityNameValue();
    }
}
