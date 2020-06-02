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

package com.exclamationlabs.connid.base.connector.zoom;

import com.exclamationlabs.connid.base.connector.util.ConnectorMockRestTest;
import com.exclamationlabs.connid.base.connector.util.ConnectorTestUtils;
import com.exclamationlabs.connid.base.zoom.ZoomConnector;
import com.exclamationlabs.connid.base.zoom.attribute.ZoomGroupAttribute;
import com.exclamationlabs.connid.base.zoom.attribute.ZoomUserAttribute;
import com.exclamationlabs.connid.base.zoom.configuration.ZoomConfiguration;
import com.exclamationlabs.connid.base.zoom.driver.rest.ZoomDriver;
import com.exclamationlabs.connid.base.zoom.model.ZoomUserType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ZoomConnectorTest extends ConnectorMockRestTest {

    private ZoomConnector connector;

    @Before
    public void setup() {
        connector = new ZoomConnector() {
            @Override
            public void init(Configuration configuration) {
                setAuthenticator(null);
                setDriver(new ZoomDriver() {
                    @Override
                    protected HttpClient createClient() {
                        return stubClient;
                    }
                });
                super.init(configuration);
            }
        };
        ZoomConfiguration configuration = new ZoomConfiguration();
        configuration.setMidPointConfigurationFilePath("src/test/resources/testZoomConfiguration.properties");
        connector.init(configuration);
    }


    @Test
    public void test110UserCreate() {
        final String responseData = "{\"id\":\"keGi76UxSBePr_kFhIaM2Q\",\"first_name\":\"Captain\",\"last_name\":\"America\",\"email\":\"captain@america.com\",\"type\":1}";
        prepareMockResponse(responseData);

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(ZoomUserAttribute.TYPE.name()).addValue("1").build());
        attributes.add(new AttributeBuilder().setName(ZoomUserAttribute.FIRST_NAME.name()).addValue("Fred").build());
        attributes.add(new AttributeBuilder().setName(ZoomUserAttribute.LAST_NAME.name()).addValue("Rubble").build());
        attributes.add(new AttributeBuilder().setName(ZoomUserAttribute.TYPE.name()).addValue(ZoomUserType.BASIC).build());

        attributes.add(new AttributeBuilder().setName(ZoomUserAttribute.EMAIL.name()).addValue("fred@rubble.com").build());

        Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test
    public void test120UserModify() {
        prepareMockResponseEmpty();
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(ZoomUserAttribute.FIRST_NAME.name()).addValue("Wilma").build());

        Uid newId = connector.update(ObjectClass.ACCOUNT, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test
    public void test130UsersGet() {
        String responseData = "{\"page_count\":1,\"page_number\":1,\"page_size\":30,\"total_records\":1,\"users\":[{\"id\":\"ZpRAY4X9SEipRS9kS--Img\",\"first_name\":\"Alfred\",\"last_name\":\"Neuman\",\"email\":\"alfred@mad.com\",\"type\":2,\"pmi\":5825080948,\"timezone\":\"America/Chicago\",\"verified\":0,\"created_at\":\"2020-05-06T19:22:24Z\",\"last_login_time\":\"2020-05-10T19:37:29Z\",\"pic_url\":\"https://lh6.googleusercontent.com/-mboZtlAHsM4/AAAAAAAAAAI/AAAAAAAAAAA/AMZuuclRl5BboLrsXCiJ9dRBBD1yEIG2ww/photo.jpg\",\"language\":\"en-US\",\"phone_number\":\"\",\"status\":\"active\"}]}";
        prepareMockResponse(responseData);

        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.ACCOUNT, "", resultsHandler, new OperationOptionsBuilder().build());
        assertTrue(idValues.size() >= 1);
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void test140UserGet() {
        String responseData = "{\"id\":\"ZpRAY4X9SEipRS9kS--Img\",\"first_name\":\"Alfred\",\"last_name\":\"Neuman\",\"email\":\"alfred@mad.com\",\"type\":2,\"pmi\":5825080948,\"timezone\":\"America/Chicago\",\"verified\":0,\"created_at\":\"2020-05-06T19:22:24Z\",\"last_login_time\":\"2020-05-10T19:37:29Z\",\"pic_url\":\"https://lh6.googleusercontent.com/-mboZtlAHsM4/AAAAAAAAAAI/AAAAAAAAAAA/AMZuuclRl5BboLrsXCiJ9dRBBD1yEIG2ww/photo.jpg\",\"language\":\"en-US\",\"phone_number\":\"\",\"status\":\"active\"}";
        prepareMockResponse(responseData);

        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.ACCOUNT, "1234", resultsHandler, new OperationOptionsBuilder().build());
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    }


    @Test
    public void test210GroupCreate() {
        final String responseData = "{\"id\":\"yRU7LBa6RmenCOjsoEJkxw\",\"name\":\"Alpha Flight\",\"total_members\":0}";
        prepareMockResponse(responseData);
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(ZoomGroupAttribute.GROUP_NAME.name()).addValue("Flinstones").build());

        Uid newId = connector.create(ObjectClass.GROUP, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test
    public void test220GroupModify() {
        prepareMockResponseEmpty();
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(ZoomGroupAttribute.GROUP_NAME.name()).addValue("Flinstones2").build());

        Uid newId = connector.update(ObjectClass.GROUP, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test
    public void test230GroupsGet() {
        String responseData = "{\"total_records\":3,\"groups\":[{\"id\":\"tAKM1nXqSSS4kgtNu91_uQ\",\"name\":\"Alpha Flight\",\"total_members\":0},{\"id\":\"loiFdqtuR4WoCq2Rn3G8uw\",\"name\":\"Avengers\",\"total_members\":0},{\"id\":\"nu7kJQ4PRwWrlyXoGHHopg\",\"name\":\"West Coast Avengers\",\"total_members\":0}]}";
        prepareMockResponse(responseData);
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.GROUP, "", resultsHandler, new OperationOptionsBuilder().build());
        assertTrue(idValues.size() >= 1);
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void test240GroupGet() {
        String responseData = "{\"id\":\"AxBcuvQeQSaP-Imhrludgw\",\"name\":\"Alpha Flight\",\"total_members\":0}";
        prepareMockResponse(responseData);
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.GROUP, "1234", resultsHandler, new OperationOptionsBuilder().build());
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void test290GroupDelete() {
        prepareMockResponseEmpty();
        connector.delete(ObjectClass.GROUP, new Uid("1234"), new OperationOptionsBuilder().build());
    }

    @Test
    public void test390UserDelete() {
        prepareMockResponseEmpty();
        connector.delete(ObjectClass.ACCOUNT, new Uid("1234"), new OperationOptionsBuilder().build());
    }

}
