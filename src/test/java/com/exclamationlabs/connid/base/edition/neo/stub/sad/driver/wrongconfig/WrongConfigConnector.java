package com.exclamationlabs.connid.base.edition.neo.stub.sad.driver.wrongconfig;

import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;

public class WrongConfigConnector extends BaseConnector<StubConfiguration> {
  public WrongConfigConnector() {
    super(StubConfiguration.class);
  }
}
