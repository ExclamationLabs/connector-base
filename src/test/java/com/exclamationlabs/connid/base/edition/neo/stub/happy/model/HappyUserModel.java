package com.exclamationlabs.connid.base.edition.neo.stub.happy.model;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttributeHolder;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelObjectClass;
import com.exclamationlabs.connid.base.edition.neo.model.ConnIdType;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import lombok.Getter;
import lombok.Setter;
import org.identityconnectors.framework.common.objects.AttributeInfo;

@ModelObjectClass("HappyUser")
@Getter
@Setter
public class HappyUserModel implements IdentityModel {

  @ModelAttribute(value = "USER_ID", identifier = ConnIdType.UID)
  private String userId;

  @ModelAttribute(value = "USER_NAME", identifier = ConnIdType.NAME)
  private String userName;

  @ModelAttribute private String firstName;

  @ModelAttribute private String lastName;

  @ModelAttribute(
      nativeName = "USER_EMAIL",
      flags = {AttributeInfo.Flags.NOT_CREATABLE, AttributeInfo.Flags.NOT_UPDATEABLE})
  private String email;

  @ModelAttribute(value = "USER_YEARS", type = ConnectorAttributeDataType.INTEGER)
  private Integer yearsOfService;

  @ModelAttribute(value = "IS_ACTIVE", type = ConnectorAttributeDataType.BOOLEAN)
  private Boolean active;

  @ModelAttributeHolder private HappyUserAddress address;

  @Override
  public String getIdentityIdValue() {
    return getUserId();
  }

  @Override
  public String getIdentityNameValue() {
    return getUserName();
  }
}
