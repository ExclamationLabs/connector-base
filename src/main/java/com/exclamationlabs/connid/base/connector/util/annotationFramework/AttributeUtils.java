package com.exclamationlabs.connid.base.connector.util.annotationFramework;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.BOOLEAN;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.DOUBLE;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.FLOAT;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.GUARDED_STRING;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.INTEGER;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.LONG;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.ZONED_DATE_TIME;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.MULTIVALUED;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_CREATABLE;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

import com.exclamationlabs.connid.base.connector.adapter.AdapterValueTypeConverter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.connector.attribute.meta.AttributeConstraint;
import com.exclamationlabs.connid.base.connector.attribute.meta.AttributeConstraintDirection;
import com.exclamationlabs.connid.base.connector.attribute.meta.AttributeConstraintRule;
import com.exclamationlabs.connid.base.connector.attribute.meta.AttributeMetaInfo;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AdapterSettings;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributeIdentityValue;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributeIgnore;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributeMultiValue;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributeNameValue;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributeNotCreateable;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributeNotUpdateable;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributeSchemaMetaInfo;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributeSearchResult;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributedGuarded;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo.Flags;

public class AttributeUtils {

  /**
   * This is a convenience method to create ConnectorAttributes for the getConnectorAttributes() in
   * the adpatper. This method will not recurse through the object tree, if you want to process
   * child attributes, run this again with the child class and add a prefix to prevent duplicate
   * names.
   *
   * @param result - this is the Set of connector attributes that will be populated.
   * @param ignoreAttributes - An array of Strings that are Attribute Names that you would not like
   *     to be output. If you use a prefix for child objects, you must include the prefix in the
   *     ignore list example PREFIX_FIELD_NAME.
   * @param notUpdateableAttributes - An array of Strings that are Attribute Names that you would
   *     like to be NotUpdateable. If you use a prefix for child objects, you must include the
   *     prefix in the ignore list example PREFIX_FIELD_NAME.
   * @param notCreateableAttributes - An array of Strings that are Attribute Names that you would to
   *     be NotCreatable. If you use a prefix for child objects, you must include the prefix in the
   *     ignore list example PREFIX_FIELD_NAME.
   * @param prefix - String of the prefix. If you are going to process child objects, you should use
   *     a prefix to prevent duplication.
   * @param clazz - The class of the object which you want to create attributes for.
   */
  public static void createAttributes(
      Set<ConnectorAttribute> result,
      String[] ignoreAttributes,
      String[] notUpdateableAttributes,
      String[] notCreateableAttributes,
      String prefix,
      Class clazz) {
    createAttributes(
        result,
        ignoreAttributes,
        notUpdateableAttributes,
        notCreateableAttributes,
        prefix,
        clazz,
        false);
  }

  /**
   * This is used in Annotation Adapter to output the Attributes;
   *
   * @param clazz class of Identity Model
   * @param result Set of connector attributes to put output.
   */
  public static void createAttributesFromAnnotations(Class clazz, Set<ConnectorAttribute> result) {

    for (var field : clazz.getDeclaredFields()) {
      if (isValidType(field)
          || (field.getType() == List.class && hasAnnotation(field, AttributeMultiValue.class))) {
        String attributeName = fieldNameToAttribute(field.getName());
        if (!hasAnnotation(field, AttributeIgnore.class)) {
          ArrayList<Flags> flags = new ArrayList<>();
          if (hasAnnotation(field, AttributeNotCreateable.class)) {
            flags.add(NOT_CREATABLE);
          }
          if (hasAnnotation(field, AttributeNotUpdateable.class)) {
            flags.add(NOT_UPDATEABLE);
          }
          ConnectorAttributeDataType type = null;
          if ((field.getType() == List.class && hasAnnotation(field, AttributeMultiValue.class))) {
            flags.add(MULTIVALUED);
            var annotations = field.getAnnotation(AttributeMultiValue.class);
            if (annotations == null) {
              type = STRING;
            } else {
              type = ((AttributeMultiValue) annotations).dataType();
            }
          } else {
            type = getDataType(field, hasAnnotation(field, AttributedGuarded.class));
          }
          if (flags.isEmpty()) {
            result.add(new ConnectorAttribute(attributeName, type));
          } else {
            var flagsArray = new Flags[flags.size()];
            flags.toArray(flagsArray);
            result.add(new ConnectorAttribute(attributeName, type, flagsArray));
          }
        }
      }
    }
  }

