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
import java.util.Set;

/**
 * Describes an immutable Connector Attribute definition - it's name, data type
 * and ConnId Flags.  This needs to be in place so the ConnId Schema can
 * be built, and also inform the Adapter of the attribute types applicable to
 * this connector.  This implementation does not hold attribute values;
 * the ConnId Attribute type is still used to hold the actual values.
 */
public final class ConnectorAttribute {

    final String name;

    final ConnectorAttributeDataType dataType;

    final Set<AttributeInfo.Flags> flags;


    public ConnectorAttribute(String attributeName, ConnectorAttributeDataType
            attributeDataType, Set<AttributeInfo.Flags> attributeFlags) {
        name = attributeName;
        dataType = attributeDataType;
        flags = attributeFlags;
    }

    public String getName() {
        return name;
    }

    public ConnectorAttributeDataType getDataType() {
        return dataType;
    }

    public Set<AttributeInfo.Flags> getFlags() {
        return flags;
    }


}
