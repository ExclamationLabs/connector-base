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

package com.exclamationlabs.connid.base.connector.filter;

import static org.junit.jupiter.api.Assertions.*;

import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.stub.adapter.StubUsersAdapter;
import java.util.Collections;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.filter.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FilterValidatorTest {

  BaseAdapter<?, ?> testAdapter;

  @BeforeEach
  public void setup() {
    testAdapter = new StubUsersAdapter();
  }

  @Test
  public void validEqualsFilter() {
    Attribute attribute = new AttributeBuilder().setName("yin").addValue("yang").build();
    FilterValidator.validate(new EqualsFilter(attribute), testAdapter);
  }

  @Test
  public void validContainsFilter() {
    Attribute attribute = new AttributeBuilder().setName("yin").addValue("yang").build();
    FilterValidator.validate(new ContainsFilter(attribute), testAdapter);
  }

  @Test
  public void validAndEqualsFilter() {
    Attribute attribute1 = new AttributeBuilder().setName("yin").addValue("yang").build();
    EqualsFilter filter1 = new EqualsFilter(attribute1);
    Attribute attribute2 = new AttributeBuilder().setName("corn").addValue("pops").build();
    EqualsFilter filter2 = new EqualsFilter(attribute2);
    Attribute attribute3 = new AttributeBuilder().setName("alpha").addValue("omega").build();
    EqualsFilter filter3 = new EqualsFilter(attribute3);
    AndFilter andFilter = new AndFilter(Set.of(filter1, filter2, filter3));
    FilterValidator.validate(andFilter, testAdapter);
  }

  @Test
  public void validAndContainsFilter() {
    Attribute attribute1 = new AttributeBuilder().setName("yin").addValue("yang").build();
    ContainsFilter filter1 = new ContainsFilter(attribute1);
    Attribute attribute2 = new AttributeBuilder().setName("corn").addValue("pops").build();
    ContainsFilter filter2 = new ContainsFilter(attribute2);
    Attribute attribute3 = new AttributeBuilder().setName("alpha").addValue("omega").build();
    ContainsFilter filter3 = new ContainsFilter(attribute3);
    AndFilter andFilter = new AndFilter(Set.of(filter1, filter2, filter3));
    FilterValidator.validate(andFilter, testAdapter);
  }

  @Test
  public void unsupportedFilterType() {
    Attribute attribute = new AttributeBuilder().setName("yin").addValue("yang").build();
    assertThrows(
        InvalidAttributeValueException.class,
        () -> FilterValidator.validate(new EndsWithFilter(attribute), testAdapter));
  }

  @Test
  public void hasNothingAttributeValue() {
    Attribute attribute = new AttributeBuilder().setName("yin").build();
    assertThrows(
        InvalidAttributeValueException.class,
        () -> FilterValidator.validate(new EqualsFilter(attribute), testAdapter));
  }

  @Test
  public void hasEmptyAttributeValue() {
    Attribute attribute = new AttributeBuilder().setName("yin").addValue("").build();
    assertThrows(
        InvalidAttributeValueException.class,
        () -> FilterValidator.validate(new EqualsFilter(attribute), testAdapter));
  }

  @Test
  public void hasBlankAttributeValue() {
    Attribute attribute = new AttributeBuilder().setName("yin").addValue(" ").build();
    assertThrows(
        InvalidAttributeValueException.class,
        () -> FilterValidator.validate(new ContainsFilter(attribute), testAdapter));
  }

  @Test
  public void emptyAndFilter() {
    assertThrows(
        InvalidAttributeValueException.class,
        () -> FilterValidator.validate(new AndFilter(Collections.emptySet()), testAdapter));
  }

  @Test
  public void soloAndFilter() {
    Attribute attribute = new AttributeBuilder().setName("yin").addValue("yang").build();
    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            FilterValidator.validate(
                new AndFilter(Collections.singleton(new EqualsFilter(attribute))), testAdapter));
  }

  @Test
  public void nestedAndFilter() {
    Attribute attribute1 = new AttributeBuilder().setName("yin").addValue("yang").build();
    ContainsFilter filter1 = new ContainsFilter(attribute1);
    Attribute attribute2 = new AttributeBuilder().setName("corn").addValue("pops").build();
    ContainsFilter filter2 = new ContainsFilter(attribute2);
    Attribute attribute3 = new AttributeBuilder().setName("alpha").addValue("omega").build();
    ContainsFilter filter3 = new ContainsFilter(attribute3);
    AndFilter andFilter = new AndFilter(Set.of(filter1, filter2, filter3));

    assertThrows(
        InvalidAttributeValueException.class,
        () ->
            FilterValidator.validate(new AndFilter(Collections.singleton(andFilter)), testAdapter));
  }

  @Test
  public void mixedAndFilter() {
    Attribute attribute1 = new AttributeBuilder().setName("yin").addValue("yang").build();
    ContainsFilter filter1 = new ContainsFilter(attribute1);
    Attribute attribute2 = new AttributeBuilder().setName("corn").addValue("pops").build();
    EqualsFilter filter2 = new EqualsFilter(attribute2);
    Attribute attribute3 = new AttributeBuilder().setName("alpha").addValue("omega").build();
    ContainsFilter filter3 = new ContainsFilter(attribute3);
    Attribute attribute4 = new AttributeBuilder().setName("pop").addValue("tarts").build();
    EqualsFilter filter4 = new EqualsFilter(attribute4);
    AndFilter andFilter = new AndFilter(Set.of(filter1, filter2, filter3, filter4));
    assertThrows(
        InvalidAttributeValueException.class,
        () -> FilterValidator.validate(andFilter, testAdapter));
  }

  @Test
  public void andFilterRepeatedAttribute() {
    Attribute attribute1 = new AttributeBuilder().setName("yin").addValue("yang").build();
    ContainsFilter filter1 = new ContainsFilter(attribute1);
    Attribute attribute2 = new AttributeBuilder().setName("yin").addValue("yang2").build();
    ContainsFilter filter2 = new ContainsFilter(attribute2);
    AndFilter andFilter = new AndFilter(Set.of(filter1, filter2));
    assertThrows(
        InvalidAttributeValueException.class,
        () -> FilterValidator.validate(andFilter, testAdapter));
  }
}
