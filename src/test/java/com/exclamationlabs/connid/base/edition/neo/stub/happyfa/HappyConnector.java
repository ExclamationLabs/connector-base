package com.exclamationlabs.connid.base.edition.neo.stub.happyfa;

import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;

public class HappyConnector extends BaseConnector<StubConfiguration> {
  public HappyConnector() {
    super(StubConfiguration.class, false);
  }
}
