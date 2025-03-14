package com.exclamationlabs.connid.base.connector.util.annotationFramework;

import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.connector.util.annotationFramework.annotations.AdapterSettings;
import java.util.HashSet;
import java.util.Set;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;

public class AnnotationAdapter<T extends AnnotatedIdentityModel, U extends ConnectorConfiguration>
    extends BaseAdapter<T, U> {
  protected Class<T> clazz;
  protected AdapterSettings settings;

  public AnnotationAdapter(Class<T> AnnotatedIdentityModelClass) {
    this.clazz = AnnotatedIdentityModelClass;
    settings = AttributeUtils.getAdapterSettingsAnnotation(clazz);
    if (settings == null) {
      throw new RuntimeException("AnnotationIdentiyModels must have an AdapterSettings annotation");
    }
  }

  @Override
  public ObjectClass getType() {
    return new ObjectClass(settings.type());
  }

  @Override
  public Class<T> getIdentityModelClass() {
    return clazz;
  }

  @Override
  public Set<ConnectorAttribute> getConnectorAttributes() {
    Set<ConnectorAttribute> result = new HashSet<>();
    AttributeUtils.createAttributesFromAnnotations(clazz, result);
    T o = AttributeUtils.instantiateClass(clazz);
    o.modifyConnectorAttributes(result, this.getDriver());
    if (settings.logAttributes()) {
      Logger.info(this, "Logging getConnectorAttributes");
      AttributeUtils.logAttributes(this, result);
    }
    return result;
  }

  @Override
  protected Set<Attribute> constructAttributes(T model) {
    Set<Attribute> attributes = new HashSet<>();
    model.beforeConstructAttributes(model, attributes, this.getDriver());
    AttributeUtils.constructAttributesFromAnnotations(model, attributes);
    model.afterConstructAttributes(model, attributes, this.getDriver());
    if (settings.logAttributes()) {
      Logger.info(this, "Logging constructAttributes");
      AttributeUtils.logAttributeValues(this, attributes);
    }
    return attributes;
  }

  @Override
  protected T constructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate) {
    T o = AttributeUtils.instantiateClass(clazz);
    o.beforeConstructModel(
        attributes,
        addedMultiValueAttributes,
        removedMultiValueAttributes,
        isCreate,
        o,
        this.getDriver());
    AttributeUtils.constructModelFromAnnotations(
        attributes, addedMultiValueAttributes, removedMultiValueAttributes, isCreate, o);
    o.afterConstructModel(
        attributes,
        addedMultiValueAttributes,
        removedMultiValueAttributes,
        isCreate,
        o,
        this.getDriver());
    if (settings.logAttributes()) {
      Logger.info(this, "Logging constructModel");
      Logger.info(this, o.toString());
    }
    return o;
  }
}
