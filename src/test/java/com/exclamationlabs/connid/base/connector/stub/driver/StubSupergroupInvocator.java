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
import com.exclamationlabs.connid.base.connector.stub.model.StubSupergroup;
import com.exclamationlabs.connid.base.connector.stub.util.StubInvocationChecker;
import java.util.*;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class StubSupergroupInvocator implements DriverInvocator<ComplexStubDriver, StubSupergroup> {

  @Override
  public String create(ComplexStubDriver driver, StubSupergroup model) throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("supergroup create");
    StubInvocationChecker.setMethodParameter1(model);
    return UUID.randomUUID().toString();
  }

  @Override
  public void update(ComplexStubDriver driver, String userId, StubSupergroup model)
      throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("supergroup update");
    StubInvocationChecker.setMethodParameter1(userId);
    StubInvocationChecker.setMethodParameter2(model);
  }

  @Override
  public void delete(ComplexStubDriver driver, String id) throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("supergroup delete");
    StubInvocationChecker.setMethodParameter1(id);
  }

  @Override
  public Set<StubSupergroup> getAll(
      ComplexStubDriver driver, ResultsFilter filter, ResultsPaginator paginator, Integer resultCap)
      throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("club getAll");
    StubSupergroup item1 = new StubSupergroup();
    item1.setId(UUID.randomUUID().toString());
    item1.setName("SuperGroup Uno");

    StubSupergroup item2 = new StubSupergroup();
    item2.setId(UUID.randomUUID().toString());
    item2.setName("SuperGroup Dos");
    return new HashSet<>(Arrays.asList(item1, item2));
  }

  @Override
  public StubSupergroup getOne(ComplexStubDriver driver, String id, Map<String, Object> data)
      throws ConnectorException {
    StubInvocationChecker.setMethodInvoked("group getOne");
    StubInvocationChecker.setMethodParameter1(id);
    StubSupergroup item = new StubSupergroup();
    item.setId(UUID.randomUUID().toString());
    item.setName("A Sueprgroup");

    return item;
  }
}
