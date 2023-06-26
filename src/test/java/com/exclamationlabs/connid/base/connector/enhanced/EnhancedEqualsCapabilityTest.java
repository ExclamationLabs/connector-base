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

package com.exclamationlabs.connid.base.connector.enhanced;

import static com.exclamationlabs.connid.base.connector.adapter.SearchExecutor.DEFAULT_FILTER_PAGE_SIZE;
import static org.junit.jupiter.api.Assertions.*;

import com.exclamationlabs.connid.base.connector.adapter.FilterCapableSource;
import com.exclamationlabs.connid.base.connector.stub.EnhancedPFConnector;
import com.exclamationlabs.connid.base.connector.stub.adapter.EnhancedPFUserAdapter;
import com.exclamationlabs.connid.base.connector.stub.attribute.EnhancedPFUserAttribute;
import com.exclamationlabs.connid.base.connector.stub.configuration.EnhancedPFConfiguration;
import com.exclamationlabs.connid.base.connector.stub.driver.EnhancedPFDriver;
import com.exclamationlabs.connid.base.connector.test.ApiIntegrationTest;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.StartsWithFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnhancedEqualsCapabilityTest
    extends ApiIntegrationTest<
        EnhancedPFConfiguration, EnhancedEqualsCapabilityTest.TestConnector> {

  public static class TestConnector extends EnhancedPFConnector {

    public TestConnector() {
      super();
      setAdapters(new TestAdapter());
      setDriver(new EnhancedPFDriver(false, true, false));
    }
  }

  public static class TestAdapter extends EnhancedPFUserAdapter implements FilterCapableSource {
    @Override
    public boolean getSearchResultsContainsAllAttributes() {
      return false;
    }

    @Override
    public boolean getSearchResultsContainsNameAttribute() {
      return true;
    }

    @Override
    public Set<String> getEqualsFilterAttributes() {
      Set<String> attributes = new HashSet<>();
      Arrays.stream(EnhancedPFUserAttribute.values()).forEach(item -> attributes.add(item.name()));
      attributes.remove(EnhancedPFUserAttribute.DEPARTMENT.name());
      return attributes;
    }

    @Override
    public Set<String> getContainsFilterAttributes() {
      return Collections.emptySet();
    }

    @Override
    public boolean getIdContainsFilterSupported() {
      return true;
    }

    @Override
    public boolean getNameContainsFilterSupported() {
      return true;
    }
  }

  @Override
  protected EnhancedPFConfiguration getConfiguration() {
    return new EnhancedPFConfiguration();
  }

  @Override
  protected Class<EnhancedEqualsCapabilityTest.TestConnector> getConnectorClass() {
    return EnhancedEqualsCapabilityTest.TestConnector.class;
  }

  @Override
  protected void readConfiguration(EnhancedPFConfiguration configuration) {}

  @BeforeEach
  public void setup() {
    super.setup();
  }

  @Test
  public void testImportAll() {
    results = new ArrayList<>();
    getConnectorFacade()
        .search(new ObjectClass("pfUser"), null, handler, new OperationOptionsBuilder().build());
    assertEquals(45, results.size());
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testImportPage() {
    results = new ArrayList<>();
    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            null,
            handler,
            new OperationOptionsBuilder().setPagedResultsOffset(1).setPageSize(15).build());
    assertEquals(15, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1001", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));

    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testImportPage2() {
    results = new ArrayList<>();
    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            null,
            handler,
            new OperationOptionsBuilder().setPagedResultsOffset(11).setPageSize(10).build());
    assertEquals(10, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1011", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));

    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserGetObject() {
    ConnectorObject response =
        getConnectorFacade()
            .getObject(
                new ObjectClass("pfUser"), new Uid("1009"), new OperationOptionsBuilder().build());
    assertNotNull(response);
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1009", response.getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "rhenderson@test.com",
            response.getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            response
                .getAttributeByName(EnhancedPFUserAttribute.DETAIL.name())
                .getValue()
                .get(0)
                .toString()));
  }

  @Test
  public void testUserEqualsIdFilter() {
    results = new ArrayList<>();
    Attribute attribute = new AttributeBuilder().setName(Uid.NAME).addValue("1009").build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new EqualsFilter(attribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(1, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1009", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "rhenderson@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.isNotBlank(
            results
                .get(0)
                .getAttributeByName(EnhancedPFUserAttribute.DETAIL.name())
                .getValue()
                .get(0)
                .toString()));
  }

  @Test
  public void testUserContainsIdFilter1() {
    results = new ArrayList<>();
    Attribute attribute = new AttributeBuilder().setName(Uid.NAME).addValue("1009").build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new ContainsFilter(attribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(1, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1009", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "rhenderson@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserContainsIdFilter2() {
    results = new ArrayList<>();
    Attribute attribute = new AttributeBuilder().setName(Uid.NAME).addValue("10").build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new ContainsFilter(attribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(DEFAULT_FILTER_PAGE_SIZE, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1001", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "tcobb@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserContainsIdFilterPage() {
    results = new ArrayList<>();
    Attribute attribute = new AttributeBuilder().setName(Uid.NAME).addValue("10").build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new ContainsFilter(attribute),
            handler,
            new OperationOptionsBuilder().setPageSize(10).setPagedResultsOffset(11).build());
    assertEquals(10, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1011", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "rjackson@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserEqualsFilterName() {
    results = new ArrayList<>();
    Attribute attribute =
        new AttributeBuilder().setName(Name.NAME).addValue("rhenderson@test.com").build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new EqualsFilter(attribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(1, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1009", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "rhenderson@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserContainsFilterName() {
    results = new ArrayList<>();
    Attribute attribute =
        new AttributeBuilder().setName(Name.NAME).addValue("rhenderson@test.com").build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new ContainsFilter(attribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(1, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1009", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "rhenderson@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserContainsFilterNamePartialMatch() {
    results = new ArrayList<>();
    Attribute attribute = new AttributeBuilder().setName(Name.NAME).addValue("ripken").build();
    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new ContainsFilter(attribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(2, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1002", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "cripkenjr@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserContainsFilterNamePartialMatch2() {
    results = new ArrayList<>();
    Attribute attribute = new AttributeBuilder().setName(Name.NAME).addValue("test").build();
    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new ContainsFilter(attribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(DEFAULT_FILTER_PAGE_SIZE, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1001", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "tcobb@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserContainsFilterNamePartialMatch3() {
    results = new ArrayList<>();
    Attribute attribute = new AttributeBuilder().setName(Name.NAME).addValue("test").build();
    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new ContainsFilter(attribute),
            handler,
            new OperationOptionsBuilder().setPageSize(10).setPagedResultsOffset(11).build());
    assertEquals(10, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1011", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "rjackson@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserLocationEqualsFilterName() {
    results = new ArrayList<>();
    Attribute attribute =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.LOCATION.name())
            .addValue("New York")
            .build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new EqualsFilter(attribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(4, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1006", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "mmantle@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserLocationEqualsFilterNamePage() {
    results = new ArrayList<>();
    Attribute attribute =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.LOCATION.name())
            .addValue("New York")
            .build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new EqualsFilter(attribute),
            handler,
            new OperationOptionsBuilder().setPagedResultsOffset(3).setPageSize(2).build());
    assertEquals(2, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1018", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "bruth@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserJobTitleContainsFilterName() {
    results = new ArrayList<>();
    Attribute attribute =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.JOB_TITLE.name())
            .addValue("Outfielder")
            .build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new ContainsFilter(attribute),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(4, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1001", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "tcobb@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserJobTitleContainsFilterNamePage2() {
    results = new ArrayList<>();
    Attribute attribute =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.JOB_TITLE.name())
            .addValue("Outfielder")
            .build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new ContainsFilter(attribute),
            handler,
            new OperationOptionsBuilder().setPageSize(2).setPagedResultsOffset(3).build());
    assertEquals(2, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1014", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "tgwynn@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testBadFilter() {
    results = new ArrayList<>();
    Attribute attribute =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.LOCATION.name())
            .addValue("New")
            .build();

    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            getConnectorFacade()
                .search(
                    new ObjectClass("pfUser"),
                    new StartsWithFilter(attribute),
                    handler,
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testBadFilterEqualsAttributeUnsupported() {
    results = new ArrayList<>();
    Attribute attribute =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.DEPARTMENT.name())
            .addValue("Test")
            .build();

    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            getConnectorFacade()
                .search(
                    new ObjectClass("pfUser"),
                    new EqualsFilter(attribute),
                    handler,
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testBadFilterContainsAttributeUnsupported() {
    results = new ArrayList<>();
    Attribute attribute =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.DEPARTMENT.name())
            .addValue("Test")
            .build();

    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            getConnectorFacade()
                .search(
                    new ObjectClass("pfUser"),
                    new ContainsFilter(attribute),
                    handler,
                    new OperationOptionsBuilder().build()));
  }
}