  private static void createAttributes(
      Set<ConnectorAttribute> result,
      String[] ignoreAttributes,
      String[] notUpdateableAttributes,
      String[] notCreateableAttributes,
      String prefix,
      Class clazz,
      boolean print) {
    List<String> attributeNames = new ArrayList<>();
    if (result != null) {
      if (print) {
        System.out.println("**** getConnectorAttributes");
      }
      if (notUpdateableAttributes == null) notUpdateableAttributes = new String[0];
      if (notCreateableAttributes == null) notCreateableAttributes = new String[0];
      if (ignoreAttributes == null) ignoreAttributes = new String[0];
      if (prefix == null) prefix = "";
      if (result != null) {
        for (var field : clazz.getDeclaredFields()) {
          if (isValidType(field)) {
            String attributeName =
                (prefix.isEmpty())
                    ? fieldNameToAttribute(field.getName())
                    : prefix.toUpperCase() + "_" + fieldNameToAttribute(field.getName());
            if (!Arrays.stream(ignoreAttributes)
                .anyMatch(ignoreAttribute -> ignoreAttribute.equals(attributeName))) {
              ArrayList<Flags> flags = new ArrayList<>();
              if (Arrays.stream(notCreateableAttributes)
                  .anyMatch(notCreatableAttribute -> notCreatableAttribute.equals(attributeName))) {
                flags.add(NOT_CREATABLE);
              }
              if (Arrays.stream(notUpdateableAttributes)
                  .anyMatch(
                      notUpdateableAttribute -> notUpdateableAttribute.equals(attributeName))) {
                flags.add(NOT_UPDATEABLE);
              }
              var type = getDataType(field, hasAnnotation(field, AttributedGuarded.class));
              attributeNames.add(attributeName);
              if (flags.isEmpty()) {
                if (print) {
                  System.out.println(
                      "result.add(new ConnectorAttribute("
                          + attributeName
                          + ".name(), "
                          + type.name()
                          + ");");
                }
                result.add(new ConnectorAttribute(attributeName, type));
              } else {
                if (print) {
                  System.out.println(
                      "result.add(new ConnectorAttribute("
                          + attributeName
                          + ".name(), "
                          + type.name()
                          + ", "
                          + flags.toString().replace("[", "").replace("]", "")
                          + "));");
                }
                var flagsArray = new Flags[flags.size()];
                flags.toArray(flagsArray);
                result.add(new ConnectorAttribute(attributeName, type, flagsArray));
              }
            }
          }
        }
      }
    }
    if (print) {
      System.out.println("\r\n");
    }
  }

  private static boolean isValidType(Field field) {
    if (field.getType() == String.class
        || field.getType() == Boolean.class
        || field.getType() == Integer.class
        || field.getType() == Long.class
        || field.getType() == Double.class
        || field.getType() == Float.class
        || field.getType() == ZonedDateTime.class) {
      return true;
    } else {
      return false;
    }
  }

  private static ConnectorAttributeDataType getDataType(Field field, boolean isGuarded) {
    if (field.getType() == Integer.class) {
      return INTEGER;
    }
    if (field.getType() == Long.class) {
      return LONG;
    }
    if (field.getType() == Double.class) {
      return DOUBLE;
    }
    if (field.getType() == Float.class) {
      return FLOAT;
    }
    if (field.getType() == Boolean.class) {
      return BOOLEAN;
    }
    if (field.getType() == ZonedDateTime.class) {
      return ZONED_DATE_TIME;
    }
    if (field.getType() == String.class && isGuarded) {
      return GUARDED_STRING;
    }
    return STRING;
  }

