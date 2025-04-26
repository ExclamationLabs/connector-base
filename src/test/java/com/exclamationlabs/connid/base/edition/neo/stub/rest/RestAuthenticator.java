package com.exclamationlabs.connid.base.edition.neo.stub.rest;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.RestTestConfiguration;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

public class RestAuthenticator implements Authenticator<RestTestConfiguration> {
  @Override
  public String authenticate(RestTestConfiguration configuration)
      throws ConnectorSecurityException {
    return "HAPPY-REST";
  }
}
