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

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;

// TODO: refine for general use
public class DefaultFilterTranslator extends AbstractFilterTranslator<String> {

    private static final Log LOG = Log.getLog(DefaultFilterTranslator.class);

    @Override
    protected String createEqualsExpression(EqualsFilter filter, boolean not) {

        LOG.info("### Entered filter equals");

        if (not) { // no way (natively) to search for "NotEquals"
            return null;
        }

        if (filter == null) {
            return null;
        }

        Attribute attr = filter.getAttribute();
        if (!attr.is(Name.NAME) && !attr.is(Uid.NAME)) {
            return null;
        }
        String name = attr.getName();
        String value = AttributeUtil.getAsStringValue(attr);

        LOG.info("### Filter name and value {0}, {1}", name, value);
        if (checkSearchValue(value) == null) {
            return null;
        } else {
            return value;
        }
    }

    @Override
    protected String createContainsExpression(ContainsFilter filter, boolean not) {
        LOG.info("### Entered filter contains, name: {0}, value {1}",
                filter.getName(), filter.getValue());
        if (filter.getAttribute() != null) {
            LOG.info("### Attr Entered filter contains, name: {0}, value {1}",
                    filter.getAttribute().getName(), filter.getAttribute().getValue());
        }

        return null;
    }

    private static String checkSearchValue(java.lang.String value) {
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
