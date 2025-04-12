package com.exclamationlabs.connid.base.edition.neo.stub.happyfa.model;

import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HappyUserAddressType {

  @ModelAttribute("ADDRESS_TYPE")
  private String type;

  private String description;
}
