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
import com.exclamationlabs.connid.base.connector.stub.model.StubUser;
import com.exclamationlabs.connid.base.connector.stub.util.StubInvocationChecker;
import java.util.*;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class StubUserInvocator implements DriverInvocator<StubDriver, StubUser> {

  @Override
  public String create(StubDriver driver, StubUser model) throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("user create");
    if (model.getClubIds() != null && !model.getClubIds().isEmpty()) {
      StubInvocationChecker.setMethodInvoked("user create with group and club ids");
    } else {
      if (model.getGroupIds() != null && !model.getGroupIds().isEmpty()) {
        StubInvocationChecker.setMethodInvoked("user create with group ids");
      }
    }

    StubInvocationChecker.setMethodParameter1(model);
    return UUID.randomUUID().toString();
  }

  @Override
  public void update(StubDriver driver, String userId, StubUser model) throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("user update");
    if (model.getGroupIds() != null && !model.getGroupIds().isEmpty()) {
      StubInvocationChecker.setMethodInvoked("user update with group ids");
    }
    StubInvocationChecker.setMethodParameter1(userId);
    StubInvocationChecker.setMethodParameter2(model);
  }

  @Override
  public void delete(StubDriver driver, String id) throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("user delete");
    StubInvocationChecker.setMethodParameter1(id);
  }

  @Override
  public Set<StubUser> getAll(
      StubDriver driver, ResultsFilter filter, ResultsPaginator paginator, Integer resultCap)
      throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("user getAll " + filter);
    StubUser user1 = new StubUser();
    user1.setId(UUID.randomUUID().toString());
    user1.setUserName("User Uno");

    StubUser user2 = new StubUser();
    user2.setId(UUID.randomUUID().toString());
    user2.setUserName("User Dos");
    return new HashSet<>(Arrays.asList(user1, user2));
  }

  @Override
  public StubUser getOne(StubDriver driver, String id, Map<String, Object> data)
      throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("user getOne");
    StubInvocationChecker.setMethodParameter1(id);
    StubUser user1 = new StubUser();
    user1.setId(UUID.randomUUID().toString());
    if (id.equals("filteredId")) {
      user1.setUserName("filteredName");
      user1.setEmail("filtered@yahoo.com");
    } else {
      user1.setUserName("Ying");
      user1.setEmail("ying@yahoo.com");
    }

    return user1;
  }
}
