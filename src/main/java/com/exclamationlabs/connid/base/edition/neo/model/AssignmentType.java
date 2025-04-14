package com.exclamationlabs.connid.base.edition.neo.model;

import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentType {
  private Set<String> currentInboundAssignments;
  private Set<String> addedOutboundAssignments;
  private Set<String> removedOutboundAssignments;

  public AssignmentType() {
    this.currentInboundAssignments = Collections.emptySet();
    this.addedOutboundAssignments = Collections.emptySet();
    this.removedOutboundAssignments = Collections.emptySet();
  }
}
