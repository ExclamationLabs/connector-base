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

package com.exclamationlabs.connid.base.connector.stub;

import com.exclamationlabs.connid.base.connector.BaseReadOnlyConnector;
import com.exclamationlabs.connid.base.connector.stub.adapter.StubGroupsAdapter;
import com.exclamationlabs.connid.base.connector.stub.adapter.StubUsersAdapter;
import com.exclamationlabs.connid.base.connector.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.connector.stub.driver.StubDriver;
import org.identityconnectors.framework.spi.ConnectorClass;

@ConnectorClass(displayNameKey = "test.display", configurationClass = StubConfiguration.class)
public class StubReadOnlyConnector extends BaseReadOnlyConnector<StubConfiguration> {

  public StubReadOnlyConnector() {
    super(StubConfiguration.class);
    setDriver(new StubDriver());
    setAdapters(new StubUsersAdapter(), new StubGroupsAdapter());
    setAuthenticator(configuration -> "stubAuth");
  }
}