  /**
   * Used in AnnotationAdpater to construct Attributes from Object
   *
   * @param o
   * @return
   */
  public static Set<Attribute> constructAttributesFromAnnotations(
      Object o, Set<Attribute> attributes) {
    if (o != null && attributes != null) {
      Class clazz = o.getClass();
      for (var field : clazz.getDeclaredFields()) {
        if (isValidType(field)
            || (field.getType() == List.class && hasAnnotation(field, AttributeMultiValue.class))) {
          String attributeName = fieldNameToAttribute(field.getName());
          if (!hasAnnotation(field, AttributeIgnore.class)) {
            Object val = null;
            try {
              field.setAccessible(true);
              val = field.get(o);
            } catch (Exception e) {
              Logger.error(o.getClass(), "Error constructing attribute value", e);
            }
            if (field.getType() == List.class) {
              Collection collection = (Collection) val;
              attributes.add(AttributeBuilder.build(attributeName, collection));
            } else {
              attributes.add(AttributeBuilder.build(attributeName, val));
            }
          }
        }
      }
    }
    return attributes;
  }

  /**
   * This is a convenience method to construct Attributes according to Object o. It should be used
   * in the adapter constructAttributes method of the adpapter.This method will not recurse through
   * the object tree, if you want to process child attributes, run this again with the child class
   * and add a prefix to prevent duplicate names.
   *
   * @param attributes - A set of Attributes which should be instantiated before calling this. The
   *     constructed attributes will be output into this Set.
   * @param ignoreAttributes - An array of Strings that are Attribute Names that you would not like
   *     to be output. If you use a prefix for child objects, you must include the prefix in the
   *     ignore list example PREFIX_FIELD_NAME.
   * @param prefix - String of the prefix. If you are going to process child objects, you should use
   *     a prefix to prevent duplication.
   * @param o - The instantiated object which to pull the values from to populate the attributes.
   */
  public static void constructAttributes(
      Set<Attribute> attributes, String[] ignoreAttributes, String prefix, Object o) {
    constructAttributes(attributes, ignoreAttributes, prefix, o, false);
  }

  private static void constructAttributes(
      Set<Attribute> attributes,
      String[] ignoreAttributes,
      String prefix,
      Object o,
      boolean print) {
    if (print) {
      System.out.println("**** constructAttributes");
    }
    if (ignoreAttributes == null) ignoreAttributes = new String[0];
    if (prefix == null) prefix = "";
    if (attributes != null && o != null) {
      if (o != null && attributes != null) {
        Class clazz = o.getClass();
        for (var field : clazz.getDeclaredFields()) {
          if (isValidType(field)) {
            String attributeName =
                (prefix.isEmpty())
                    ? fieldNameToAttribute(field.getName())
                    : prefix + "_" + fieldNameToAttribute(field.getName());
            if (!Arrays.stream(ignoreAttributes)
                .anyMatch(ignoreAttribute -> ignoreAttribute.equals(attributeName))) {
              Object val = null;

              try {
                if (print) {
                  System.out.println(
                      "attributes.add(AttributeBuilder.build("
                          + attributeName
                          + ".name(), o."
                          + fieldNameToGetter(field.getName())
                          + "()));");
                }
                field.setAccessible(true);
                val = field.get(o);
              } catch (Exception e) {
                Logger.error(o.getClass(), "Error constructing attribute value", e);
              }
              attributes.add(AttributeBuilder.build(attributeName, val));
            }
          }
        }
      }
    }
    if (print) {
      System.out.println("\r\n");
    }
  }

