package com.exclamationlabs.connid.base.edition.neo.stub.sad.driver.multi;

import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;

public class MultiDriverConnector extends BaseConnector<StubConfiguration> {
  public MultiDriverConnector() {
    super(StubConfiguration.class);
  }
}
