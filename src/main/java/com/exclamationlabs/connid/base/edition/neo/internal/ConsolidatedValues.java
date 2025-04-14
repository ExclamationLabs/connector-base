package com.exclamationlabs.connid.base.edition.neo.internal;

import java.util.Collections;
import java.util.Set;
import org.identityconnectors.framework.common.objects.Attribute;

public class ConsolidatedValues {

  public final Set<Attribute> modifiedValues;
  public final Set<Attribute> addedMultiValues;
  public final Set<Attribute> removedMultiValues;

  public ConsolidatedValues(Set<Attribute> modified) {
    modifiedValues = modified;
    addedMultiValues = Collections.emptySet();
    removedMultiValues = Collections.emptySet();
  }

  public ConsolidatedValues(Set<Attribute> modified, Set<Attribute> added, Set<Attribute> removed) {
    modifiedValues = modified;
    addedMultiValues = added;
    removedMultiValues = removed;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("addedMultiValues: [");
    for (Attribute current : addedMultiValues) {
      builder.append(current.toString());
    }
    builder.append("],");
    builder.append("modifiedValues: [");
    for (Attribute current : modifiedValues) {
      builder.append(current.toString());
    }
    builder.append("],");
    builder.append("removedMultiValues: [");
    for (Attribute current : removedMultiValues) {
      builder.append(current.toString());
    }
    builder.append("]");
    return builder.toString();
  }
}