  public static void constructModelFromAnnotations(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate,
      AnnotatedIdentityModel o) {
    if (o != null && attributes != null) {
      for (var attributeName : attributes) {
        String cleanedAttributeName = attributeName.getName();
        String fieldString = attributeNameToFieldName(cleanedAttributeName, "");
        try {
          var field = o.getClass().getDeclaredField(fieldString);
          if (field != null
              && (isValidType(field)
                  || (field.getType() == List.class
                      && hasAnnotation(field, AttributeMultiValue.class)))
              && !hasAnnotation(field, AttributeIgnore.class)) {
            field.setAccessible(true);
            var paramType = field.getType();
            Object val = null;
            if (attributeName.getValue() != null) val = attributeName.getValue().get(0);
            if (val != null) {
              if (paramType == String.class) {
                field.set(o, val.toString());
              } else if (paramType == Integer.class) {
                field.set(o, Integer.parseInt(val.toString()));
              } else if (paramType == Long.class) {
                field.set(o, Long.parseLong(val.toString()));
              } else if (paramType == Boolean.class) {
                field.set(o, Boolean.parseBoolean(val.toString()));
              } else if (paramType == Float.class) {
                field.set(o, Float.parseFloat(val.toString()));
              } else if (paramType == Double.class) {
                field.set(o, Double.parseDouble(val.toString()));
              } else if (paramType == ZonedDateTime.class) {
                if (val instanceof ZonedDateTime) {
                  field.set(o, (ZonedDateTime) val);
                } else {
                  Logger.info(o.getClass(), "Data type is Date but value is not Date");
                }
              } else if (paramType == List.class
                  && hasAnnotation(field, AttributeMultiValue.class)) {
                populateAnnotatedMultivalueList(
                    field,
                    attributeName.getName(),
                    o,
                    attributes,
                    addedMultiValueAttributes,
                    removedMultiValueAttributes,
                    isCreate);
              } else {
                Logger.info(o.getClass(), "Data type not supported " + attributeName);
              }
            }
          }
        } catch (Exception e) {
          Logger.error(
              o.getClass(),
              "Cannot construct model :" + attributeName + ", Field " + fieldString,
              e);
        }
      }
    }
  }

  /**
   * This is a convenience method to populate the Object with the values in the Set of Attributes.
   * To be used in the constructModel method of the Adapter.
   *
   * @param attributes - The attributes object that the base framework outputs to the contructModel
   *     method.
   * @param ignoreAttributes - An array of Strings that are Attribute Names that you would not like
   *     to be output. If you use a prefix for child objects, you must include the prefix in the
   *     ignore list example PREFIX_FIELD_NAME.
   * @param prefix - String of the prefix. If you are going to process child objects, you should use
   *     a prefix to prevent duplication.
   * @param o- The instaniated object in which to this method will set values on.
   */
  public static void constructModel(
      Set<Attribute> attributes, String[] ignoreAttributes, String prefix, Object o) {
    constructModel(attributes, ignoreAttributes, prefix, o, false);
  }

  private static void constructModel(
      Set<Attribute> attributes,
      String[] ignoreAttributes,
      String prefix,
      Object o,
      boolean print) {
    if (print) {
      System.out.println("**** Construct Model");
    }
    if (ignoreAttributes == null) ignoreAttributes = new String[0];
    if (prefix == null) prefix = "";
    if (o != null && attributes != null) {
      for (var attributeName : attributes) {
        if (!Arrays.stream(ignoreAttributes)
            .anyMatch(ignoreAttribute -> ignoreAttribute.equals(attributeName))) {
          String cleanedAttributeName =
              (prefix.isEmpty()
                  ? attributeName.getName()
                  : attributeName.getName().replace(prefix + "_", ""));
          String fieldString = attributeNameToFieldName(cleanedAttributeName, prefix);
          String setterString = attributeNameToSetter(cleanedAttributeName, prefix);
          try {
            var field = o.getClass().getDeclaredField(fieldString);
            if (field != null && isValidType(field)) {
              field.setAccessible(true);
              var paramType = field.getType();
              Object val = null;
              if (attributeName.getValue() != null) val = attributeName.getValue().get(0);
              if (print) {
                System.out.println(
                    " o."
                        + setterString
                        + "(AdapterValueTypeConverter.getSingleAttributeValue("
                        + paramType.getSimpleName()
                        + ".class, attributes, "
                        + attributeName.getName()
                        + "));");
              }
              if (val != null) {
                if (paramType == String.class) {
                  field.set(o, val.toString());
                } else if (paramType == Integer.class) {
                  field.set(o, Integer.parseInt(val.toString()));
                } else if (paramType == Long.class) {
                  field.set(o, Long.parseLong(val.toString()));
                } else if (paramType == Boolean.class) {
                  field.set(o, Boolean.parseBoolean(val.toString()));
                } else if (paramType == Float.class) {
                  field.set(o, Float.parseFloat(val.toString()));
                } else if (paramType == Double.class) {
                  field.set(o, Double.parseDouble(val.toString()));
                } else if (paramType == ZonedDateTime.class) {
                  if (val instanceof ZonedDateTime) {
                    field.set(o, (ZonedDateTime) val);
                  } else {
                    Logger.info(o.getClass(), "Data type is Date but value is not Date");
                  }
                } else {
                  Logger.info(o.getClass(), "Data type not supported " + attributeName);
                }
              }
            }
          } catch (Exception e) {
            Logger.error(
                o.getClass(),
                "Cannot construct model :" + attributeName + ", Field " + fieldString,
                e);
          }
        }
      }
    }
    if (print) {
      System.out.println("\r\n");
    }
  }

