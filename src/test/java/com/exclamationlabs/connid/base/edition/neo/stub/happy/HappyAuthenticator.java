package com.exclamationlabs.connid.base.edition.neo.stub.happy;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

public class HappyAuthenticator implements Authenticator<StubConfiguration> {
  @Override
  public String authenticate(StubConfiguration configuration) throws ConnectorSecurityException {
    return "HAPPY";
  }
}
