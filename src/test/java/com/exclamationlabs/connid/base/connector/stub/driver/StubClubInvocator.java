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
import com.exclamationlabs.connid.base.connector.stub.model.StubClub;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;

public class StubClubInvocator implements DriverInvocator<StubClub> {

    @Override
    public String create(Driver driver, StubClub model, Map<String,
            List<String>> assignmentIdentifiers) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("club create");
        stubDriver.setMethodParameter1(model);
        return UUID.randomUUID().toString();
    }

    @Override
    public void update(Driver driver, String userId, StubClub model, Map<String,
            List<String>> assignmentIdentifiers) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("club update");
        stubDriver.setMethodParameter1(userId);
        stubDriver.setMethodParameter2(model);
    }

    @Override
    public void delete(Driver driver, String id) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("club delete");
        stubDriver.setMethodParameter1(id);
    }

    @Override
    public List<StubClub> getAll(Driver driver) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("club getAll");
        StubClub item1 = new StubClub();
        item1.setId(UUID.randomUUID().toString());
        item1.setName("Club Uno");

        StubClub item2 = new StubClub();
        item2.setId(UUID.randomUUID().toString());
        item2.setName("Club Dos");
        return Arrays.asList(item1, item2);
    }

    @Override
    public StubClub getOne(Driver driver, String id) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("group getOne");
        stubDriver.setMethodParameter1(id);
        StubClub item = new StubClub();
        item.setId(UUID.randomUUID().toString());
        item.setName("A Club");

        return item;
    }
}
