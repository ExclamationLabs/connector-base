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

package com.exclamationlabs.connid.base.connector.driver;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.List;
import java.util.Map;

public interface DriverInvocator<T extends IdentityModel> {

    String create(Driver driver, T model,
                  Map<String, List<String>> assignmentIdentifiers) throws ConnectorException;

    void update(Driver driver, String userId, T userModel,
                Map<String, List<String>> assignmentIdentifiers) throws ConnectorException;

    void delete(Driver driver, String userId) throws ConnectorException;

    List<T> getAll(Driver driver) throws ConnectorException;

    T getOne(Driver driver, String userId) throws ConnectorException;
}
