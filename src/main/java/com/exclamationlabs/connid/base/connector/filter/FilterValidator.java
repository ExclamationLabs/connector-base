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
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.filter.AndFilter;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

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
      AndFilter andFilter = (AndFilter) filter;
      if (andFilter.getFilters().isEmpty()) {
        throw new InvalidAttributeValueException(
            String.format(
                "Invalid request - Adapter %s received an AndFilter with no filters inside.",
                adapter.getClass().getSimpleName()));
      }
      for (Filter innerFilter : andFilter.getFilters()) {
        validate(innerFilter, adapter);
      }
    }
  }
}
