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

package com.exclamationlabs.connid.base.connector.stub.driver;

import com.exclamationlabs.connid.base.connector.driver.DriverInvocator;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;

public class StubGroupInvocator implements DriverInvocator<StubDriver, StubGroup> {

    @Override
    public String create(StubDriver driver, StubGroup model) throws ConnectorException {
        driver.setMethodInvoked("group create");

        if (model.getSupergroupIds() != null) {
            driver.setMethodInvoked("group create with supergroup ids");
        }

        driver.setMethodParameter1(model);
        return UUID.randomUUID().toString();
    }

    @Override
    public void update(StubDriver driver, String userId, StubGroup model)
            throws ConnectorException {
        driver.setMethodInvoked("group update");
        driver.setMethodParameter1(userId);
        driver.setMethodParameter2(model);
    }

    @Override
    public void delete(StubDriver driver, String id) throws ConnectorException {
        driver.setMethodInvoked("group delete");
        driver.setMethodParameter1(id);
    }

    @Override
    public Set<StubGroup> getAll(StubDriver driver, ResultsFilter filter,
                                 ResultsPaginator paginator, Integer resultCap) throws ConnectorException {
        driver.setMethodInvoked("group getAll");
        StubGroup group1 = new StubGroup();
        group1.setId(UUID.randomUUID().toString());
        group1.setName("Group Uno");

        StubGroup group2 = new StubGroup();
        group2.setId(UUID.randomUUID().toString());
        group2.setName("Group Dos");
        return new HashSet<>(Arrays.asList(group1, group2));
    }

    @Override
    public StubGroup getOne(StubDriver driver, String id, Map<String,Object> data) throws ConnectorException {
        driver.setMethodInvoked("group getOne");
        driver.setMethodParameter1(id);
        StubGroup group = new StubGroup();
        group.setId(UUID.randomUUID().toString());
        group.setName("A Group");

        return group;
    }
}
