package com.exclamationlabs.connid.base.connector.util.annotationFramework;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import java.util.Set;
import org.identityconnectors.framework.common.objects.Attribute;

public interface CustomAnnotatedIdentityModel {
  void modifyConnectorAttributes(Set<ConnectorAttribute> result, Driver driver);

  void beforeConstructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate,
      AnnotatedIdentityModel annotatedIdentityModel,
      Driver driver);

  void afterConstructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate,
      AnnotatedIdentityModel annotatedIdentityModel,
      Driver driver);

  void beforeConstructAttributes(
      AnnotatedIdentityModel identityModel, Set<Attribute> attributes, Driver driver);

  void afterConstructAttributes(
      AnnotatedIdentityModel identityModel, Set<Attribute> attributes, Driver driver);
}