  /**
   * This is used for testing and troubleshooting to see what Attributes this helper will create.
   *
   * @param ignoreAttributes - An array of Strings that are Attribute Names that you would not like
   *     to be output. If you use a prefix for child objects, you must include the prefix in the
   *     ignore list example PREFIX_FIELD_NAME.
   * @param prefix - String of the prefix. If you are going to process child objects, you should use
   *     a prefix to prevent duplication.
   * @param clazz - The class in which to use to create attributes.
   * @return - Returns a list of Strings for each non-ingored attributes.
   */
  public static List<String> listAttributes(String[] ignoreAttributes, String prefix, Class clazz) {
    var out = new ArrayList<String>();
    if (ignoreAttributes == null) ignoreAttributes = new String[0];
    if (prefix == null) prefix = "";
    for (var field : clazz.getDeclaredFields()) {
      if (isValidType(field)) {
        String attributeName =
            (prefix.isEmpty())
                ? fieldNameToAttribute(field.getName())
                : prefix + "_" + fieldNameToAttribute(field.getName());
        if (!Arrays.stream(ignoreAttributes)
            .anyMatch(ignoreAttribute -> ignoreAttribute.equals(attributeName))) {
          out.add(attributeName);
        }
      }
    }
    return out;
  }

  /**
   * This is used for testing and troubleshooting to see what Attributes this helper will create.
   * Prints attribute names to System.out.
   *
   * @param ignoreAttributes - An array of Strings that are Attribute Names that you would not like
   *     to be output. If you use a prefix for child objects, you must include the prefix in the
   *     ignore list example PREFIX_FIELD_NAME.
   * @param prefix - String of the prefix. If you are going to process child objects, you should use
   *     a prefix to prevent duplication.
   * @param clazz - The class in which to use to create attributes.
   */
  public static void logAttributes(String[] ignoreAttributes, String prefix, Class clazz) {
    System.out.println("****Attribute Enum");
    var list = listAttributes(ignoreAttributes, prefix, clazz);
    for (var s : list) {
      System.out.println(s + ",");
    }
    System.out.println("\r\n");
  }

  /**
   * The method generates code for adapter to System.out
   *
   * @param ignoreAttributes - An array of Strings that are Attribute Names that you would not like
   *     * to be output. If you use a prefix for child objects, you must include the prefix in the *
   *     ignore list example PREFIX_FIELD_NAME.
   * @param notUpdateableAttributes - An array of Strings that are Attribute Names that you would
   *     like to be NotUpdateable. If you use a prefix for child objects, you must include the
   *     prefix in the ignore list example PREFIX_FIELD_NAME.
   * @param notCreateableAttributes - An array of Strings that are Attribute Names that you would to
   *     be NotCreatable. If you use a prefix for child objects, you must include the prefix in the
   *     ignore list example PREFIX_FIELD_NAME.
   * @param prefix - String of the prefix. If you are going to process child objects, you should use
   *     a prefix to prevent duplication.
   * @param o Object which to use to code generate.
   */
  public static void codeGenerate(
      String[] ignoreAttributes,
      String[] notUpdateableAttributes,
      String[] notCreateableAttributes,
      String prefix,
      Object o) {
    logAttributes(ignoreAttributes, prefix, o.getClass());
    Set<ConnectorAttribute> result = new HashSet<ConnectorAttribute>();
    createAttributes(
        result,
        ignoreAttributes,
        notUpdateableAttributes,
        notCreateableAttributes,
        prefix,
        o.getClass(),
        true);
    Set<Attribute> attributes = new HashSet<>();
    constructAttributes(attributes, ignoreAttributes, prefix, o, true);
    constructModel(attributes, ignoreAttributes, prefix, o, true);
  }

