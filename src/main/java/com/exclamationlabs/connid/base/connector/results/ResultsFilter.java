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

package com.exclamationlabs.connid.base.connector.results;

import com.exclamationlabs.connid.base.connector.filter.FilterType;
import org.identityconnectors.framework.common.objects.filter.AttributeFilter;

/**
 * ResultsFilter is used to store, if applicable, the attribute name and value that the user wishes
 * to filter data on. The driver/invocator getAll method may or may not support results filtering
 * (this is determined by getEnhancedFiltering() and getFilterAttributes() in the Connector).
 *
 * <p>Note: If deemed useful, this object could possibly be enhanced in the future to support
 * multiple filter attribute/value pairs, instead of a single one.
 */
public class ResultsFilter {

  private String attribute;
  private String value;

  private FilterType filterType;

  public ResultsFilter() {
    setAttribute(null);
    setValue(null);
    setFilterType(null);
  }

  public ResultsFilter(String filterAttribute, String filterValue) {
    setAttribute(filterAttribute);
    setValue(filterValue);
    setFilterType(FilterType.ContainsFilter);
  }

  public ResultsFilter(String filterAttribute, String filterValue, FilterType filterType) {
    setAttribute(filterAttribute);
    setValue(filterValue);
    setFilterType(filterType);
  }

  public ResultsFilter(
      String filterAttribute, String filterValue, Class<? extends AttributeFilter> filterType) {
    setAttribute(filterAttribute);
    setValue(filterValue);
    FilterType type;
    switch (filterType.getSimpleName()) {
      case "EqualsFilter":
        type = FilterType.EqualsFilter;
        break;
      case "EndsWithFilter":
        type = FilterType.EndsWithFilter;
        break;
      case "StartsWithFilter":
        type = FilterType.StartsWithFilter;
        break;
      case "GreaterThanFilter":
        type = FilterType.GreaterThanFilter;
        break;
      case "GreaterThanOrEqualFilter":
        type = FilterType.GreaterThanOrEqualFilter;
        break;
      case "LessThanFilter":
        type = FilterType.LessThanFilter;
        break;
      case "LessThanOrEqualFilter":
        type = FilterType.LessThanOrEqualFilter;
        break;
      default:
        type = FilterType.ContainsFilter;
        break;
    }
    setFilterType(type);
  }

  public boolean hasFilter() {
    return getAttribute() != null && getValue() != null && getFilterType() != null;
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public FilterType getFilterType() {
    return filterType;
  }

  public void setFilterType(FilterType filterType) {
    this.filterType = filterType;
  }

  @Override
  public String toString() {
    return hasFilter()
        ? String.format("%s:%s (%s)", getAttribute(), getValue(), getFilterType())
        : "none";
  }
}
