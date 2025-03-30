package com.exclamationlabs.connid.base.edition.neo.stub.sad.authenticator.wrongconfig;

import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelObjectClass;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;

@ModelObjectClass("Testing")
public class AuthWrongConfigModel implements IdentityModel {

  @Override
  public String getIdentityIdValue() {
    return "";
  }

  @Override
  public String getIdentityNameValue() {
    return "";
  }
}