  private static String fieldNameToAttribute(String fieldName) {
    StringBuilder sb = new StringBuilder();
    sb.append(Character.toLowerCase(fieldName.charAt(0)));
    for (int i = 1; i < fieldName.length(); i++) {
      sb.append(
          Character.isLowerCase(fieldName.charAt(i))
              ? String.valueOf(fieldName.charAt(i))
              : "_" + String.valueOf(Character.toLowerCase(fieldName.charAt(i))));
    }
    return sb.toString().toUpperCase();
  }

  private static String attributeNameToFieldName(String attributeName, String prefix) {
    if (attributeName == null) return null;
    if (prefix != null) {
      attributeName.replace("_" + prefix, "");
    }
    StringBuilder sb = new StringBuilder();
    sb.append(Character.toLowerCase(attributeName.charAt(0)));
    boolean isUnderscore = false;
    for (int i = 1; i < attributeName.length(); i++) {
      if (attributeName.charAt(i) == '_') {
        isUnderscore = true;
      } else {
        sb.append(
            (isUnderscore)
                ? Character.toUpperCase(attributeName.charAt(i))
                : Character.toLowerCase(attributeName.charAt(i)));
        isUnderscore = false;
      }
    }
    return sb.toString();
  }

  private static String attributeNameToSetter(String attributeName, String prefix) {
    String field = attributeNameToFieldName(attributeName, prefix);
    return "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
  }

  private static String fieldNameToGetter(String fieldName) {
    if (fieldName == null) return null;
    return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }

  private static void findAndSetField(Object o, String fieldName, Object value) {
    Field field = null;
    try {
      field = o.getClass().getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      Logger.error(o.getClass(), "Cannot find field " + fieldName, e);
    }
    if (field != null) {
      field.setAccessible(true);
      try {
        field.set(o, value);
      } catch (IllegalAccessException e) {
        Logger.error(o.getClass(), "Cannot set field " + fieldName, e);
      }
    }
  }

  public static String getAnnotatedFieldValue(Object o, Class clazz) {
    if (o == null) return null;
    for (var field : o.getClass().getDeclaredFields()) {
      if (field.getAnnotationsByType(clazz) == null
          || field.getAnnotationsByType(clazz).length == 0) continue;
      if (field.getType() == String.class) {
        field.setAccessible(true);
        try {
          return field.get(o).toString();
        } catch (IllegalAccessException e) {
          return null;
        }
      }
    }
    return null;
  }

  public static AdapterSettings getAdapterSettingsAnnotation(Class clazz) {
    var annotations = clazz.getAnnotationsByType(AdapterSettings.class);
    if (annotations == null || annotations.length < 1) return null;
    return ((AdapterSettings) annotations[0]);
  }

  public static Set<String> getAnnotatedSearchResultFields(Class clazz) {
    if (clazz == null) return Set.of();
    Set<String> result = new HashSet<>();
    for (var field : clazz.getDeclaredFields()) {
      if (field.getAnnotationsByType(AttributeSearchResult.class) != null
          && field.getAnnotationsByType(AttributeSearchResult.class).length > 0) {
        result.add(fieldNameToAttribute(field.getName()));
      }
    }
    return result;
  }

  private static Annotation getAnnotation(Field field, Class annotation) {
    if (field.getAnnotationsByType(annotation) != null
        && field.getAnnotationsByType(annotation).length > 0)
      return field.getAnnotationsByType(annotation)[0];
    return null;
  }

  private static boolean hasAnnotation(Field field, Class annotation) {
    if (field.getAnnotationsByType(annotation) != null
        && field.getAnnotationsByType(annotation).length > 0) return true;
    return false;
  }

