package com.exclamationlabs.connid.base.edition.neo.stub.happyfa.model;

import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttribute;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelAttributeHolder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HappyUserAddress {

  @ModelAttribute("ADDRESS_STREET")
  private String street;

  @ModelAttribute("ADDRESS_CITY")
  private String city;

  @ModelAttribute("ADDRESS_STATE")
  private String state;

  @ModelAttribute("ADDRESS_ZIP")
  private String zip;

  @ModelAttributeHolder private HappyUserAddressType addressType;

  private String description;
}
