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
 * All Connectors developed using the base framework
 * must define a concrete class (probably a POJO) that inherits
 * GroupIdentityModel, so that the base connector framework
 * has a Group model definition to transmit and utilize.
 */
public interface GroupIdentityModel extends IdentityModel {

    @Override
    default IdentityModelType getIdentityType() {
        return IdentityModelType.GROUP;
    }
}
