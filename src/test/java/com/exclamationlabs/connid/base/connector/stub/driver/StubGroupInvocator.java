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

import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.driver.DriverInvocator;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;

public class StubGroupInvocator implements DriverInvocator<StubGroup> {

    @Override
    public String create(Driver driver, StubGroup model, Map<String,
            List<String>> assignmentIdentifiers) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("group create");

        if (assignmentIdentifiers != null && assignmentIdentifiers.containsKey("supergroup")) {
                stubDriver.setMethodInvoked("group create with supergroup ids");
        }

        stubDriver.setMethodParameter1(model);
        return UUID.randomUUID().toString();
    }

    @Override
    public void update(Driver driver, String userId, StubGroup model, Map<String,
            List<String>> assignmentIdentifiers) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("group update");
        stubDriver.setMethodParameter1(userId);
        stubDriver.setMethodParameter2(model);
    }

    @Override
    public void delete(Driver driver, String id) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("group delete");
        stubDriver.setMethodParameter1(id);
    }

    @Override
    public List<StubGroup> getAll(Driver driver) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("group getAll");
        StubGroup group1 = new StubGroup();
        group1.setId(UUID.randomUUID().toString());
        group1.setName("Group Uno");

        StubGroup group2 = new StubGroup();
        group2.setId(UUID.randomUUID().toString());
        group2.setName("Group Dos");
        return Arrays.asList(group1, group2);
    }

    @Override
    public StubGroup getOne(Driver driver, String id) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("group getOne");
        stubDriver.setMethodParameter1(id);
        StubGroup group = new StubGroup();
        group.setId(UUID.randomUUID().toString());
        group.setName("A Group");

        return group;
    }
}
