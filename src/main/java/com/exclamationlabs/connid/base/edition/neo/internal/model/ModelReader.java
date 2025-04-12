package com.exclamationlabs.connid.base.edition.neo.internal.model;

import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttributeHolder;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import java.util.HashSet;
import java.util.Set;

public class ModelReader {

    private ModelReader() {}

    public static Set<Attribute> execute(IdentityModel model) {
        Set<Attribute> attributeInfoSet = new HashSet<>();
        readDepthValues(model, attributeInfoSet);
        return attributeInfoSet;
    }

    private static void readDepthValues(Object dataObject, Set<Attribute> attributeInfoSet) {
        for (var field : dataObject.getClass().getDeclaredFields()) {
            var modelAttribute = field.getAnnotation(ModelAttribute.class);
            if (modelAttribute != null) {
                if (modelAttribute.identifier() == ConnIdType.UID || modelAttribute.identifier() == ConnIdType.NAME) {
                    continue;
                }
                final var attributeName =
                        StringUtils.isNoneBlank(modelAttribute.value())
                                ? modelAttribute.value()
                                : field.getName();
                Attribute currentAttribute;
                try {
                    var getterMethodName = "get" + StringUtils.capitalize(field.getName());
                    var getterMethod = dataObject.getClass().getMethod(getterMethodName);
                    var readValue = getterMethod.invoke(dataObject);
                    if (readValue == null) {
                        currentAttribute = AttributeBuilder.build(attributeName);
                    } else {
                        switch (modelAttribute.type()) {
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
                } catch (ReflectiveOperationException ill) {
                    Logger.warn(ModelReader.class, "Unexpected reflection access issue for field " + field.getName(), ill);
                    continue;
                }
                attributeInfoSet.add(currentAttribute);
            }

            var holderAttribute = field.getAnnotation(ModelAttributeHolder.class);
            if (holderAttribute != null) {
                try {
                    var getterMethodName = "get" + StringUtils.capitalize(field.getName());
                    var getterMethod = dataObject.getClass().getMethod(getterMethodName);
                    var readValue = getterMethod.invoke(dataObject);

                    readDepthValues(readValue, attributeInfoSet);
                } catch (ReflectiveOperationException ill) {
                    Logger.warn(ModelReader.class, "Unexpected reflection access issue for holder data", ill);
                }

            }
        }
    }
}
