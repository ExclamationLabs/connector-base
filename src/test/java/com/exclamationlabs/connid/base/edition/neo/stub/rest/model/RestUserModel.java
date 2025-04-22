package com.exclamationlabs.connid.base.edition.neo.stub.rest.model;

import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelObjectClass;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import lombok.Getter;
import lombok.Setter;

@ModelObjectClass("RestUser")
@Getter
@Setter
public class RestUserModel implements IdentityModel {

  @ModelAttribute(value = "USER_ID", identifier = ConnIdType.UID)
  private String userId;

  @ModelAttribute(value = "USER_NAME", identifier = ConnIdType.NAME)
  private String userName;

  @ModelAttribute private String firstName;

  @ModelAttribute private String lastName;

}
