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

import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.filter.*;

/**
 * Validate incoming filters for Base Connector 4.0+ versions with the following restrictions: -
 * Only AndFilter, EqualsFilter and ContainsFilter are supported - ContainsFilter and EqualsFilter
 * must have a non-blank attribute name - ContainsFilter and EqualsFilter must have a non-blank
 * attribute value - AndFilter must have 2 or more filters within it - AndFilter cannot contain
 * another AndFilter - AndFilter cannot contain a mix of EqualsFilters and ContainsFilters -
 * AndFilter cannot have the same attribute more than once If any of these checks fail, an
 * InvalidAttributeValueException is thrown
 *
 * <p>NOTE: The filter may still be invalid and InvalidAttributeValueException thrown if the filters
 * supplied have attributes that cannot be searched upon by the source API or base framework. But
 * this check is not handled here.
 */
public class FilterValidator {

  private FilterValidator() {}

  public static void validate(Filter filter, BaseAdapter<?, ?> adapter)
      throws InvalidAttributeValueException {
    if (!(filter instanceof EqualsFilter
        || filter instanceof ContainsFilter
        || filter instanceof AndFilter)) {
      throw new InvalidAttributeValueException(
          String.format(
              "Adapter %s received an unsupported Filter type %s for attempting get/search",
              adapter.getClass().getSimpleName(), filter.getClass().getSimpleName()));
    }

    if (filter instanceof ContainsFilter) {
      ContainsFilter containsFilter = (ContainsFilter) filter;
      if (StringUtils.isBlank(containsFilter.getValue())) {
        throw new InvalidAttributeValueException(
            String.format(
                "Adapter %s received an empty ContainsFilter value for attribute %s",
                adapter.getClass().getSimpleName(), containsFilter.getName()));
      }
    }

    if (filter instanceof EqualsFilter) {
      EqualsFilter equalsFilter = (EqualsFilter) filter;
      if (equalsFilter.getAttribute().getValue() == null
          || equalsFilter.getAttribute().getValue().isEmpty()
          || equalsFilter.getAttribute().getValue().get(0) == null
          || StringUtils.isBlank(equalsFilter.getAttribute().getValue().get(0).toString())) {
        throw new InvalidAttributeValueException(
            String.format(
                "Adapter %s received an empty EqualsFilter value for attribute %s",
                adapter.getClass().getSimpleName(), equalsFilter.getName()));
      }
    }

    if (filter instanceof AndFilter) {
      validateAndFilter((AndFilter) filter, adapter);
    }
  }

  private static void validateAndFilter(AndFilter andFilter, BaseAdapter<?, ?> adapter) {
    if (andFilter.getFilters().isEmpty()) {
      throw new InvalidAttributeValueException(
          String.format(
              "Invalid request - Adapter %s received an AndFilter with no filters inside.",
              adapter.getClass().getSimpleName()));
    }
    int equalsCounter = 0;
    int containsCounter = 0;
    Set<String> attributeNames = new HashSet<>();
    for (Filter innerFilter : andFilter.getFilters()) {
      if (innerFilter instanceof EqualsFilter) {
        equalsCounter++;

      } else if (innerFilter instanceof ContainsFilter) {
        containsCounter++;
      } else {
        throw new InvalidAttributeValueException(
            String.format(
                "Invalid Request: Adapter %s received nested AndFilters",
                adapter.getClass().getSimpleName()));
      }
      if (equalsCounter > 0 && containsCounter > 0) {
        throw new InvalidAttributeValueException(
            String.format(
                "Invalid Request: Adapter %s received mixed Equals/Contains filters within AndFilter",
                adapter.getClass().getSimpleName()));
      }
      validate(innerFilter, adapter);
      AttributeFilter attributeFilter = (AttributeFilter) innerFilter;
      if (attributeNames.contains(attributeFilter.getName())) {
        throw new InvalidAttributeValueException(
            String.format(
                "Invalid Request: Adapter %s received AndFilter with attribute %s supplied more than once.",
                adapter.getClass().getSimpleName(), attributeFilter.getName()));
      } else {
        attributeNames.add(attributeFilter.getName());
      }
    } // end for filters within AndFilter
    if (equalsCounter == 1 || containsCounter == 1) {
      throw new InvalidAttributeValueException(
          String.format(
              "Invalid Request: Adapter %s received AndFilter with just 1 filter within it.",
              adapter.getClass().getSimpleName()));
    }
  }
}
