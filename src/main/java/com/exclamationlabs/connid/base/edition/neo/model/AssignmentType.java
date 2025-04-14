package com.exclamationlabs.connid.base.edition.neo.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;

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
