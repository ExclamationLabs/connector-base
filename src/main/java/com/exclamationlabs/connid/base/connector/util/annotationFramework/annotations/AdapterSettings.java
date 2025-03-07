package com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AdapterSettings {
  public String type() default "";

  public boolean resultsContainsAllAttributes() default false;

  public boolean resultsContainsNameAttribute() default true;

  public boolean getOneByName() default true;

  public boolean filteringRequiresFullImport() default true;
  public boolean logAttributes() default false;
}
