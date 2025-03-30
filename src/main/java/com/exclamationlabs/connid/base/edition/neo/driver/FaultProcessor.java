package com.exclamationlabs.connid.base.edition.neo.driver;

public interface FaultProcessor {

  default Class<Invocator<?, ?, ?>> forInvocator() {
    return null;
  }
}
