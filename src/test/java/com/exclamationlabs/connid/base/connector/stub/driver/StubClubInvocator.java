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
import com.exclamationlabs.connid.base.connector.stub.model.StubClub;
import com.exclamationlabs.connid.base.connector.stub.util.StubInvocationChecker;
import java.util.*;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class StubClubInvocator implements DriverInvocator<ComplexStubDriver, StubClub> {

  @Override
  public String create(ComplexStubDriver driver, StubClub model) throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("club create");
    StubInvocationChecker.setMethodParameter1(model);
    return UUID.randomUUID().toString();
  }

  @Override
  public void update(ComplexStubDriver driver, String userId, StubClub model)
      throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("club update");
    StubInvocationChecker.setMethodParameter1(userId);
    StubInvocationChecker.setMethodParameter2(model);
  }

  @Override
  public void delete(ComplexStubDriver driver, String id) throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("club delete");
    StubInvocationChecker.setMethodParameter1(id);
  }

  @Override
  public Set<StubClub> getAll(
      ComplexStubDriver driver, ResultsFilter filter, ResultsPaginator paginator, Integer resultCap)
      throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("club getAll");
    StubClub item1 = new StubClub();
    item1.setId(UUID.randomUUID().toString());
    item1.setName("Club Uno");

    StubClub item2 = new StubClub();
    item2.setId(UUID.randomUUID().toString());
    item2.setName("Club Dos");
    return new HashSet<>(Arrays.asList(item1, item2));
  }

  @Override
  public StubClub getOne(ComplexStubDriver driver, String id, Map<String, Object> data)
      throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("group getOne");
    StubInvocationChecker.setMethodParameter1(id);
    StubClub item = new StubClub();
    item.setId(UUID.randomUUID().toString());
    item.setName("A Club");

    return item;
  }
}
