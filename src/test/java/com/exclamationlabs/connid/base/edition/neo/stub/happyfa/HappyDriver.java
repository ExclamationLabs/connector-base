package com.exclamationlabs.connid.base.edition.neo.stub.happyfa;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessDriver;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.edition.neo.util.ConnectivityTester;
import com.exclamationlabs.connid.base.edition.neo.util.TestPoint;
import java.util.Map;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class HappyDriver implements FullAccessDriver<StubConfiguration> {

  @Override
  public void initialize(
      StubConfiguration configuration, Authenticator<StubConfiguration> authenticator)
      throws ConnectorException {}

  @Override
  public void test(StubConfiguration configuration) throws ConnectorException {
    ConnectivityTester.setPoint(TestPoint.DRIVER_TEST, this.getClass().getSimpleName() + ":test");
  }

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

  @Override
  public String create(
      StubConfiguration configuration,
      Class<? extends IdentityModel> identityModelClass,
      IdentityModel model)
      throws ConnectorException {
    return "";
  }

  @Override
  public void update(
      StubConfiguration configuration,
      Class<? extends IdentityModel> identityModelClass,
      String objectId,
      IdentityModel userModel)
      throws ConnectorException {}

  @Override
  public void delete(
      StubConfiguration configuration,
      Class<? extends IdentityModel> identityModelClass,
      String objectId)
      throws ConnectorException {}
}
