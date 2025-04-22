package com.exclamationlabs.connid.base.edition.neo;

import com.exclamationlabs.connid.base.connector.test.util.ConnectorMockRestTest;
import com.exclamationlabs.connid.base.connector.test.util.ConnectorTestUtils;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.RestTestConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.rest.RestConnector;
import com.exclamationlabs.connid.base.edition.neo.util.ConnectivityTester;
import com.exclamationlabs.connid.base.edition.neo.util.ConnectorMockNativeRestTest;
import com.exclamationlabs.connid.base.edition.neo.util.TestPoint;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.SearchResult;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.spi.SearchResultsHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RestConnectorTest extends ConnectorMockNativeRestTest {


    private static final String USER_ID = "123";

    private static final String USER_ID_CUSTOM_RESPONSE = "999111";
    private static final String USER_ID_CUSTOM_RESPONSE2 = "999222";

    private static final String USER_ID_RAW = "456";
    private static final String USER_NAME = "Bob";
    private static final String USER_NAME_RAW = "BobRaw";
    private static final String USER_NAME_RAW2 = "BobRaw2";

    private static final String USER_EMAIL = "bob@bob.com";
    private static final String USER_ID2 = "456";
    private static final String USER_NAME2 = "Jack";
    private static final String USER_EMAIL2 = "jack@jack.com";

    private static final String GROUP_ID = "777";
    private static final String GROUP_NAME = "Rabbits";
    private static final String GROUP_ID2 = "888";
    private static final String GROUP_NAME2 = "Bunnies";

    private static final String TEST_RESPONSE = "{customTestResponse:\"Test was good\"}";

    private static final String SINGLE_USER_RESPONSE =
            "{id:\""
                    + USER_ID
                    + "\", "
                    + " userName:\""
                    + USER_NAME
                    + "\", "
                    + " email:\""
                    + USER_EMAIL
                    + "\" }";
    private static final String SECOND_USER_RESPONSE =
            "{id:\""
                    + USER_ID2
                    + "\", "
                    + " userName:\""
                    + USER_NAME2
                    + "\", "
                    + " email:\""
                    + USER_EMAIL2
                    + "\" }";
    private static final String MULTI_USER_RESPONSE =
            "{people: [ " + SINGLE_USER_RESPONSE + "," + SECOND_USER_RESPONSE + " ]}";

    private static final String MULTI_USER_DUPLICATE_RESPONSE =
            "{people: [ "
                    + SINGLE_USER_RESPONSE
                    + ","
                    + SECOND_USER_RESPONSE
                    + ","
                    + SECOND_USER_RESPONSE
                    + " ]}";

    private static final String SINGLE_GROUP_RESPONSE =
            "{id:\"" + GROUP_ID + "\", " + " name:\"" + GROUP_NAME + "\" }";
    private static final String SECOND_GROUP_RESPONSE =
            "{id:\"" + GROUP_ID2 + "\", " + " name:\"" + GROUP_NAME2 + "\" }";
    private static final String MULTI_GROUP_RESPONSE =
            "{groups: [ " + SINGLE_GROUP_RESPONSE + "," + SECOND_GROUP_RESPONSE + " ]}";

    private RestConnector restConnector;

    @BeforeEach
    public void setup() {
        RestTestConfiguration configuration = new RestTestConfiguration();
        restConnector = new RestConnector();
        restConnector.init(configuration);
        ConnectivityTester.reset();
    }

    @Test
    void test() {
        prepareMockResponse(Collections.emptyMap(), TEST_RESPONSE);
        ConnectivityTester.setMockClient(stubClient);
        restConnector.test();
        assertEquals("Test was good", ConnectivityTester.getPoint(TestPoint.DRIVER_TEST));
    }

    @Test
    void getOneUser() {
        Attribute idAttribute = new AttributeBuilder().setName(Uid.NAME).addValue("1234").build();
        prepareMockResponse(Collections.emptyMap(), SINGLE_USER_RESPONSE);
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);
        ObjectClass oClass = new ObjectClass("RestUser");
        restConnector.executeQuery(oClass, new EqualsFilter(idAttribute), resultsHandler, new OperationOptionsBuilder().build());
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    }
}
