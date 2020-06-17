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

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.BaseConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.stub.model.StubGroup;
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;

public class StubDriver implements Driver<StubUser, StubGroup> {

    private String methodInvoked;
    private Object methodParameter1;
    private Object methodParameter2;
    private boolean initializeInvoked = false;

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return null;
    }

    @Override
    public void initialize(BaseConnectorConfiguration configuration, Authenticator authenticator) throws ConnectorException {
        initializeInvoked = true;
    }

    @Override
    public void test() throws ConnectorException {

    }

    @Override
    public void close() {

    }

    @Override
    public String createUser(StubUser userModel) throws ConnectorException {
        setMethodInvoked("createUser");
        setMethodParameter1(userModel);
        return UUID.randomUUID().toString();
    }

    @Override
    public String createGroup(StubGroup groupModel) throws ConnectorException {
        setMethodInvoked("createGroup");
        setMethodParameter1(groupModel);
        return UUID.randomUUID().toString();
    }

    @Override
    public void updateUser(String userId, StubUser userModel) throws ConnectorException {
        setMethodInvoked("updateUser");
        setMethodParameter1(userId);
        setMethodParameter2(userModel);
    }

    @Override
    public void updateGroup(String groupId, StubGroup groupModel) throws ConnectorException {
        setMethodInvoked("updateGroup");
        setMethodParameter1(groupId);
        setMethodParameter2(groupModel);
    }

    @Override
    public void deleteUser(String userId) throws ConnectorException {
        setMethodInvoked("deleteUser");
        setMethodParameter1(userId);
    }

    @Override
    public void deleteGroup(String groupId) throws ConnectorException {
        setMethodInvoked("deleteGroup");
        setMethodParameter1(groupId);
    }

    @Override
    public List<StubUser> getUsers() throws ConnectorException {
        setMethodInvoked("getUsers");
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
        setMethodInvoked("getGroups");
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
        setMethodInvoked("getUser");
        setMethodParameter1(userId);
        StubUser user1 = new StubUser();
        user1.setId(userId);
        user1.setUserName("Ying");
        user1.setEmail("ying@yahoo.com");
        user1.setGroupIds(Arrays.asList("alpha", "beta"));
        return user1;
    }

    @Override
    public StubGroup getGroup(String groupId) throws ConnectorException {
        setMethodInvoked("getGroup");
        setMethodParameter1(groupId);
        StubGroup group1 = new StubGroup();
        group1.setId(UUID.randomUUID().toString());
        group1.setName("Uno");
        return group1;
    }

    @Override
    public void addGroupToUser(String groupId, String userId) throws ConnectorException {
        setMethodInvoked("addGroupToUser");
        setMethodParameter1(groupId);
        setMethodParameter2(userId);
    }

    @Override
    public void removeGroupFromUser(String groupId, String userId) throws ConnectorException {
        setMethodInvoked("removeGroupFromUser");
        setMethodParameter1(groupId);
        setMethodParameter2(userId);
    }

    public String getMethodInvoked() {
        return methodInvoked;
    }

    private void setMethodInvoked(String methodInvoked) {
        this.methodInvoked = methodInvoked;
    }

    public Object getMethodParameter1() {
        return methodParameter1;
    }

    private void setMethodParameter1(Object methodParameter1) {
        this.methodParameter1 = methodParameter1;
    }

    public Object getMethodParameter2() {
        return methodParameter2;
    }

    private void setMethodParameter2(Object methodParameter2) {
        this.methodParameter2 = methodParameter2;
    }

    public boolean isInitializeInvoked() {
        return initializeInvoked;
    }

}
