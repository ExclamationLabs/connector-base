package com.exclamationlabs.connid.base.connector.driver;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.model.DefaultGroup;
import com.exclamationlabs.connid.base.connector.model.DefaultUser;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.List;
import java.util.UUID;

public class DefaultDriver implements Driver<DefaultUser, DefaultGroup> {
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
    public String createUser(DefaultUser userModel) throws ConnectorException {
        return "User-" + UUID.randomUUID().toString();
    }

    @Override
    public String createGroup(DefaultGroup groupModel) throws ConnectorException {
        return "Group-" + UUID.randomUUID().toString();
    }

    @Override
    public void updateUser(String userId, DefaultUser userModel) throws ConnectorException {

    }

    @Override
    public void updateGroup(String groupId, DefaultGroup groupModel) throws ConnectorException {

    }

    @Override
    public void deleteUser(String userId) throws ConnectorException {

    }

    @Override
    public void deleteGroup(String groupId) throws ConnectorException {

    }

    @Override
    public List<DefaultUser> getUsers() throws ConnectorException {
        return null;
    }

    @Override
    public List<DefaultGroup> getGroups() throws ConnectorException {
        return null;
    }

    @Override
    public DefaultUser getUser(String userId) throws ConnectorException {
        return null;
    }

    @Override
    public DefaultGroup getGroup(String groupId) throws ConnectorException {
        return null;
    }

    @Override
    public void addGroupToUser(String groupId, String userId) throws ConnectorException {

    }

    @Override
    public void removeGroupFromUser(String groupId, String userId) throws ConnectorException {

    }
}
