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
import org.identityconnectors.framework.common.objects.filter.AndFilter;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnhancedAndApiCapabilityTest
    extends ApiIntegrationTest<
        EnhancedPFConfiguration, EnhancedAndApiCapabilityTest.TestConnector> {

  public static class TestConnector extends EnhancedPFConnector {

    public TestConnector() {
      super();
      setAdapters(new TestAdapter());
      setDriver(new EnhancedPFDriver(false, false, false));
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
      Set<String> attributes = new HashSet<>();
      Arrays.stream(EnhancedPFUserAttribute.values()).forEach(item -> attributes.add(item.name()));
      attributes.remove(EnhancedPFUserAttribute.DEPARTMENT.name());
      return attributes;
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
  protected Class<EnhancedAndApiCapabilityTest.TestConnector> getConnectorClass() {
    return EnhancedAndApiCapabilityTest.TestConnector.class;
  }

  @Override
  protected void readConfiguration(EnhancedPFConfiguration configuration) {}

  @BeforeEach
  public void setup() {
    super.setup();
  }

  @Test
  public void testUserLocationJobEqualsFilterName() {
    results = new ArrayList<>();
    Attribute attribute1 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.LOCATION.name())
            .addValue("New York")
            .build();

    Attribute attribute2 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.JOB_TITLE.name())
            .addValue("Starting Pitcher")
            .build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new AndFilter(new EqualsFilter(attribute1), new EqualsFilter(attribute2)),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(2, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1007", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "dgooden@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserLocationJobEqualsNoMatchFilterName() {
    results = new ArrayList<>();
    Attribute attribute1 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.LOCATION.name())
            .addValue("York")
            .build();

    Attribute attribute2 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.JOB_TITLE.name())
            .addValue("Pitcher")
            .build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new AndFilter(new EqualsFilter(attribute1), new EqualsFilter(attribute2)),
            handler,
            new OperationOptionsBuilder().build());
    assertTrue(results.isEmpty());
  }

  @Test
  public void testUserLocationJobContainsFilterName() {
    results = new ArrayList<>();
    Attribute attribute1 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.LOCATION.name())
            .addValue("York")
            .build();

    Attribute attribute2 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.JOB_TITLE.name())
            .addValue("Starting")
            .build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new AndFilter(new ContainsFilter(attribute1), new ContainsFilter(attribute2)),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(2, results.size());
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "1007", results.get(0).getAttributeByName(Uid.NAME).getValue().get(0).toString()));
    assertTrue(
        StringUtils.equalsIgnoreCase(
            "dgooden@test.com",
            results.get(0).getAttributeByName(Name.NAME).getValue().get(0).toString()));
    assertNotNull(
        results.get(0).getAttributeByName(EnhancedPFUserAttribute.DETAIL.name()).getValue().get(0));
  }

  @Test
  public void testUserIdEmailContainsFilterName() {
    results = new ArrayList<>();
    Attribute attribute1 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.USER_ID.name())
            .addValue("101")
            .build();

    Attribute attribute2 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.EMAIL.name())
            .addValue("n@test.com")
            .build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new AndFilter(new ContainsFilter(attribute1), new ContainsFilter(attribute2)),
            handler,
            new OperationOptionsBuilder().build());
    assertEquals(4, results.size());
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
  public void testUserLocationJobContainsNoMatchFilterName() {
    results = new ArrayList<>();
    Attribute attribute1 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.LOCATION.name())
            .addValue("Bogus")
            .build();

    Attribute attribute2 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.JOB_TITLE.name())
            .addValue("Nonsense")
            .build();

    getConnectorFacade()
        .search(
            new ObjectClass("pfUser"),
            new AndFilter(new ContainsFilter(attribute1), new ContainsFilter(attribute2)),
            handler,
            new OperationOptionsBuilder().build());
    assertTrue(results.isEmpty());
  }

  @Test
  public void testBadFilterEqualsAttributeUnsupported() {
    Attribute attribute1 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.DEPARTMENT.name())
            .addValue("Test")
            .build();
    Attribute attribute2 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.LOCATION.name())
            .addValue("Test")
            .build();

    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            getConnectorFacade()
                .search(
                    new ObjectClass("pfUser"),
                    new AndFilter(new EqualsFilter(attribute1), new EqualsFilter(attribute2)),
                    handler,
                    new OperationOptionsBuilder().build()));
  }

  @Test
  public void testBadFilterContainsAttributeUnsupported() {
    Attribute attribute1 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.DEPARTMENT.name())
            .addValue("Test")
            .build();
    Attribute attribute2 =
        new AttributeBuilder()
            .setName(EnhancedPFUserAttribute.LOCATION.name())
            .addValue("Test")
            .build();

    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            getConnectorFacade()
                .search(
                    new ObjectClass("pfUser"),
                    new AndFilter(new ContainsFilter(attribute1), new ContainsFilter(attribute2)),
                    handler,
                    new OperationOptionsBuilder().build()));
  }
}