  public static void populateAnnotatedMultivalueList(
      Field field,
      String attributeName,
      Object o,
      Set<Attribute> attributes,
      Set<Attribute> multiValueAdd,
      Set<Attribute> multiValueRemove,
      boolean creation) {
    String listField = field.getName();
    String addFieldName = listField + "toAdd";
    String removeFieldName = listField + "toRemove";
    if (creation) {
      List<String> values =
          AdapterValueTypeConverter.getMultipleAttributeValueNoEnum(
              List.class, attributes, attributeName);
      if (values != null) {
        List<String> newValues = new ArrayList<>(values);
        findAndSetField(o, listField, newValues);
      } else {
        findAndSetField(o, listField, Collections.emptyList());
      }
    } else {
      List<String> roles =
          AdapterValueTypeConverter.getMultipleAttributeValueNoEnum(
              List.class, multiValueAdd, attributeName);
      if (roles != null) {
        findAndSetField(o, addFieldName, roles);
      }
      roles =
          AdapterValueTypeConverter.getMultipleAttributeValueNoEnum(
              List.class, multiValueRemove, attributeName);
      if (roles != null) {
        findAndSetField(o, removeFieldName, roles);
      }
    }
  }

  public static void printEnumAsFields(Class clazz) {
    System.out.println(clazz.getSimpleName());

    var enums = clazz.getEnumConstants();
    if (enums != null) {
      for (int i = 0; i < enums.length; i++) {
        System.out.println(
            "private String " + attributeNameToFieldName(enums[i].toString(), "") + ";");
      }
    }
  }

  private static boolean isFieldMatch(
      String fromName, String fromPrefix, String toName, String toPrefix) {
    String left;
    String right;
    if (fromPrefix == null) fromPrefix = "";
    if (toPrefix == null) toPrefix = "";
    if (fromName == null) fromName = "";
    if (toName == null) toName = "";
    fromPrefix = fromPrefix.toLowerCase();
    toPrefix = toPrefix.toLowerCase();
    fromName = fromName.toLowerCase();
    toName = toName.toLowerCase();
    if (!fromPrefix.isEmpty()) {
      if (fromName.startsWith(fromPrefix)) {
        left = fromName.substring(fromPrefix.length());
      } else {
        return false;
      }
    } else {
      left = fromName;
    }
    if (!toPrefix.isEmpty()) {
      if (toName.startsWith(toPrefix)) {
        right = toName.substring(toPrefix.length());
      } else {
        return false;
      }
    } else {
      right = toName;
    }
    return left.equalsIgnoreCase(right);
  }

  public static void copyProperties(
      Object from, Object to, String fromPrefix, String toPrefix, String[] ignoreProperties) {
    if (from == null || to == null) return;
    if (ignoreProperties == null) ignoreProperties = new String[0];
    if (fromPrefix == null) fromPrefix = "";
    if (toPrefix == null) toPrefix = "";
    for (var fromField : from.getClass().getDeclaredFields()) {
      if (Arrays.stream(ignoreProperties)
          .noneMatch(
              n -> {
                return n.toLowerCase().equals(fromField.getName().toLowerCase());
              })) {
        for (var toField : to.getClass().getDeclaredFields()) {
          if (isFieldMatch(fromField.getName(), fromPrefix, toField.getName(), toPrefix)
              && fromField.getType().equals(toField.getType())) {
            toField.setAccessible(true);
            fromField.setAccessible(true);
            try {
              toField.set(to, fromField.get(from));
            } catch (Exception e) {
              Logger.error(
                  to,
                  "Cannot set field "
                      + fromField.getName()
                      + " to "
                      + toField.getName()
                      + " of type "
                      + toField.getType());
            }
            break;
          }
        }
      }
    }
  }

  private static List<String> populateListFromListOfObjects(List<Object> source, String fieldName) {
    if (source == null || source.isEmpty()) return Collections.emptyList();
    List<String> result = new ArrayList<>();
    for (Object o : source) {
      if (o != null) {
        Field f = null;
        try {
          f = o.getClass().getDeclaredField(fieldName);
          f.setAccessible(true);
          result.add(f.get(o).toString());
        } catch (Exception e) {
          Logger.error(o, "Cannot find field " + fieldName, e);
        }
      }
    }

    return result;
  }

  public static String getAnnotatedValue(Object o, Class clazz) {
    if (o == null) return null;
    for (var field : o.getClass().getDeclaredFields()) {
      if (field.getAnnotationsByType(clazz) == null
          || field.getAnnotationsByType(clazz).length == 0) continue;
      if (field.getType() == String.class) {
        field.setAccessible(true);
        try {
          Object value = field.get(o);
          if (value != null) {
            return field.get(o).toString();
          } else {
            return null;
          }

        } catch (Exception e) {
          return null;
        }
      }
    }
    return null;
  }

