package com.exclamationlabs.connid.base.edition.neo.stub.happyfa;

import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessInvocator;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.happyfa.model.HappyGroupModel;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.Map;
import java.util.Set;

public class HappyGroupInvocator
    implements FullAccessInvocator<StubConfiguration, HappyDriver, HappyGroupModel> {


  @Override
  public String create(HappyDriver driver, StubConfiguration configuration, HappyGroupModel model) throws ConnectorException {
    return "";
  }

  @Override
  public void update(HappyDriver driver, StubConfiguration configuration, String userId, HappyGroupModel userModel) throws ConnectorException {

  }

  @Override
  public void delete(HappyDriver driver, StubConfiguration configuration, String userId) throws ConnectorException {

  }

  @Override
  public Set<HappyGroupModel> getAll(HappyDriver driver, StubConfiguration configuration, ResultsFilter filter, ResultsPaginator paginator, Integer resultCap, Map<String, Object> prefetchDataMap) throws ConnectorException {
    return Set.of();
  }

  @Override
  public HappyGroupModel getOne(HappyDriver driver, StubConfiguration configuration, String objectId, Map<String, Object> prefetchDataMap) throws ConnectorException {
    return null;
  }
}
