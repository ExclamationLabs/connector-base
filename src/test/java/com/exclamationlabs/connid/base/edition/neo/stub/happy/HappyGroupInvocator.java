package com.exclamationlabs.connid.base.edition.neo.stub.happy;

import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.Invocator;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.happy.model.HappyGroupModel;
import java.util.Map;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class HappyGroupInvocator
    implements Invocator<StubConfiguration, HappyDriver, HappyGroupModel> {

  @Override
  public Set<HappyGroupModel> getAll(
      HappyDriver driver,
      StubConfiguration configuration,
      ResultsFilter filter,
      ResultsPaginator paginator,
      Integer resultCap,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    return Set.of();
  }

  @Override
  public HappyGroupModel getOne(
      HappyDriver driver,
      StubConfiguration configuration,
      String objectId,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    return null;
  }
}
