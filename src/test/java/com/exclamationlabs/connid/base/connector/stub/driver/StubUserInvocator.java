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
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;

public class StubUserInvocator implements DriverInvocator {

    @Override
    public String create(Driver driver, IdentityModel model,
                         Map<String, List<String>> assignmentIdentifiers) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("user create");
        if (assignmentIdentifiers != null && assignmentIdentifiers.containsKey("club") && assignmentIdentifiers.containsKey("group")) {
            stubDriver.setMethodInvoked("user create with group and club ids");
        } else {
            if (assignmentIdentifiers != null && assignmentIdentifiers.containsKey("group")) {
                stubDriver.setMethodInvoked("user create with group ids");
            }
        }

        stubDriver.setMethodParameter1(model);
        return UUID.randomUUID().toString();
    }

    @Override
    public void update(Driver driver, String userId, IdentityModel model,
                       Map<String, List<String>> assignmentIdentifiers) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("user update");
        if (assignmentIdentifiers != null && assignmentIdentifiers.containsKey("group")) {
            stubDriver.setMethodInvoked("user update with group ids");
        }
        stubDriver.setMethodParameter1(userId);
        stubDriver.setMethodParameter2(model);
    }

    @Override
    public void delete(Driver driver, String id) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("user delete");
        stubDriver.setMethodParameter1(id);
    }

    @Override
    public List<IdentityModel> getAll(Driver driver) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("user getAll");
        StubUser user1 = new StubUser();
        user1.setId(UUID.randomUUID().toString());
        user1.setUserName("User Uno");

        StubUser user2 = new StubUser();
        user2.setId(UUID.randomUUID().toString());
        user2.setUserName("User Dos");
        return new ArrayList<>(Arrays.asList(user1, user2));
    }

    @Override
    public StubUser getOne(Driver driver, String id) throws ConnectorException {
        StubDriver stubDriver = (StubDriver) driver;
        stubDriver.setMethodInvoked("user getOne");
        stubDriver.setMethodParameter1(id);
        StubUser user1 = new StubUser();
        user1.setId(UUID.randomUUID().toString());
        user1.setUserName("Ying");
        user1.setEmail("ying@yahoo.com");

        return user1;
    }
}
