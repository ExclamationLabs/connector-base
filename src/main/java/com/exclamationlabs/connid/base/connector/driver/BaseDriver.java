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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseDriver implements Driver {

    Map<Class<? extends IdentityModel>, DriverInvocator> invocatorMap;

    public BaseDriver() {
        invocatorMap = new HashMap<>();
    }

    @Override
    public DriverInvocator getInvocator(Class<? extends IdentityModel> identityModelClass) {
        return invocatorMap.get(identityModelClass);
    }

    @Override
    public void addInvocator(Class<? extends IdentityModel> identityModelClass, DriverInvocator invocator) {
        if (!invocatorMap.containsKey(identityModelClass)) {
            invocatorMap.put(identityModelClass, invocator);
        }
    }


    @Override
    public String create(Class<? extends IdentityModel> identityModelClass, IdentityModel model,
                         Map<String, List<String>> assignmentIdentifiers) throws ConnectorException {
        return getInvocator(identityModelClass).create(this, model, assignmentIdentifiers);
    }

    @Override
    public void update(Class<? extends IdentityModel> identityModelClass, String id,
                       IdentityModel model, Map<String, List<String>> assignmentIdentifiers) throws ConnectorException {
        getInvocator(identityModelClass).update(this, id, model, assignmentIdentifiers);
    }

    @Override
    public void delete(Class<? extends IdentityModel> modelClass, String id) throws ConnectorException {
        getInvocator(modelClass).delete(this, id);
    }

    @Override
    public IdentityModel getOne(Class<? extends IdentityModel> modelClass, String id) throws ConnectorException {
        return getInvocator(modelClass).getOne(this, id);
    }

    @Override
    public List<IdentityModel> getAll(Class<? extends IdentityModel> modelClass) throws ConnectorException {
        return getInvocator(modelClass).getAll(this);
    }

}
