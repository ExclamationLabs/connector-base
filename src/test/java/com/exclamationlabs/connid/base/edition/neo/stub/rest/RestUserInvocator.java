package com.exclamationlabs.connid.base.edition.neo.stub.rest;

import com.exclamationlabs.connid.base.connector.configuration.basetypes.RestConfiguration;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessInvocator;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.RestTestConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.rest.model.RestUserModel;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.Map;
import java.util.Set;

public class RestUserInvocator
    implements FullAccessInvocator<RestTestConfiguration, RestDriver, RestUserModel> {

  @Override
  public String create(RestDriver driver, RestTestConfiguration configuration, RestUserModel model) throws ConnectorException {
    return "";
  }

  @Override
  public void update(RestDriver driver, RestTestConfiguration configuration, String userId, RestUserModel userModel) throws ConnectorException {

  }

  @Override
  public void delete(RestDriver driver, RestTestConfiguration configuration, String userId) throws ConnectorException {

  }

  @Override
  public Set<RestUserModel> getAll(RestDriver driver, RestTestConfiguration configuration, ResultsFilter filter, ResultsPaginator paginator, Integer resultCap, Map<String, Object> prefetchDataMap) throws ConnectorException {
    return Set.of();
  }

  @Override
  public RestUserModel getOne(RestDriver driver, RestTestConfiguration configuration, String objectId, Map<String, Object> prefetchDataMap) throws ConnectorException {
    return null;
  }
}
