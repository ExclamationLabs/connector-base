package com.exclamationlabs.connid.base.connector.util.annotationFramework;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributeIdentityValue;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AttributeNameValue;
import java.util.Set;
import org.identityconnectors.framework.common.objects.Attribute;

public abstract class AnnotatedIdentityModel implements IdentityModel {
  protected void modifyConnectorAttributes(Set<ConnectorAttribute> result, Driver driver) {}

  protected void beforeConstructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate,
      AnnotatedIdentityModel annotatedIdentityModel,
      Driver driver) {}

  protected void afterConstructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate,
      AnnotatedIdentityModel annotatedIdentityModel,
      Driver driver) {}

  protected void beforeConstructAttributes(
      AnnotatedIdentityModel identityModel, Set<Attribute> attributes, Driver driver) {}

  protected void afterConstructAttributes(
      AnnotatedIdentityModel identityModel, Set<Attribute> attributes, Driver driver) {}

  @Override
  public String getIdentityIdValue() {
    return AttributeUtils.getAnnotatedValue(this, AttributeIdentityValue.class);
  }

  @Override
  public String getIdentityNameValue() {
    return AttributeUtils.getAnnotatedValue(this, AttributeNameValue.class);
  }

  @Override
  public String getValueBySearchableAttributeName(String attributeName) {
    return IdentityModel.super.getValueBySearchableAttributeName(attributeName);
  }

  @Override
  public String identityToString() {
    return IdentityModel.super.identityToString();
  }

  @Override
  public boolean identityEquals(
      Class<? extends IdentityModel> identityClass, IdentityModel compareSource, Object compareTo) {
    return IdentityModel.super.identityEquals(identityClass, compareSource, compareTo);
  }

  @Override
  public int identityHashCode() {
    return IdentityModel.super.identityHashCode();
  }
}
