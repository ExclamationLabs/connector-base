package com.exclamationlabs.connid.base.edition.neo.stub.rest;

import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessInvocator;
import com.exclamationlabs.connid.base.edition.neo.driver.rest.RestClient;
import com.exclamationlabs.connid.base.edition.neo.driver.rest.RestResponse;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.RestTestConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.rest.model.RestUserModel;
import com.exclamationlabs.connid.base.edition.neo.stub.rest.model.response.AllUsersResponseType;
import java.util.Map;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class RestUserInvocator
    implements FullAccessInvocator<RestTestConfiguration, RestDriver, RestUserModel> {

  @Override
  public String create(RestDriver driver, RestTestConfiguration configuration, RestUserModel model)
      throws ConnectorException {
    RestClient<RestTestConfiguration> client = driver.getClient(configuration);
    RestResponse<RestUserModel> response = client.post("createUser", model, RestUserModel.class);
    return response.getResponseBody().getUserId();
  }

  @Override
  public void update(
      RestDriver driver,
      RestTestConfiguration configuration,
      String userId,
      RestUserModel userModel)
      throws ConnectorException {
    RestClient<RestTestConfiguration> client = driver.getClient(configuration);
    client.post("updateUser", userModel, RestUserModel.class);
  }

  @Override
  public void delete(RestDriver driver, RestTestConfiguration configuration, String userId)
      throws ConnectorException {
    RestClient<RestTestConfiguration> client = driver.getClient(configuration);
    client.delete("deleteUser/" + userId, RestUserModel.class);
  }

  @Override
  public Set<RestUserModel> getAll(
      RestDriver driver,
      RestTestConfiguration configuration,
      ResultsFilter filter,
      ResultsPaginator paginator,
      Integer resultCap,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    RestClient<RestTestConfiguration> client = driver.getClient(configuration);
    RestResponse<AllUsersResponseType> response = client.get("oneUser", AllUsersResponseType.class);
    return Set.of(response.getResponseBody().getPeople().toArray(new RestUserModel[0]));
  }

  @Override
  public RestUserModel getOne(
      RestDriver driver,
      RestTestConfiguration configuration,
      String objectId,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    RestClient<RestTestConfiguration> client = driver.getClient(configuration);
    RestResponse<RestUserModel> response = client.get("oneUser", RestUserModel.class);
    return response.getResponseBody();
  }
}
