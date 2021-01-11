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
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.stub.model.StubSupergroup;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;

public class StubSupergroupInvocator implements DriverInvocator {

    @Override
    public String create(Driver driver, IdentityModel model, Map<String,
            List<String>> assignmentIdentifiers) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("supergroup create");
        stubDriver.setMethodParameter1(model);
        return UUID.randomUUID().toString();
    }

    @Override
    public void update(Driver driver, String userId, IdentityModel model, Map<String,
            List<String>> assignmentIdentifiers) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("supergroup update");
        stubDriver.setMethodParameter1(userId);
        stubDriver.setMethodParameter2(model);
    }

    @Override
    public void delete(Driver driver, String id) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("supergroup delete");
        stubDriver.setMethodParameter1(id);
    }

    @Override
    public List<IdentityModel> getAll(Driver driver) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("club getAll");
        StubSupergroup item1 = new StubSupergroup();
        item1.setId(UUID.randomUUID().toString());
        item1.setName("SuperGroup Uno");

        StubSupergroup item2 = new StubSupergroup();
        item2.setId(UUID.randomUUID().toString());
        item2.setName("SuperGroup Dos");
        return new ArrayList<>(Arrays.asList(item1, item2));
    }

    @Override
    public StubSupergroup getOne(Driver driver, String id) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("group getOne");
        stubDriver.setMethodParameter1(id);
        StubSupergroup item = new StubSupergroup();
        item.setId(UUID.randomUUID().toString());
        item.setName("A Sueprgroup");

        return item;
    }
}
