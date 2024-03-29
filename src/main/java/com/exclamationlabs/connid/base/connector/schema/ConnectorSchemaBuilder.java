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

package com.exclamationlabs.connid.base.connector.schema;

import com.exclamationlabs.connid.base.connector.BaseConnector;
import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import java.util.Map;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Schema;

/** Interface for building a ConnId schema using the base connector framework. */
public interface ConnectorSchemaBuilder<T extends ConnectorConfiguration> {

  /**
   * Build and define the schema, capturing all the Attribute definitions for object types for this
   * connector.
   *
   * @param connector Applicable Connector object.
   * @param adapterMap Map of adapters defined for this Connector.
   * @return Schema object needed for ConnId identity management system (normally MidPoint)
   * @throws ConfigurationException if exception or failure occurred while trying to read or
   *     construct the schema.
   */
  Schema build(BaseConnector<T> connector, Map<ObjectClass, BaseAdapter<?, T>> adapterMap)
      throws ConfigurationException;
}
