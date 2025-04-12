package com.exclamationlabs.connid.base.edition.neo.stub.happyfa;

import com.exclamationlabs.connid.base.edition.neo.driver.FaultProcessor;
import com.exclamationlabs.connid.base.edition.neo.driver.Invocator;

public class HappyUserFaultProcessor implements FaultProcessor {

  @SuppressWarnings("unchecked")
  @Override
  public Class<Invocator<?, ?, ?>> forInvocator() {
    return (Class<Invocator<?, ?, ?>>) (Class<?>) HappyUserInvocator.class;
  }
}
