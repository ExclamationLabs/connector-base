package com.exclamationlabs.connid.base.connector;

import org.identityconnectors.framework.impl.api.local.LocalConnectorInfoManagerImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

/**
 * If a Connector is having major issues and cannot load up in Midpoint, this can
 * be modified and used as a means of diagnosis.
 *
 * Because of classloader issues, the test is unlikely to ever be successful, but
 * will allow to see better error messages as to what is wrong with the connector.
 */
public class ConnectorBundleTest {

    @Test
    @Ignore
    public void test() throws MalformedURLException {
        List<URL> bundleUrls = new ArrayList<>();
        bundleUrls.add(new URL("file:/Users/mneugebauer/dist/midpoint-4.0.1/var/icf-connectors/connector-base-zoom-0.1-connector.jar"));
        //bundleUrls.add(new URL("file:/Users/mneugebauer/dist/midpoint-4.0.1/var/icf-conn-bak/connector-zoom-1.0-SNAPSHOT-connector.jar"));


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
