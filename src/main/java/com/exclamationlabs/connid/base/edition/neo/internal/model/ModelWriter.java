package com.exclamationlabs.connid.base.edition.neo.internal.model;

import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttributeHolder;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeDelta;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.MULTIVALUED;

public class ModelWriter {

    private ModelWriter() {}

    public static IdentityModel executeUpdateDelta(Class<? extends IdentityModel> identityModelClass, Set<AttributeDelta> attributes, String uidValue) {
        Set<String> multiValueAttributeNames = new HashSet<>();
        determineMultiValueAttributeNames(identityModelClass, multiValueAttributeNames);
        ConsolidatedValues consolidatedValues = consolidateAttributeValues(attributes, multiValueAttributeNames);

        return populateValues(identityModelClass, consolidatedValues, "Update", uidValue);
    }

    public static IdentityModel execute(Class<? extends IdentityModel> identityModelClass, Set<Attribute> attributes, String operation) {
        return populateValues(identityModelClass, new ConsolidatedValues(attributes), operation, null);
    }

    private static IdentityModel populateValues(Class<? extends IdentityModel> identityModelClass, ConsolidatedValues consolidatedValues,
                                                String operation, String uidValue) {

        // TODO: figure out what to do w/ multivalue adds/removes

        IdentityModel model;
        try {
            model = identityModelClass.getDeclaredConstructor().newInstance();
            setDepthValues(model, uidValue, consolidatedValues.modifiedValues, operation);
        }  catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error writing model for operation: " + operation, e);
        }
        return model;
    }

    private static void setDepthValues(Object dataObject, final String uidValue, Set<Attribute> attributeInfoSet, final String operation) {
        for (var field : dataObject.getClass().getDeclaredFields()) {
            var modelAttribute = field.getAnnotation(ModelAttribute.class);
            if (modelAttribute != null) {
                if (modelAttribute.identifier() == ConnIdType.UID && "Create".equals(operation)) {
                    continue;
                }
                Object singleValueRead;
                if (modelAttribute.identifier() == ConnIdType.UID) {
                    singleValueRead = uidValue; // Automatically set UID value for Update on model
                } else {
                    final var attributeNameOnField =
                            StringUtils.isNoneBlank(modelAttribute.value())
                                    ? modelAttribute.value()
                                    : field.getName();

                    Optional<Attribute> attribute = attributeInfoSet.stream()
                            .filter(attr -> attr.getName().equals(attributeNameOnField))
                            .findFirst();

                    if (attribute.isEmpty()) {
                        continue;
                    }
                    List<Object> valueRead = attribute.get().getValue();
                    if (valueRead == null || valueRead.isEmpty()) {
                        continue;
                    }
                    singleValueRead = valueRead.get(0);
                }

                try {
                    var setterMethodName = "set" + StringUtils.capitalize(field.getName());
                    Method setterMethod;

                    switch (modelAttribute.type()) {
                        case BOOLEAN:
                            setterMethod = dataObject.getClass().getMethod(setterMethodName, Boolean.class);
                            boolean booleanValue;
                            if (singleValueRead instanceof Boolean) {
                                booleanValue = BooleanUtils.toBoolean((Boolean) singleValueRead);
                            } else if (singleValueRead instanceof Integer) {
                                booleanValue = BooleanUtils.toBoolean((Integer) singleValueRead);
                            } else {
                                booleanValue = BooleanUtils.toBoolean(singleValueRead.toString());
                            }
                            setterMethod.invoke(dataObject, booleanValue);
                            break;
                        case INTEGER:
                            setterMethod = dataObject.getClass().getMethod(setterMethodName, Integer.class);
                            int intValue;
                            if (singleValueRead instanceof Integer) {
                                intValue = (Integer) singleValueRead;
                            } else {
                                intValue = Integer.parseInt(singleValueRead.toString());
                            }
                            setterMethod.invoke(dataObject, intValue);
                            break;
                        case GUARDED_STRING:
                            setterMethod = dataObject.getClass().getMethod(setterMethodName, String.class);
                            var unguardedString = singleValueRead instanceof GuardedString
                                    ? GuardedStringUtil.read((GuardedString) singleValueRead) : singleValueRead.toString();
                            setterMethod.invoke(dataObject, unguardedString);
                            break;
                        default: // string
                            setterMethod = dataObject.getClass().getMethod(setterMethodName, String.class);
                            setterMethod.invoke(dataObject, singleValueRead.toString());
                            break;
                    }
                } catch (ReflectiveOperationException ill) {
                    Logger.warn(ModelReader.class, "Unexpected reflection access issue for field " + field.getName(), ill);
                    continue;
                }
            }

            var holderAttribute = field.getAnnotation(ModelAttributeHolder.class);
            if (holderAttribute != null) {
                try {
                    // Nested object data: need to invoke default constructor for holder's data type, then invoke setter, then invoke getter
                    Object newHolder = field.getType()
                            .getDeclaredConstructor()
                            .newInstance();
                    var setterMethodName = "set" + StringUtils.capitalize(field.getName());
                    var setterMethod = dataObject.getClass().getMethod(setterMethodName, field.getType());
                    setterMethod.invoke(dataObject, newHolder);

                    var getterMethodName = "get" + StringUtils.capitalize(field.getName());
                    var getterMethod = dataObject.getClass().getMethod(getterMethodName);
                    var readValue = getterMethod.invoke(dataObject);

                    setDepthValues(readValue, uidValue, attributeInfoSet, operation);
                } catch (ReflectiveOperationException ill) {
                    Logger.warn(ModelReader.class, "Unexpected reflection access issue for holder data", ill);
                }

            }
        }
    }


    private static void determineMultiValueAttributeNames(Object dataObject,
                                                          Set<String> multiValueAttributeNames) {
        for (var field : dataObject.getClass().getDeclaredFields()) {
            var modelAttribute = field.getAnnotation(ModelAttribute.class);
            if (modelAttribute != null && Arrays.asList(modelAttribute.flags()).contains(MULTIVALUED)) {
                multiValueAttributeNames.add(modelAttribute.value());
            } else {
                var holderAttribute = field.getAnnotation(ModelAttributeHolder.class);
                if (holderAttribute != null) {
                        determineMultiValueAttributeNames(field, multiValueAttributeNames);
                }
            }
        }
    }


    private static ConsolidatedValues consolidateAttributeValues(Set<AttributeDelta> delta, Set<String> multiValueAttributeNames) {
        Set<Attribute> modifiedSet = new HashSet<>();
        Set<Attribute> addedSet = new HashSet<>();
        Set<Attribute> removedSet = new HashSet<>();

        Logger.info(ModelWriter.class, String.format("Consolidate %d delta values", delta.size()));
        for (AttributeDelta current : delta) {
            boolean multiValuedAttribute = multiValueAttributeNames.contains(current.getName());
            List<Object> addValues = current.getValuesToAdd();
            List<Object> modifiedValues = current.getValuesToReplace();
            List<Object> removedValues = current.getValuesToRemove();

            if (modifiedValues != null) {
                modifiedSet.add(AttributeBuilder.build(current.getName(), modifiedValues));
                Logger.info(
                        ModelWriter.class,
                        String.format(
                                "ModifiedValues not null. Added %s to modified set.  New size %d",
                                current.getName(), modifiedSet.size()));
            } else {
                if (addValues != null) {
                    if (multiValuedAttribute) {
                        addedSet.add(AttributeBuilder.build(current.getName(), addValues));
                        Logger.info(
                                ModelWriter.class,
                                String.format(
                                        "MultiValuedAttribute. Added %s to added set.  New size %d",
                                        current.getName(), addedSet.size()));
                    } else {
                        modifiedSet.add(AttributeBuilder.build(current.getName(), addValues));
                        Logger.info(
                                ModelWriter.class,
                                String.format(
                                        "Not MultiValuedAttribute. Added %s to modified set.  New size %d",
                                        current.getName(), modifiedSet.size()));
                    }
                }

                if (removedValues != null) {
                    if (multiValuedAttribute) {
                        removedSet.add(AttributeBuilder.build(current.getName(), removedValues));
                        Logger.info(
                                ModelWriter.class,
                                String.format(
                                        "Removed values present and multiValuedAttribute. Added %s to removed set.  New size %d",
                                        current.getName(), removedSet.size()));
                    } else {
                        modifiedSet.add(AttributeBuilder.build(current.getName(), Collections.emptyList()));
                        Logger.info(
                                ModelWriter.class,
                                String.format(
                                        "Removed values present and not multiValuedAttribute. Added %s to modified set.  New size %d",
                                        current.getName(), modifiedSet.size()));
                    }
                }
            }
        }
        ConsolidatedValues responseValues = new ConsolidatedValues(modifiedSet, addedSet, removedSet);
        Logger.info(ModelWriter.class, String.format("Consolidated values result: %s", responseValues));
        return responseValues;
    }

    private static final class ConsolidatedValues {

        final Set<Attribute> modifiedValues;
        final Set<Attribute> addedMultiValues;
        final Set<Attribute> removedMultiValues;

        public ConsolidatedValues(Set<Attribute> modified) {
            modifiedValues = modified;
            addedMultiValues = Collections.emptySet();
            removedMultiValues = Collections.emptySet();
        }

        public ConsolidatedValues(
                Set<Attribute> modified, Set<Attribute> added, Set<Attribute> removed) {
            modifiedValues = modified;
            addedMultiValues = added;
            removedMultiValues = removed;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("addedMultiValues: [");
            for (Attribute current : addedMultiValues) {
                builder.append(current.toString());
            }
            builder.append("],");
            builder.append("modifiedValues: [");
            for (Attribute current : modifiedValues) {
                builder.append(current.toString());
            }
            builder.append("],");
            builder.append("removedMultiValues: [");
            for (Attribute current : removedMultiValues) {
                builder.append(current.toString());
            }
            builder.append("]");
            return builder.toString();
        }
    }
}
