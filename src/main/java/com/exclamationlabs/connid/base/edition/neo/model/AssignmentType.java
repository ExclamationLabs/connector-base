/*
    Copyright 2025 Exclamation Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.exclamationlabs.connid.base.edition.neo.model;

import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * This is a wrapper class used to consolidate multiple identifiers related to the special
 * ASSIGNMENT_IDENTIFIER data type, which is used to link object classes together (particular a set
 * of group id's that belong to a user).
 *
 * <p>'currentInboundAssignments' represents a set of id's that are currently already associated
 * with the assignee. For example, if a user is already a member of a group id's A and B, then this
 * field would hold A and B.
 *
 * <p>'addedOutboundAssignments' represents a set of id's that are not currently associated with the
 * assignee and need to be assigned. For example, if a user is not currently a member of group id's
 * A and B but needs to be associated to these groups, then this field would hold A and B.
 *
 * <p>'removedOutboundAssignments' represents a set of id's that are currently associated with the
 * assignee and need to be removed. For example, if a user is currently a member of group id's A and
 * B but needs to no longer be associated to these groups, then this field would hold A and B.
 */
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
