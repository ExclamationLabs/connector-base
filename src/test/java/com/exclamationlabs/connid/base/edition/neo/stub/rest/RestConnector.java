package com.exclamationlabs.connid.base.edition.neo.stub.rest;

import com.exclamationlabs.connid.base.edition.neo.BaseConnector;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.RestTestConfiguration;

public class RestConnector extends BaseConnector<RestTestConfiguration> {
  public RestConnector() {
    super(RestTestConfiguration.class, false);
  }
}
