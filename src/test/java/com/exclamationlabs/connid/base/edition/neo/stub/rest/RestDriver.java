package com.exclamationlabs.connid.base.edition.neo.stub.rest;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessDriver;
import com.exclamationlabs.connid.base.edition.neo.driver.rest.RestClient;
import com.exclamationlabs.connid.base.edition.neo.driver.rest.RestResponse;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.RestTestConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.rest.model.response.RestTestResponseTypeTest;
import com.exclamationlabs.connid.base.edition.neo.util.ConnectivityTester;
import com.exclamationlabs.connid.base.edition.neo.util.MockRestClient;
import com.exclamationlabs.connid.base.edition.neo.util.TestPoint;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.Map;
import java.util.Set;

public class RestDriver implements FullAccessDriver<RestTestConfiguration> {

  @Override
  public String create(RestTestConfiguration configuration, Class<? extends IdentityModel> identityModelClass, IdentityModel model) throws ConnectorException {
    return "";
  }

  @Override
  public void update(RestTestConfiguration configuration, Class<? extends IdentityModel> identityModelClass, String objectId, IdentityModel userModel) throws ConnectorException {

  }

  @Override
  public void delete(RestTestConfiguration configuration, Class<? extends IdentityModel> identityModelClass, String objectId) throws ConnectorException {

  }

  @Override
  public void initialize(RestTestConfiguration configuration, Authenticator<RestTestConfiguration> authenticator) throws ConnectorException {

  }

  @Override
  public void test(RestTestConfiguration configuration) throws ConnectorException {
    RestClient<RestTestConfiguration> client = new MockRestClient<>(configuration, new RestAuthenticator(), new RestTestBehavior(),
            ConnectivityTester.getMockClient());
    RestResponse<RestTestResponseTypeTest> response = client.get("mine", RestTestResponseTypeTest.class);
    if (response == null || response.getResponseBody() == null) {
      throw new ConnectorException("Invalid response");
    }
    RestTestResponseTypeTest testType = response.getResponseBody();
    ConnectivityTester.setPoint(TestPoint.DRIVER_TEST, testType.getCustomTestResponse());
  }

  @Override
  public void close() {

  }

  @Override
  public Set<IdentityModel> getAll(RestTestConfiguration configuration, Class<? extends IdentityModel> identityModelClass, ResultsFilter resultsFilter, ResultsPaginator pagination, Integer resultCap, Map<String, Object> prefetchDataMap) throws ConnectorException {
    return Set.of();
  }

  @Override
  public IdentityModel getOne(RestTestConfiguration configuration, Class<? extends IdentityModel> identityModelClass, String idValue, Map<String, Object> prefetchDataMap) throws ConnectorException {
    return null;
  }

  @Override
  public Map<String, Object> getPrefetch(RestTestConfiguration configuration, Class<? extends IdentityModel> identityModelClass) {
    return Map.of();
  }
}
