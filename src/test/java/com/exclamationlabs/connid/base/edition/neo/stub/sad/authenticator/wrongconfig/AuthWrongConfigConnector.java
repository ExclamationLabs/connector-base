package com.exclamationlabs.connid.base.edition.neo.stub.sad.authenticator.wrongconfig;

import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;

public class AuthWrongConfigConnector extends BaseConnector<StubConfiguration> {
  public AuthWrongConfigConnector() {
    super(StubConfiguration.class);
  }
}
