package com.exclamationlabs.connid.base.edition.neo.stub.sad.authenticator.wrongconfig;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.OtherConfiguration;
import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

public class AuthWrongConfigAuthenticator implements Authenticator<OtherConfiguration> {

  @Override
  public String authenticate(OtherConfiguration configuration) throws ConnectorSecurityException {
    return "";
  }
}
