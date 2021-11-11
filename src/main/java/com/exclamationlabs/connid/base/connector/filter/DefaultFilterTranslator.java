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

import com.exclamationlabs.connid.base.connector.BaseConnector;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.ContainsAllValuesFilter;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;

import java.util.Collections;
import java.util.Set;

/**
 * A filter translator can be used in Midpoint to filter by a field on a resource
 * in the Midpoint UI.  This implementation is very clumsy, however, and has not
 * been leveraged often up to this point, so we have defined a simple default
 * behavior Midpoint can use.
 */
public class DefaultFilterTranslator extends AbstractFilterTranslator<String> {

    private final Set<String> acceptableAttributeNames;

    public DefaultFilterTranslator() {
        acceptableAttributeNames = Collections.emptySet();
    }

    public DefaultFilterTranslator(Set<String> attributes) {
        acceptableAttributeNames = attributes;
    }

    @Override
    // Not normally invoked by Midpoint
    protected String createEqualsExpression(EqualsFilter filter, boolean not) {
        if (not || filter == null) {
            return null;
        }

        Attribute attr = filter.getAttribute();
        if (!attr.is(Name.NAME) && !attr.is(Uid.NAME)) {
            return null;
        }
        String value = AttributeUtil.getAsStringValue(attr);

        return (checkSearchValue(value) == null) ? null : value;
    }

    @Override
    protected String createContainsAllValuesExpression(ContainsAllValuesFilter filter, boolean not) {
        if (StringUtils.equals(filter.getAttribute().getName(), Uid.NAME) || acceptableAttributeNames.contains(filter.getAttribute().getName())) {
            return filter.getAttribute().getName() + BaseConnector.FILTER_SEPARATOR + filter.getAttribute().getValue().get(0);
        } else {
            throw new InvalidAttributeValueException("Filter on attribute " + filter.getAttribute().getName() + " not supported.");
        }
    }


    @Override
    // Normally invoked by Midpoint for filtering
    protected String createContainsExpression(ContainsFilter filter, boolean not) {
        if (StringUtils.equals(filter.getAttribute().getName(), Uid.NAME) || acceptableAttributeNames.contains(filter.getAttribute().getName())) {
            return filter.getAttribute().getName() + BaseConnector.FILTER_SEPARATOR + filter.getValue();
        } else {
            throw new InvalidAttributeValueException("Filter on attribute " + filter.getAttribute().getName() + " not supported.");
        }
    }

    private static String checkSearchValue(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        if (value.contains("*") || value.contains("&") || value.contains("|")) {
            throw new IllegalArgumentException(
                    "Value of search attribute contains illegal character(s).");
        }
        return value;
    }
}
