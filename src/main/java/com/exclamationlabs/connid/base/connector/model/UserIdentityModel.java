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

import java.util.List;

/**
 * All Connectors developed using the base framework
 * must define a concrete class (probably a POJO) that inherits
 * UserIdentityModel, so that the base connector framework
 * has a User model definition to transmit and utilize.
 */
public interface UserIdentityModel extends IdentityModel {

    @Override
    default IdentityModelType getIdentityType() {
        return IdentityModelType.USER;
    }

    /**
     * Implement this method to return the connector attribute name that
     * holds group id(s) that the user currently belongs to.
     * @return String version of attribute name
     */
    String getAssignedGroupsAttributeName();

    /**
     * Implement this method to return a list of group id's
     * that the user is assigned to.  This needs to be implemented
     * so the base API can automate adding/removing groups from the user.
     * @return List of Strings holding group ids that the user is currently
     * assigned to.
     */
    List<String> getAssignedGroupIds();
}
