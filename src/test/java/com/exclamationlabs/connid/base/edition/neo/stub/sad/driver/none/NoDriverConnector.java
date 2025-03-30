package com.exclamationlabs.connid.base.edition.neo.stub.sad.driver.none;

import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;

public class NoDriverConnector extends BaseConnector<StubConfiguration> {
  public NoDriverConnector() {
    super(StubConfiguration.class);
  }
}
