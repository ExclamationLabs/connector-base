package com.exclamationlabs.connid.base.edition.neo.stub.sad.model.ocmissing;

import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;

public class NoOcModelsConnector extends BaseConnector<StubConfiguration> {
  public NoOcModelsConnector() {
    super(StubConfiguration.class);
  }
}
