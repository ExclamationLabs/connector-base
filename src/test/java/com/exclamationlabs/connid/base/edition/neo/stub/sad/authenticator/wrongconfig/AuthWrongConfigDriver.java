package com.exclamationlabs.connid.base.edition.neo.stub.sad.authenticator.wrongconfig;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.Driver;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import java.util.Map;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class AuthWrongConfigDriver implements Driver<StubConfiguration> {

  @Override
  public void initialize(
      StubConfiguration configuration, Authenticator<StubConfiguration> authenticator)
      throws ConnectorException {}

  @Override
  public void test(StubConfiguration configuration) throws ConnectorException {}

  @Override
  public void close() {}

  @Override
  public Set<IdentityModel> getAll(
      StubConfiguration configuration,
      Class<? extends IdentityModel> identityModelClass,
      ResultsFilter resultsFilter,
      ResultsPaginator pagination,
      Integer resultCap,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    return Set.of();
  }

  @Override
  public IdentityModel getOne(
      StubConfiguration configuration,
      Class<? extends IdentityModel> identityModelClass,
      String idValue,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    return null;
  }

  @Override
  public Map<String, Object> getPrefetch(
      StubConfiguration configuration, Class<? extends IdentityModel> identityModelClass) {
    return Map.of();
  }
}
