package com.exclamationlabs.connid.base.edition.neo.stub.sad.authenticator.multi;

import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;

public class MultiAuthenticatorConnector extends BaseConnector<StubConfiguration> {
  public MultiAuthenticatorConnector() {
    super(StubConfiguration.class);
  }
}
