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
