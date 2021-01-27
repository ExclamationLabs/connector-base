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
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;

public class StubUserInvocator implements DriverInvocator<StubDriver,StubUser> {

    @Override
    public String create(StubDriver driver, StubUser model,
                         Map<String, List<String>> assignmentIdentifiers) throws ConnectorException {
        driver.setMethodInvoked("user create");
        if (assignmentIdentifiers != null && assignmentIdentifiers.containsKey("club") && assignmentIdentifiers.containsKey("group")) {
            driver.setMethodInvoked("user create with group and club ids");
        } else {
            if (assignmentIdentifiers != null && assignmentIdentifiers.containsKey("group")) {
                driver.setMethodInvoked("user create with group ids");
            }
        }

        driver.setMethodParameter1(model);
        return UUID.randomUUID().toString();
    }

    @Override
    public void update(StubDriver driver, String userId, StubUser model,
                       Map<String, List<String>> assignmentIdentifiers) throws ConnectorException {
        driver.setMethodInvoked("user update");
        if (assignmentIdentifiers != null && assignmentIdentifiers.containsKey("group")) {
            driver.setMethodInvoked("user update with group ids");
        }
        driver.setMethodParameter1(userId);
        driver.setMethodParameter2(model);
    }

    @Override
    public void delete(StubDriver driver, String id) throws ConnectorException {
        driver.setMethodInvoked("user delete");
        driver.setMethodParameter1(id);
    }

    @Override
    public List<StubUser> getAll(StubDriver driver) throws ConnectorException {
        driver.setMethodInvoked("user getAll");
        StubUser user1 = new StubUser();
        user1.setId(UUID.randomUUID().toString());
        user1.setUserName("User Uno");

        StubUser user2 = new StubUser();
        user2.setId(UUID.randomUUID().toString());
        user2.setUserName("User Dos");
        return Arrays.asList(user1, user2);
    }

    @Override
    public StubUser getOne(StubDriver driver, String id) throws ConnectorException {
        driver.setMethodInvoked("user getOne");
        driver.setMethodParameter1(id);
        StubUser user1 = new StubUser();
        user1.setId(UUID.randomUUID().toString());
        user1.setUserName("Ying");
        user1.setEmail("ying@yahoo.com");

        return user1;
    }
}
