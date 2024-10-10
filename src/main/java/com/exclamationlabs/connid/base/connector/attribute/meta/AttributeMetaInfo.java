package com.exclamationlabs.connid.base.connector.attribute.meta;

import java.util.List;

/**
 * This class and others in this package are used to provide an advised structure for the optional
 * attribute metaInfo. At this time, we're using this primarily to store attribute data
 * constraint/validation info.
 */
public class AttributeMetaInfo {
  List<AttributeConstraint> constraints;

  public List<AttributeConstraint> getConstraints() {
    return constraints;
  }

  public void setConstraints(List<AttributeConstraint> constraints) {
    this.constraints = constraints;
  }
}
