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

package com.exclamationlabs.connid.base.stub.driver;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.stub.model.StubGroup;
import com.exclamationlabs.connid.base.stub.model.StubUser;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StubDriver implements Driver<StubUser, StubGroup> {
    @Override
    public void initialize(ConnectorConfiguration configuration, Authenticator authenticator) throws ConnectorException {

    }

    @Override
    public void test() throws ConnectorException {

    }

    @Override
    public void close() {

    }

    @Override
    public String createUser(StubUser userModel) throws ConnectorException {
        return UUID.randomUUID().toString();
    }

    @Override
    public String createGroup(StubGroup groupModel) throws ConnectorException {
        return UUID.randomUUID().toString();
    }

    @Override
    public void updateUser(String userId, StubUser userModel) throws ConnectorException {

    }

    @Override
    public void updateGroup(String groupId, StubGroup groupModel) throws ConnectorException {

    }

    @Override
    public void deleteUser(String userId) throws ConnectorException {

    }

    @Override
    public void deleteGroup(String groupId) throws ConnectorException {

    }

    @Override
    public List<StubUser> getUsers() throws ConnectorException {
        StubUser user1 = new StubUser();
        user1.setId(UUID.randomUUID().toString());
        user1.setUserName("Ying");
        user1.setEmail("ying@yahoo.com");

        StubUser user2 = new StubUser();
        user2.setId(UUID.randomUUID().toString());
        user2.setUserName("Yang");
        user2.setEmail("yang@yahoo.com");

        return new ArrayList<>(Arrays.asList(user1, user2));
    }

    @Override
    public List<StubGroup> getGroups() throws ConnectorException {
        StubGroup group1 = new StubGroup();
        group1.setId(UUID.randomUUID().toString());
        group1.setName("Uno");

        StubGroup group2 = new StubGroup();
        group2.setId(UUID.randomUUID().toString());
        group2.setName("Dos");
        return new ArrayList<>(Arrays.asList(group1, group2));
    }

    @Override
    public StubUser getUser(String userId) throws ConnectorException {
        StubUser user1 = new StubUser();
        user1.setId(userId);
        user1.setUserName("Ying");
        user1.setEmail("ying@yahoo.com");
        return user1;
    }

    @Override
    public StubGroup getGroup(String groupId) throws ConnectorException {
        StubGroup group1 = new StubGroup();
        group1.setId(UUID.randomUUID().toString());
        group1.setName("Uno");
        return group1;
    }

    @Override
    public void addGroupToUser(String groupId, String userId) throws ConnectorException {

    }

    @Override
    public void removeGroupFromUser(String groupId, String userId) throws ConnectorException {

    }
}
