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

package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.driver.Driver;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Uid;

import java.util.Set;

/**
 * Interface describing implementations that will translate ConnId Identity Access Management (IAM)
 * types to and from a destination system/service.
 */
public interface Adapter<M> {

    void setType(ObjectClass objectClass);

    ConnectorAttribute getIdAttribute();

    ConnectorAttribute getNameAttribute();

    M constructModelFromAttributeSet(Set<Attribute> attributes, boolean creation);

    Set<Attribute> constructAttributeSetFromModel(M modelType);

    void setAttributes(Set<ConnectorAttribute> attributeSet);

    void setDriver(Driver driver);

    default Object readAttributeValue(Attribute input) {
        if (input != null && input.getValue() != null && input.getValue().get(0) != null) {
            return input.getValue().get(0);
        } else {
            return null;
        }
    }

    default boolean queryAllRecords(String query) {
        return (query == null || StringUtils.isBlank(query) || StringUtils.equalsIgnoreCase(query, "ALL"));
    }

    /**
     * Service a request from IAM system to create the type on the destination system.
     * @param attributes Attributes from IAM system that map to data elements needed
     *                   by the destination system for type creation.
     * @return new unique identifier for newly created type
     */
    Uid create(Set<Attribute> attributes);

    /**
     * Service a request from IAM system to update the type on the destination system.
     * @param uid Unique identifier for the data item being updated.
     * @param attributes Attributes from IAM system that map to data elements supported
     *                   by the destination system to update the type.
     * @return unique identifier applicable to the type that was just updated
     */
    Uid update(Uid uid, Set<Attribute> attributes);

    /**
     * Service a request from IAM system to delete the type on the destination system.
     * @param uid Unique identifier for the data item to be deleted.
     */
    void delete(Uid uid);

    /**
     * Service a request from IAM to get one, some, or all items of a data type from the destination system.
     * @param query Query string to help identify which item(s) need to be retrieved.
     * @param resultsHandler ConnId ResultsHandler object used to send result data back to IAM system.
     */
    void get(String query, ResultsHandler resultsHandler);
}
