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

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class for a Driver that should be subclassed for all use cases.  This
 * abstract class provides the support for associating the Driver with a map
 * of Invocators, each of which that are used to perform data operations on the data
 * system pertaining to specific object types.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseDriver<T extends ConnectorConfiguration> implements Driver<T> {

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
    public String create(Class<? extends IdentityModel> identityModelClass, IdentityModel model)
            throws ConnectorException {
        return getInvocator(identityModelClass).create(this, model);
    }

    @Override
    public void update(Class<? extends IdentityModel> identityModelClass, String id,
                       IdentityModel model) throws ConnectorException {
        getInvocator(identityModelClass).update(this, id, model);
    }

    @Override
    public void delete(Class<? extends IdentityModel> modelClass, String id) throws ConnectorException {
        getInvocator(modelClass).delete(this, id);
    }

    @Override
    public IdentityModel getOne(Class<? extends IdentityModel> modelClass, String id,
                                Map<String,Object> operationOptionsData) throws ConnectorException {
        return getInvocator(modelClass).getOne(this, id, operationOptionsData);
    }

    @Override
    public Set<IdentityModel> getAll(Class<? extends IdentityModel> modelClass,
                                             ResultsFilter filter,
                                             ResultsPaginator paginator,
                                             Integer resultCap) throws ConnectorException {
        return getInvocator(modelClass).getAll(this, filter, paginator, resultCap);
    }

}
