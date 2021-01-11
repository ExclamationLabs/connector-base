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

package com.exclamationlabs.connid.base.connector.attribute;

import org.identityconnectors.framework.common.objects.AttributeInfo;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;


/**
 * Builder to aid with the definition of connector attributes.  Attributes
 * are needed by Midpoint to setup the schema and lay out all the data elements
 * available for Users and Groups in a connector.
 * @param <C> Your own enum type needs to be supplied here that gives
 *           the names of attributes (for a group or for a user)
 */
public class ConnectorAttributeMapBuilder<C extends Enum<C>> {

    protected EnumMap<C, ConnectorAttribute> map;

    /**
     * Method to append another attribute definition to the builder.
     * @param enumClass Enum value representing a connector attribute name
     * @param dataType Acceptable data type for ConnId.
     * @param flags Optional. Any number of flags restricting or describing ConnId usage for
     *              this attribute. Possible flags are ...
     *              REQUIRED, MULTIVALUED, NOT_CREATABLE, NOT_UPDATEABLE, NOT_READABLE, NOT_RETURNED_BY_DEFAULT
     * @return builder object in progress
     */
    public ConnectorAttributeMapBuilder<C> add(C enumClass, ConnectorAttributeDataType dataType,
                                               AttributeInfo.Flags... flags) {
        Set<AttributeInfo.Flags> dataFlags = new HashSet<>(Arrays.asList(flags));
        map.put(enumClass, new ConnectorAttribute(enumClass.name(), dataType, dataFlags));
        return this;
    }

    /**
     * When you are all done calling add() methods for all attribute,
     * call build() to produce the EnumMap that will hold the attribute
     * types (for a user or for a group).
     * @return EnumMap containing all attribute definitions (for a user or for a group)
     */
    public EnumMap<?, ConnectorAttribute> build() {
        return map;
    }
}