  /**
   * This is used for testing and troubleshooting to see what Attributes this helper will create.
   * Prints attribute names to System.out.
   *
   * @param ignoreAttributes - An array of Strings that are Attribute Names that you would not like
   *     to be output. If you use a prefix for child objects, you must include the prefix in the
   *     ignore list example PREFIX_FIELD_NAME.
   * @param prefix - String of the prefix. If you are going to process child objects, you should use
   *     a prefix to prevent duplication.
   * @param clazz - The class in which to use to create attributes.
   */
  public static void printAttributes(String[] ignoreAttributes, String prefix, Class clazz) {
    System.out.println("****Attribute Enum");
    var list = listAttributes(ignoreAttributes, prefix, clazz);
    for (var s : list) {
      System.out.println(s + ",");
    }
    System.out.println("\r\n");
  }

  public static <T> T instantiateClass(Class<T> clazz) {
    try {
      T o = clazz.getDeclaredConstructor().newInstance();
      return o;
    } catch (Exception e) {
      Logger.error(new Object(), "Cannot instantiate constructor for " + clazz.getName());
      throw new ConnectorException("Cannot Instantiate Class " + clazz, e);
    }
  }

  public static void logAttributes(Object o, Set<ConnectorAttribute> attributes) {
    if (attributes == null || attributes.isEmpty()) return;
    for (ConnectorAttribute attribute : attributes) {
      Logger.info(
          o,
          "Attribute: "
              + attribute.getName()
              + ", Type: "
              + attribute.getDataType()
              + ", Flags: "
              + attribute.getFlags());
    }
  }

  public static void logAttributeValues(Object o, Set<Attribute> attributes) {
    if (attributes == null || attributes.isEmpty()) return;
    for (Attribute attribute : attributes) {
      Logger.info(o, "Attribute: " + attribute.getName() + ", Value: " + attribute.getValue());
    }
  }
  public static Map<String, AttributeMetaInfo> getSchemaAttributeInfo(Class<?> clazz) {
    Map<String, AttributeMetaInfo> schemaMetaJson = new HashMap<>();
    for (var field : clazz.getDeclaredFields()) {
      if (isValidType(field)) {
        String attributeName = fieldNameToAttribute(field.getName());
        if (!hasAnnotation(field, AttributeIgnore.class)) {
          if (hasAnnotation(field, AttributeSchemaMetaInfo.class)) {
            int maxLength = ((AttributeSchemaMetaInfo) Objects.requireNonNull(
                getAnnotation(field, AttributeSchemaMetaInfo.class))).maxLength();
            if (maxLength < 255) {
              schemaMetaJson.put(attributeName, addMaxLengthConstraint(maxLength));
            }
          }
        }
      }
    }
    return schemaMetaJson;
  }
  public static String getAttributeNameForIdentityField(Class<?> clazz) {
    for (var field : clazz.getDeclaredFields()) {
      if (isValidType(field)
          && hasAnnotation(field, AttributeIdentityValue.class)) {
        String attributeName = fieldNameToAttribute(field.getName());
        return attributeName;
      }
    }
    return null;
  }
  public static String getAttributeNameForNameField(Class<?> clazz) {
    for (var field : clazz.getDeclaredFields()) {
      if (isValidType(field)
          && hasAnnotation(field, AttributeNameValue.class)) {
        String attributeName = fieldNameToAttribute(field.getName());
        return attributeName;
      }
    }
    return null;
  }
  public static AttributeMetaInfo addMaxLengthConstraint(int maxLength) {
    AttributeConstraint constraint = new AttributeConstraint();
    constraint.setDirection(AttributeConstraintDirection.OUTBOUND);
    constraint.setRule(AttributeConstraintRule.MAX_LENGTH);
    constraint.setRuleData(String.valueOf(maxLength));
    AttributeMetaInfo metaInfo = new AttributeMetaInfo();
    metaInfo.setConstraints(Collections.singletonList(constraint));
    return metaInfo;
  }
}
