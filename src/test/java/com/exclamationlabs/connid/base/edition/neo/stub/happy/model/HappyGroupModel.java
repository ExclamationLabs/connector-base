package com.exclamationlabs.connid.base.edition.neo.stub.happy.model;

import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelObjectClass;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;

@ModelObjectClass("HappyGroup")
public class HappyGroupModel implements IdentityModel {

  @ModelAttribute(value = "GROUP_ID", identifier = ConnIdType.UID)
  private String groupId;

  @ModelAttribute(value = "GROUP_NAME", identifier = ConnIdType.NAME)
  private String groupName;

  @ModelAttribute(value = "GROUP_DESCRIPTION")
  private String groupDescription;

  private String ignoredField;

  @Override
  public String getIdentityIdValue() {
    return "";
  }

  @Override
  public String getIdentityNameValue() {
    return "";
  }
}
