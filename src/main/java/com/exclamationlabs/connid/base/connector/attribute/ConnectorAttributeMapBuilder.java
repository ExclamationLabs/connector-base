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

import java.util.*;

public class ConnectorAttributeMapBuilder<C extends Enum<C>> {

    protected EnumMap<C, ConnectorAttribute> map;

    public ConnectorAttributeMapBuilder(Class<C> enumClass) {
        map = new EnumMap<C, ConnectorAttribute>(enumClass);
    }

    public ConnectorAttributeMapBuilder<C> add(C enumClass, ConnectorAttributeDataType dataType,
                                               AttributeInfo.Flags... flags) {
        Set<AttributeInfo.Flags> dataFlags = new HashSet<>(Arrays.asList(flags));
        map.put(enumClass, new ConnectorAttribute(enumClass.name(), dataType, dataFlags));
        return this;
    }

    public EnumMap<?, ConnectorAttribute> build() {
        return map;
    }
}
