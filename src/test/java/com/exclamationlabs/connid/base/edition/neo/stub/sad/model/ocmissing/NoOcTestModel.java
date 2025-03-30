package com.exclamationlabs.connid.base.edition.neo.stub.sad.model.ocmissing;

import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoOcTestModel implements IdentityModel {

  private String some;
  private String other;

  @Override
  public String getIdentityIdValue() {
    return getSome();
  }

  @Override
  public String getIdentityNameValue() {
    return getOther();
  }
}
