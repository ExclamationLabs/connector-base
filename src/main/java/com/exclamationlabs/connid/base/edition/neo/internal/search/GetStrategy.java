package com.exclamationlabs.connid.base.edition.neo.internal.search;

import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetStrategy {
  private GetStrategyType type;
  private String matchValue;
  private Class<? extends IdentityModel> identityModelClass;
}
