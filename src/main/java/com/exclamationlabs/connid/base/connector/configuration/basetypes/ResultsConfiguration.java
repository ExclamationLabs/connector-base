/*
    Copyright 2021 Exclamation Labs

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

package com.exclamationlabs.connid.base.connector.configuration.basetypes;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;

/**
 * Configuration properties common to connectors that support pagination
 * or need to perform deep gets or deep imports on each record when performing a getAll.
 */
public interface ResultsConfiguration extends ConnectorConfiguration {

    Boolean getDeepGet();
    void setDeepGet(Boolean input);

    Boolean getDeepImport();
    void setDeepImport(Boolean input);

    Integer getImportBatchSize();
    void setImportBatchSize(Integer input);

    Boolean getPagination();
    void setPagination(Boolean input);
}
