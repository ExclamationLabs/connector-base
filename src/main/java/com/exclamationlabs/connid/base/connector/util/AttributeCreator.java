package com.exclamationlabs.connid.base.connector.util;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.BOOLEAN;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.DOUBLE;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.FLOAT;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.INTEGER;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.LONG;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.ZONED_DATE_TIME;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_CREATABLE;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo.Flags;

public class AttributeCreator {

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
    createAttributes(result, ignoreAttributes, notUpdateableAttributes, notCreateableAttributes, prefix, clazz, false);
  }
  private static void createAttributes(
      Set<ConnectorAttribute> result,
      String[] ignoreAttributes,
      String[] notUpdateableAttributes,
      String[] notCreateableAttributes,
      String prefix,
      Class clazz, boolean print) {
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
          if(isValidType(field)) {
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
              var type = getDataType(field);
              attributeNames.add(attributeName);
              if (flags.isEmpty()) {
                if (print) {
                  System.out.println(
                      "result.add(new ConnectorAttribute(" + attributeName + ".name(), "
                          + type.name() + ");");
                }
                result.add(new ConnectorAttribute(attributeName, type));
              } else {
                if (print) {
                  System.out.println(
                      "result.add(new ConnectorAttribute(" + attributeName + ".name(), "
                          + type.name() + ", " + flags.toString().replace("[", "").replace("]", "")
                          + ");");
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
    if(field.getType() == String.class
        || field.getType() == Boolean.class
        || field.getType()== Integer.class
        || field.getType() == Long.class
        || field.getType() == Double.class
        || field.getType() == Float.class
        || field.getType() == ZonedDateTime.class) {return true;}
    else{ return false; }
  }
  private static ConnectorAttributeDataType getDataType(Field field) {
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
    return STRING;
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
      Set<Attribute> attributes, String[] ignoreAttributes, String prefix, Object o, boolean print) {
    if(print){
      System.out.println("**** constructAttributes");
    }
    if (ignoreAttributes == null) ignoreAttributes = new String[0];
    if (prefix == null) prefix = "";
    if (attributes != null && o != null) {
      if (o != null && attributes != null) {
        Class clazz = o.getClass();
        for (var field : clazz.getDeclaredFields()) {
          if(isValidType(field)) {
            String attributeName =
                (prefix.isEmpty())
                    ? fieldNameToAttribute(field.getName())
                    : prefix + "_" + fieldNameToAttribute(field.getName());
            if (!Arrays.stream(ignoreAttributes)
                .anyMatch(ignoreAttribute -> ignoreAttribute.equals(attributeName))) {
              Object val = null;

              try {
                if(print){
                  System.out.println("attributes.add(AttributeBuilder.build("+attributeName+".name(), o."+fieldNameToGetter(field.getName())+"()));");
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
    if(print){
      System.out.println("\r\n");
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
      Set<Attribute> attributes, String[] ignoreAttributes, String prefix, Object o,boolean print) {
    if(print){
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
              if (attributeName.getValue() != null)
                val = attributeName.getValue().get(0);
              if (print) {
                System.out.println(
                    " o." + setterString + "(AdapterValueTypeConverter.getSingleAttributeValue("
                        + paramType.getSimpleName() + ".class, attributes, " + attributeName.getName()
                        + "));");
              }
              if (val != null ) {
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
            Logger.error(o.getClass(), "Cannot construct model :"+ attributeName+ ", Field " + fieldString, e);
          }
        }
      }
    }
    if(print){
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
      if(isValidType(field)) {
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
  public static void printAttributes(String[] ignoreAttributes, String prefix, Class clazz) {
    System.out.println("****Attribute Enum");
    var list = listAttributes(ignoreAttributes, prefix, clazz);
    for (var s : list) {
      System.out.println(s+ ",");
    }
    System.out.println("\r\n");
  }

  /**
   * The method generates code for adapter to System.out
   *
   * @param ignoreAttributes - An array of Strings that are Attribute Names that you would not like
   *    *     to be output. If you use a prefix for child objects, you must include the prefix in the
   *    *     ignore list example PREFIX_FIELD_NAME.
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
  public static void codeGenerate(  String[] ignoreAttributes,
      String[] notUpdateableAttributes,
      String[] notCreateableAttributes,
      String prefix,
      Object o){
    printAttributes(ignoreAttributes, prefix, o.getClass());
    Set<ConnectorAttribute> result = new HashSet<ConnectorAttribute>();
    createAttributes(result,ignoreAttributes,notUpdateableAttributes,notCreateableAttributes,prefix,o.getClass(),true);
    Set<Attribute> attributes = new HashSet<>();
    constructAttributes(attributes,ignoreAttributes,prefix,o,true );
    constructModel(attributes,ignoreAttributes,prefix,o,true );

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
    return  sb.toString();
  }

  private static String attributeNameToSetter(String attributeName, String prefix) {
    String field = attributeNameToFieldName(attributeName, prefix);
    return "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
  }

  private static String fieldNameToGetter(String fieldName) {
    if (fieldName == null) return null;
    return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }
}
