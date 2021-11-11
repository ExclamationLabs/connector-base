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

/**
 * ResultsFilter is used to store, if applicable, the attribute name and value
 * that the user wishes to filter data on.  The driver/invocator getAll method
 * may or may not support results filtering (this is determined by getEnhancedFiltering()
 * and getFilterAttributes() in the Connector).
 *
 * Note: If deemed useful, this object could possibly be enhanced in the future to
 * support multiple filter attribute/value pairs, instead of a single one.
 */
public class ResultsFilter {

    private String attribute;
    private String value;

    public ResultsFilter() {
        setAttribute(null);
        setValue(null);
    }

    public ResultsFilter(String filterAttribute, String filterValue) {
        setAttribute(filterAttribute);
        setValue(filterValue);
    }

    public boolean hasFilter() {
        return getAttribute() != null && getValue() != null;
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

    @Override
    public String toString() {
        return hasFilter() ? attribute + ":" + value : "none";
    }
}
