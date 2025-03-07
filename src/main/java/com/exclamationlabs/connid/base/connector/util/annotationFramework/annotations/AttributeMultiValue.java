package com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AttributeMultiValue {
  ConnectorAttributeDataType dataType() default ConnectorAttributeDataType.STRING;
}
