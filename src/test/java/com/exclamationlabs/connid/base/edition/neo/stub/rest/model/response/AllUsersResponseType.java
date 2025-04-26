package com.exclamationlabs.connid.base.edition.neo.stub.rest.model.response;

import com.exclamationlabs.connid.base.edition.neo.stub.rest.model.RestUserModel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllUsersResponseType {

  private List<RestUserModel> people;
}
