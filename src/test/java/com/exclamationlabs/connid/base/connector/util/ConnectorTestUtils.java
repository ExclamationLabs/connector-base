package com.exclamationlabs.connid.base.connector.util;

import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ResultsHandler;

import java.util.List;

public class ConnectorTestUtils {

    private ConnectorTestUtils() {}

    public static ResultsHandler buildResultsHandler(List<String> idValues, List<String> nameValues) {
        return (ConnectorObject connectorObject) -> {
            idValues.add(connectorObject.getUid().getUidValue());
            nameValues.add(connectorObject.getName().getNameValue());
            return true;
        };

    }
}
