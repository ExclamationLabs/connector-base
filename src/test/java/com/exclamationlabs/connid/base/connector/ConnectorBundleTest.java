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

package com.exclamationlabs.connid.base.connector;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.identityconnectors.framework.impl.api.local.LocalConnectorInfoManagerImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * If a Connector is having major issues and cannot load up in Midpoint, this can be modified and
 * used as a means of diagnosis.
 *
 * <p>Because of classloader issues, the test is unlikely to ever be successful, but will allow to
 * see better error messages as to what is wrong with the connector.
 */
public class ConnectorBundleTest {

  @Test
  @Disabled
  public void test() throws MalformedURLException {
    List<URL> bundleUrls = new ArrayList<>();
    bundleUrls.add(
        new URL(
            "/somemidpointdir/midpoint-4.0.1/var/icf-connectors/connector-base-zoom-0.1-connector.jar"));

    /*
    LocalConnectorInfoManagerImpl connectorInfoManager =
            new LocalConnectorInfoManagerImpl(bundleUrls, ClassLoader.getSystemClassLoader());

     */
    LocalConnectorInfoManagerImpl connectorInfoManager =
        new LocalConnectorInfoManagerImpl(bundleUrls, null);
    assertNotNull(connectorInfoManager.getConnectorInfos());
    assertFalse(connectorInfoManager.getConnectorInfos().isEmpty());
  }
}
