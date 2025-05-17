package com.exclamationlabs.connid.base.edition.neo.annotation.model;

import com.exclamationlabs.connid.base.edition.neo.IamType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a model's object class represented as a String. If needed, you can use
 * ObjectClass.createSpecialName("ACCOUNT") or ObjectClass.createSpecialName("GROUP") to create a
 * standard name for User or Group object class type.
 *
 * <p>This annotation should only be placed on classes that implement the IdentityModel marker
 * interface.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModelObjectClass {
  String value();

  /**
   * The applicable object type that this attribute is associated with. Default is UNDEFINED.
   *
   * @return IamType of the attribute (UNDEFINED is default if not specified)
   */
  IamType forType() default IamType.UNDEFINED;
}
