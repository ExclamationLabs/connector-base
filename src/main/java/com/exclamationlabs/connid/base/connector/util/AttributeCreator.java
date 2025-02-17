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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    if (result != null) {
      if (notUpdateableAttributes == null) notUpdateableAttributes = new String[0];
      if (notCreateableAttributes == null) notCreateableAttributes = new String[0];
      if (ignoreAttributes == null) ignoreAttributes = new String[0];
      if (prefix == null) prefix = "";
      if (result != null) {
        for (var field : clazz.getDeclaredFields()) {
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
                .anyMatch(notUpdateableAttribute -> notUpdateableAttribute.equals(attributeName))) {
              flags.add(NOT_UPDATEABLE);
            }
            var type = getDataType(field);
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
    if (ignoreAttributes == null) ignoreAttributes = new String[0];
    if (prefix == null) prefix = "";
    if (attributes != null && o != null) {
      if (o != null && attributes != null) {
        Class clazz = o.getClass();
        for (var field : clazz.getDeclaredFields()) {
          String attributeName =
              (prefix.isEmpty())
                  ? fieldNameToAttribute(field.getName())
                  : prefix + "_" + fieldNameToAttribute(field.getName());
          if (!Arrays.stream(ignoreAttributes)
              .anyMatch(ignoreAttribute -> ignoreAttribute.equals(attributeName))) {
            Object val = null;

            try {
              var getter = o.getClass().getMethod(fieldNameToGetter(field.getName()));
              if (getter != null) {
                val = getter.invoke(o);
              }
            } catch (InvocationTargetException e) {
              Logger.error(o.getClass(), "Error constructing attribute value", e);
            } catch (NoSuchMethodException e) {
              Logger.error(o.getClass(), "Error constructing attribute value", e);
            } catch (IllegalAccessException e) {
              Logger.error(o.getClass(), "Error constructing attribute value", e);
            }
            attributes.add(AttributeBuilder.build(attributeName, val));
          }
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
          String setterString = attributeNameToSetter(cleanedAttributeName, prefix);
          try {
            var setter = findMethod(setterString, o);
            var paramType = setter.getParameterTypes()[0];
            Object val = attributeName.getValue().get(0);
            if (val != null && setter != null) {
              if (paramType == String.class) {
                setter.invoke(o, val.toString());
              } else if (paramType == Integer.class) {
                setter.invoke(o, Integer.parseInt(val.toString()));
              } else if (paramType == Long.class) {
                setter.invoke(o, Long.parseLong(val.toString()));
              } else if (paramType == Boolean.class) {
                setter.invoke(o, Boolean.parseBoolean(val.toString()));
              } else if (paramType == Float.class) {
                setter.invoke(o, Float.parseFloat(val.toString()));
              } else if (paramType == Double.class) {
                setter.invoke(o, Double.parseDouble(val.toString()));
              } else if (paramType == ZonedDateTime.class) {
                if (val instanceof ZonedDateTime) {
                  setter.invoke(o, (ZonedDateTime) val);
                } else {
                  Logger.info(o.getClass(), "Data type is Date but value is not Date");
                }
              } else {
                Logger.info(o.getClass(), "Data type not supported " + attributeName);
              }
              if (setter == null) {
                Logger.info(o.getClass(), "Cannot find setter method " + setterString);
              }
            }
          } catch (Exception e) {
            Logger.error(o.getClass(), "Cannot invoke setter method " + setterString, e);
          }
        }
      }
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
      String attributeName =
          (prefix.isEmpty())
              ? fieldNameToAttribute(field.getName())
              : prefix + "_" + fieldNameToAttribute(field.getName());
      if (!Arrays.stream(ignoreAttributes)
          .anyMatch(ignoreAttribute -> ignoreAttribute.equals(attributeName))) {
        out.add(attributeName);
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
    var list = listAttributes(ignoreAttributes, prefix, clazz);
    for (var s : list) {
      System.out.println(s);
    }
  }

  private static Method findMethod(String name, Object o) {
    var clazz = o.getClass();
    Method[] methods = clazz.getDeclaredMethods();
    for (var method : methods) {
      if (method.getName().equals(name)) {
        return method;
      }
    }
    return null;
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

  private static String attributeNameToSetter(String attributeName, String prefix) {
    if (attributeName == null) return null;
    if (prefix != null) {
      attributeName.replace("_" + prefix, "");
    }
    StringBuilder sb = new StringBuilder();
    sb.append(Character.toUpperCase(attributeName.charAt(0)));
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
    return "set" + sb.toString();
  }

  private static String fieldNameToGetter(String fieldName) {
    if (fieldName == null) return null;
    return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }
}
