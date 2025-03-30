package com.exclamationlabs.connid.base.edition.neo.stub.sad.model.nomodels;

import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;

public class NoModelsConnector extends BaseConnector<StubConfiguration> {
  public NoModelsConnector() {
    super(StubConfiguration.class);
  }
}
