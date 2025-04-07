package com.exclamationlabs.connid.base.edition.neo.stub.happy;

import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.Invocator;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.happy.model.HappyUserAddress;
import com.exclamationlabs.connid.base.edition.neo.stub.happy.model.HappyUserAddressType;
import com.exclamationlabs.connid.base.edition.neo.stub.happy.model.HappyUserModel;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class HappyUserInvocator
    implements Invocator<StubConfiguration, HappyDriver, HappyUserModel> {

  @Override
  public Set<HappyUserModel> getAll(
      HappyDriver driver,
      StubConfiguration configuration,
      ResultsFilter filter,
      ResultsPaginator paginator,
      Integer resultCap,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    return Set.of(
            buildTestUserModel("1234", "happy"),
            buildTestUserModel("1235", "sneezy"),
            buildTestUserModel("1236", "grumpy")
    );
  }

  @Override
  public HappyUserModel getOne(
      HappyDriver driver,
      StubConfiguration configuration,
      String objectId,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    if (objectId.equals("1234")) {
        return buildTestUserModel("1234", "happy");
    } else {
      return null;
    }
  }

    private static HappyUserModel buildTestUserModel(final String idValue, final String namePart) {
        var user = new HappyUserModel();
        user.setUserId(idValue);
        user.setUserName(namePart + "user");
        user.setFirstName(StringUtils.capitalize(namePart));
        user.setLastName("User");
        user.setActive(true);
        user.setYearsOfService(3);
        var address = new HappyUserAddress();
        address.setStreet(String.format("123 %s St", StringUtils.capitalize(namePart)));
        address.setCity(StringUtils.capitalize(namePart) + "ville");
        address.setState("CA");
        address.setZip("12345");
        var addressType = new HappyUserAddressType();
        addressType.setType("home");
        addressType.setDescription("Home Address");
        address.setAddressType(addressType);
        user.setAddress(address);
        return user;

    }
}
