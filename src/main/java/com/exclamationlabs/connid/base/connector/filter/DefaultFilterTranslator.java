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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.*;

/**
 * A filter translator can be used in Midpoint to filter by a field on a resource in the Midpoint
 * UI. This implementation is very clumsy, however, and has not been leveraged often up to this
 * point, so we have defined a simple default behavior Midpoint can use.
 *
 * <p>At this time, filtering for And/Or conditions or ContainsAllValue support is not included.
 */
public class DefaultFilterTranslator extends AbstractFilterTranslator<Filter> {

  private static final Set<String> standardAttributeNames;
  @Deprecated private final Set<String> acceptableAttributeNames;

  @Deprecated private boolean checkNames = true;

  static {
    standardAttributeNames = new HashSet<>(Arrays.asList(Uid.NAME, "Id", Name.NAME, "Name"));
  }

  public DefaultFilterTranslator() {
    acceptableAttributeNames = Collections.emptySet();
  }

  @Deprecated
  public DefaultFilterTranslator(Set<String> attributes) {
    acceptableAttributeNames = attributes;
  }

  @Deprecated
  public DefaultFilterTranslator(Set<String> attributes, boolean checkAttributeName) {
    acceptableAttributeNames = attributes;
    checkNames = checkAttributeName;
  }

  @Override
  // Not normally invoked by Midpoint
  protected AttributeFilter createEqualsExpression(EqualsFilter filter, boolean not) {
    if (filter == null || not) {
      return null;
    }
    validateAttributeName(filter);
    return filter;
  }

  @Override
  protected AttributeFilter createContainsAllValuesExpression(
      ContainsAllValuesFilter filter, boolean not) {
    if (filter == null || not) {
      return null;
    }
    validateAttributeName(filter);
    return filter;
  }

  @Override
  // Normally invoked by Midpoint for filtering
  protected AttributeFilter createContainsExpression(ContainsFilter filter, boolean not) {
    if (filter == null || not) {
      return null;
    }
    validateAttributeName(filter);
    return filter;
  }

  @Override
  protected AttributeFilter createStartsWithExpression(StartsWithFilter filter, boolean not) {
    if (filter == null || not) {
      return null;
    }
    validateAttributeName(filter);
    return filter;
  }

  @Override
  protected AttributeFilter createEndsWithExpression(EndsWithFilter filter, boolean not) {
    if (filter == null || not) {
      return null;
    }
    validateAttributeName(filter);
    return filter;
  }

  @Override
  protected AttributeFilter createGreaterThanExpression(GreaterThanFilter filter, boolean not) {
    if (filter == null || not) {
      return null;
    }
    validateAttributeName(filter);
    return filter;
  }

  @Override
  protected AttributeFilter createGreaterThanOrEqualExpression(
      GreaterThanOrEqualFilter filter, boolean not) {
    if (filter == null || not) {
      return null;
    }
    validateAttributeName(filter);
    return filter;
  }

  @Override
  protected AttributeFilter createLessThanExpression(LessThanFilter filter, boolean not) {
    if (filter == null || not) {
      return null;
    }
    validateAttributeName(filter);
    return filter;
  }

  @Override
  protected AttributeFilter createLessThanOrEqualExpression(
      LessThanOrEqualFilter filter, boolean not) {
    if (filter == null || not) {
      return null;
    }
    validateAttributeName(filter);
    return filter;
  }

  protected void validateAttributeName(AttributeFilter filter) {
    if (standardAttributeNames.contains(filter.getName())) {
      return;
    }

    if (checkNames && (!acceptableAttributeNames.contains(filter.getName()))) {
      throw new InvalidAttributeValueException(
          "Unsupported filter attribute with name " + filter.getName());
    }
  }
}
