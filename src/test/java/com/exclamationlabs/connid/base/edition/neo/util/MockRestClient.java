package com.exclamationlabs.connid.base.edition.neo.util;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.RestConfiguration;
import com.exclamationlabs.connid.base.edition.neo.driver.rest.RestBehavior;
import com.exclamationlabs.connid.base.edition.neo.driver.rest.RestClient;
import java.net.http.HttpClient;

public class MockRestClient<T extends RestConfiguration> extends RestClient<T> {
  private final HttpClient mockHttpClient;

  public MockRestClient(
      T configuration,
      Authenticator<T> authenticator,
      RestBehavior<T> behavior,
      HttpClient mockHttpClient) {
    super(configuration, authenticator, behavior);
    this.mockHttpClient = mockHttpClient;
  }

  @Override
  protected HttpClient createClient() {
    return mockHttpClient;
  }
}
