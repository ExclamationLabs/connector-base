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
import java.util.Collections;
import java.util.Map;
import org.identityconnectors.framework.common.objects.filter.Filter;

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

  private Map<String, String> andFilterDataMap;

  private FilterType andFilterType;

  private FilterType filterType;

  public ResultsFilter() {
    setAttribute(null);
    setValue(null);
    setFilterType(null);
    setAndFilterType(null);
    setAndFilterDataMap(Collections.emptyMap());
  }

  public ResultsFilter(String filterAttribute, String filterValue) {
    setAttribute(filterAttribute);
    setValue(filterValue);
    setFilterType(FilterType.ContainsFilter);
    setAndFilterType(null);
    setAndFilterDataMap(Collections.emptyMap());
  }

  public ResultsFilter(String filterAttribute, String filterValue, FilterType filterType) {
    setAttribute(filterAttribute);
    setValue(filterValue);
    setFilterType(filterType);
    setAndFilterType(null);
    setAndFilterDataMap(Collections.emptyMap());
  }

  public ResultsFilter(
      String filterAttribute, String filterValue, Class<? extends Filter> filterType) {
    setAttribute(filterAttribute);
    setValue(filterValue);
    setAndFilterType(null);
    setAndFilterDataMap(Collections.emptyMap());
    FilterType type;
    switch (filterType.getSimpleName()) {
      case "EqualsFilter":
        type = FilterType.EqualsFilter;
        break;
      case "AndFilter":
        type = FilterType.AndFilter;
        break;
      default:
        type = FilterType.ContainsFilter;
        break;
    }
    setFilterType(type);
  }

  public ResultsFilter(Map<String, String> andFilterDataMap, FilterType andFilterType) {
    this.andFilterDataMap = andFilterDataMap;
    setValue(null);
    setAttribute(null);
    setFilterType(FilterType.AndFilter);
    setAndFilterType(andFilterType);
  }

  public boolean hasFilter() {
    return (getAttribute() != null && getValue() != null && getFilterType() != null)
        || ((!getAndFilterDataMap().isEmpty()) && getAndFilterType() != null);
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

  public Map<String, String> getAndFilterDataMap() {
    return andFilterDataMap;
  }

  public void setAndFilterDataMap(Map<String, String> andFilterDataMap) {
    this.andFilterDataMap = andFilterDataMap;
  }

  public FilterType getAndFilterType() {
    return andFilterType;
  }

  public void setAndFilterType(FilterType andFilterType) {
    this.andFilterType = andFilterType;
  }

  @Override
  public String toString() {
    return hasFilter()
        ? String.format("%s:%s (%s)", getAttribute(), getValue(), getFilterType())
        : "none";
  }
}
