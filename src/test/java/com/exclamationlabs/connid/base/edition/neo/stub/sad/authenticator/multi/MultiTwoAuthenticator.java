package com.exclamationlabs.connid.base.edition.neo.stub.sad.authenticator.multi;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

public class MultiTwoAuthenticator implements Authenticator<StubConfiguration> {

  @Override
  public String authenticate(StubConfiguration configuration) throws ConnectorSecurityException {
    return "";
  }
}
