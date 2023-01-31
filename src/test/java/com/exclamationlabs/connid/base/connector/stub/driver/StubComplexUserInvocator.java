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
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class StubComplexUserInvocator implements DriverInvocator<ComplexStubDriver, StubUser> {

  @Override
  public String create(ComplexStubDriver driver, StubUser model) throws ConnectorException {
    if (StringUtils.equalsIgnoreCase("duplicateId", model.getIdentityNameValue())) {
      driver.setMethodInvoked("got existing id");
      driver.setMethodParameter1(model);
      throw new AlreadyExistsException("test");
    }

    driver.setMethodInvoked("user create");
    if (model.getClubIds() != null && !model.getClubIds().isEmpty()) {
      driver.setMethodInvoked("user create with group and club ids");
    } else {
      if (model.getGroupIds() != null && !model.getGroupIds().isEmpty()) {
        driver.setMethodInvoked("user create with group ids");
      }
    }

    driver.setMethodParameter1(model);
    return UUID.randomUUID().toString();
  }

  @Override
  public void update(ComplexStubDriver driver, String userId, StubUser model)
      throws ConnectorException {
    driver.setMethodInvoked("user update");
    if (model.getGroupIds() != null && !model.getGroupIds().isEmpty()) {
      driver.setMethodInvoked("user update with group ids");
    }
    driver.setMethodParameter1(userId);
    driver.setMethodParameter2(model);
  }

  @Override
  public void delete(ComplexStubDriver driver, String id) throws ConnectorException {
    driver.setMethodInvoked("user delete");
    driver.setMethodParameter1(id);
  }

  @Override
  public Set<StubUser> getAll(
      ComplexStubDriver driver, ResultsFilter filter, ResultsPaginator paginator, Integer resultCap)
      throws ConnectorException {
    final int DEFAULT_NUM_RESULTS = 100;
    Set<StubUser> results = new HashSet<>();

    int effectiveResultCount =
        paginator.getPageSize() == null ? DEFAULT_NUM_RESULTS : paginator.getPageSize();
    if (resultCap != null) {
      effectiveResultCount = resultCap;
    }
    if (paginator.getCurrentOffset() != null && paginator.getCurrentOffset() >= 90) {
      effectiveResultCount = 5;
      paginator.setNoMoreResults(true);
    }

    for (int xx = 0; xx < effectiveResultCount; xx++) {
      String randomId = UUID.randomUUID().toString();
      StubUser user1 = new StubUser();
      user1.setId(randomId);
      user1.setUserName(randomId + "-NAME");
      results.add(user1);
    }
    if (resultCap != null && resultCap == 7 && results.size() == 7) {
      driver.setMethodInvoked("user got seven");
    } else {
      driver.setMethodInvoked("user getAll " + filter);
    }

    return results;
  }

  @Override
  public StubUser getOne(ComplexStubDriver driver, String id, Map<String, Object> data)
      throws ConnectorException {
    driver.setMethodInvoked("user getOne");
    driver.setMethodParameter1(id);
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

  @Override
  public StubUser getOneByName(ComplexStubDriver driver, String objectName)
      throws ConnectorException {
    StubUser user1 = new StubUser();
    user1.setId("dupe");
    user1.setUserName(objectName);
    return user1;
  }
}
