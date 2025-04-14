package com.exclamationlabs.connid.base.edition.neo.internal.model;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.util.GuardedStringUtil;
import com.exclamationlabs.connid.base.edition.neo.internal.ConsolidatedValues;
import com.exclamationlabs.connid.base.edition.neo.internal.IdentityModelAccess;
import com.exclamationlabs.connid.base.edition.neo.model.AssignmentType;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.Direction;
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
import java.util.stream.Collectors;

import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.MULTIVALUED;

public class ModelWriter {

    private ModelWriter() {}

    public static IdentityModel executeUpdateDelta(Class<? extends IdentityModel> identityModelClass, Set<AttributeDelta> attributes, String uidValue, IdentityModelAccess identityModelAccess) {
        Set<String> multiValueAttributeNames = new HashSet<>();
        for (var info : identityModelAccess.getFieldAccessInfoMap().values()) {
            if (Arrays.asList(info.getFlags()).contains(MULTIVALUED)) {
                multiValueAttributeNames.add(info.getAttributeName());
            }
        }
        ConsolidatedValues consolidatedValues = consolidateAttributeValues(attributes, multiValueAttributeNames);
        return populateValues(identityModelClass, consolidatedValues, "Update", uidValue, identityModelAccess);
    }

    public static IdentityModel execute(Class<? extends IdentityModel> identityModelClass, Set<Attribute> attributes, String operation, IdentityModelAccess identityModelAccess) {
        return populateValues(identityModelClass, new ConsolidatedValues(attributes), operation, null, identityModelAccess);
    }

    private static IdentityModel populateValues(Class<? extends IdentityModel> identityModelClass, ConsolidatedValues consolidatedValues,
                                                String operation, String uidValue, IdentityModelAccess identityModelAccess) {

        IdentityModel model;
        try {
            model = identityModelClass.getDeclaredConstructor().newInstance();
            for (var info : identityModelAccess.getFieldAccessInfoMap().values()) {
                if ((info.getIdentifier() == ConnIdType.UID && "Create".equals(operation)) || info.getDirection() == Direction.INBOUND_ONLY) {
                    continue;
                }
                Object singleValueRead;
                Method setterMethod;
                Object setterInvokeTarget = model;
                if (info.getIdentifier() == ConnIdType.UID) {
                    setterMethod = info.getSetterAccess().get(0);
                    singleValueRead = uidValue; // Automatically set UID value for Update on model
                } else {

                    Optional<Attribute> attribute = consolidatedValues.modifiedValues.stream()
                            .filter(attr -> attr.getName().equals(info.getAttributeName())
                                    && attr.getValue() != null && !attr.getValue().isEmpty()
                                    && attr.getValue().get(0) != null
                                    && StringUtils.isNotBlank(attr.getValue().get(0).toString()))
                            .findFirst();
                    List<Attribute> addedAttributes = Collections.emptyList();
                    List<Attribute> removedAttributes = Collections.emptyList();
                    if (info.getDataType() == ConnectorAttributeDataType.ASSIGNMENT_IDENTIFIER) {
                        if ("Create".equals(operation)) {
                            addedAttributes = consolidatedValues.modifiedValues.stream()
                                    .filter(attr -> attr.getName().equals(info.getAttributeName())
                                            && attr.getValue() != null && !attr.getValue().isEmpty())
                                    .collect(Collectors.toList());
                        } else {
                            addedAttributes = consolidatedValues.addedMultiValues.stream()
                                    .filter(attr -> attr.getName().equals(info.getAttributeName())
                                            && attr.getValue() != null && !attr.getValue().isEmpty())
                                    .collect(Collectors.toList());
                            removedAttributes = consolidatedValues.removedMultiValues.stream()
                                    .filter(attr -> attr.getName().equals(info.getAttributeName())
                                            && attr.getValue() != null && !attr.getValue().isEmpty())
                                    .collect(Collectors.toList());
                        }
                    }

                    if (attribute.isEmpty() && addedAttributes.isEmpty() && removedAttributes.isEmpty()) {
                        continue;
                    }
                    if ((!addedAttributes.isEmpty()) || (!removedAttributes.isEmpty())) {
                        // Presume AssignmentType adds/removes
                        Set<Object> assignmentIdsToAddObject = addedAttributes.stream()
                                .flatMap(attr -> attr.getValue().stream())
                                .collect(Collectors.toSet());
                        Set<String> assignmentIdsToAdd = assignmentIdsToAddObject.stream()
                                .map(Object::toString)
                                .collect(Collectors.toSet());

                        Set<Object> assignmentIdsToRemoveObject = removedAttributes.stream()
                                .flatMap(attr -> attr.getValue().stream())
                                .collect(Collectors.toSet());
                        Set<String> assignmentIdsToRemove = assignmentIdsToRemoveObject.stream()
                                .map(Object::toString)
                                .collect(Collectors.toSet());
                        var assignmentType = new AssignmentType();
                        assignmentType.setAddedOutboundAssignments(assignmentIdsToAdd);
                        assignmentType.setRemovedOutboundAssignments(assignmentIdsToRemove);
                        singleValueRead = assignmentType;
                        setterMethod = info.getSetterAccess().get(0);
                    } else {
                        singleValueRead = attribute.get().getValue().get(0);
                        if (info.getSetterAccess().size() == 1) {
                            // No depth, simply invoke setter method
                            setterMethod = info.getSetterAccess().get(0);
                        } else {
                            setterMethod = info.getGetterAccess().get(0);
                            for (int idx = 0; idx < info.getGetterAccess().size() - 1; idx++) {
                                var currentMethod = info.getGetterAccess().get(idx);
                                Object nextInvokeTarget = currentMethod.invoke(setterInvokeTarget);
                                if (nextInvokeTarget == null) {
                                    // Need to create new instance of the next level
                                    nextInvokeTarget = currentMethod.getReturnType().getDeclaredConstructor().newInstance();
                                    var depthSetterMethod = info.getSetterAccess().get(idx);
                                    depthSetterMethod.invoke(setterInvokeTarget, nextInvokeTarget);
                                    setterInvokeTarget = nextInvokeTarget;
                                } else {
                                    setterInvokeTarget = nextInvokeTarget;
                                }
                                setterMethod = info.getSetterAccess().get(idx + 1);
                            }
                        }
                    }

                }
                setIdentityModelValue(setterInvokeTarget, setterMethod, singleValueRead, info.getDataType());
            }

        }  catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error writing model for operation: " + operation, e);
        }
        return model;
    }

    private static void setIdentityModelValue(Object dataObject, Method setterMethod, Object singleValueRead, ConnectorAttributeDataType dataType)  throws ReflectiveOperationException{
        switch (dataType) {
            case BOOLEAN:
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
                int intValue;
                if (singleValueRead instanceof Integer) {
                    intValue = (Integer) singleValueRead;
                } else {
                    intValue = Integer.parseInt(singleValueRead.toString());
                }
                setterMethod.invoke(dataObject, intValue);
                break;
            case GUARDED_STRING:
                var unguardedString = singleValueRead instanceof GuardedString
                        ? GuardedStringUtil.read((GuardedString) singleValueRead) : singleValueRead.toString();
                setterMethod.invoke(dataObject, unguardedString);
                break;
            case ASSIGNMENT_IDENTIFIER:
                var assignmentType = (AssignmentType) singleValueRead;
                setterMethod.invoke(dataObject, assignmentType);
                break;
            default: // string
                setterMethod.invoke(dataObject, singleValueRead.toString());
                break;
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
}
