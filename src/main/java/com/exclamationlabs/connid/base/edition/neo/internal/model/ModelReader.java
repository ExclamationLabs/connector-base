package com.exclamationlabs.connid.base.edition.neo.internal.model;

import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.edition.neo.internal.FieldAccessInfo;
import com.exclamationlabs.connid.base.edition.neo.internal.IdentityModelAccess;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.Direction;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import org.apache.commons.lang3.BooleanUtils;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;

import java.util.HashSet;
import java.util.Set;

public class ModelReader {

    private ModelReader() {}

    public static Set<Attribute> execute(IdentityModel model, IdentityModelAccess identityModelAccess) {
        Set<Attribute> attributeInfoSet = new HashSet<>();

        for (var attributeName : identityModelAccess.getFieldAccessInfoMap().keySet()) {
            var fieldAccessInfo = identityModelAccess.getFieldAccessInfoMap().get(attributeName);

            if (fieldAccessInfo.getIdentifier() == ConnIdType.UID || fieldAccessInfo.getIdentifier() == ConnIdType.NAME ||
                fieldAccessInfo.getDirection() == Direction.OUTBOUND_ONLY) {
                continue;
            }
            Attribute currentAttribute;
            Object readValue = readDepthValue(attributeName, fieldAccessInfo, model);
            if (readValue == null) {
                currentAttribute = AttributeBuilder.build(attributeName);
            } else {
                switch (fieldAccessInfo.getDataType()) {
                    case BOOLEAN:
                        boolean booleanValue;
                        if (readValue instanceof Boolean) {
                            booleanValue = BooleanUtils.toBoolean((Boolean) readValue);
                        } else if (readValue instanceof Integer) {
                            booleanValue = BooleanUtils.toBoolean((Integer) readValue);
                        } else {
                            booleanValue = BooleanUtils.toBoolean(readValue.toString());
                        }
                        currentAttribute = AttributeBuilder.build(attributeName, booleanValue);
                        break;
                    case INTEGER:
                        int intValue;
                        if (readValue instanceof Integer) {
                            intValue = (Integer) readValue;
                        } else {
                            intValue = Integer.parseInt(readValue.toString());
                        }
                        currentAttribute = AttributeBuilder.build(attributeName, intValue);
                        break;
                    case GUARDED_STRING:
                        currentAttribute = AttributeBuilder.build(attributeName, new GuardedString(readValue.toString().toCharArray()));
                        break;
                    default: // string
                        currentAttribute = AttributeBuilder.build(attributeName, readValue.toString());
                        break;
                }
            }
            attributeInfoSet.add(currentAttribute);

        }
        return attributeInfoSet;
    }

    private static Object readDepthValue(final String attributeName, FieldAccessInfo fieldAccessInfo, IdentityModel model) {
        Object rawAttributeValue = null;

        Object depthDataObject = model;

        for (var getterMethod : fieldAccessInfo.getGetterAccess()) {
            try {
                Object obtainedValue = getterMethod.invoke(depthDataObject);
                if (obtainedValue == null) {
                    rawAttributeValue = null;
                    break;
                } else {
                    depthDataObject = obtainedValue;
                    rawAttributeValue = obtainedValue;
                }
            } catch (ReflectiveOperationException ill) {
                Logger.warn(ModelReader.class, "Unexpected reflection access issue for get of attribute" + attributeName, ill);
            }
        }
        return rawAttributeValue;
    }
}
