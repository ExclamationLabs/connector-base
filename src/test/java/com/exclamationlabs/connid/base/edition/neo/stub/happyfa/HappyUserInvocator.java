package com.exclamationlabs.connid.base.edition.neo.stub.happyfa;

import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessInvocator;
import com.exclamationlabs.connid.base.edition.neo.model.AssignmentType;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.StubConfiguration;
import com.exclamationlabs.connid.base.edition.neo.stub.happyfa.model.HappyUserAddress;
import com.exclamationlabs.connid.base.edition.neo.stub.happyfa.model.HappyUserAddressType;
import com.exclamationlabs.connid.base.edition.neo.stub.happyfa.model.HappyUserModel;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.Map;
import java.util.Set;

public class HappyUserInvocator
    implements FullAccessInvocator<StubConfiguration, HappyDriver, HappyUserModel> {

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
        Set<String> groupIdSet = Set.of("1001", "1002");
        AssignmentType assignmentType = new AssignmentType();
        assignmentType.setCurrentInboundAssignments(groupIdSet);
        user.setGroupIds(assignmentType);
        return user;

    }

    @Override
    public String create(HappyDriver driver, StubConfiguration configuration, HappyUserModel model) throws ConnectorException {
        if ("JohnSmith123".equals(model.getUserName())) {
            return "123456";
        } else {
            return "BAD";
        }
    }

    @Override
    public void update(HappyDriver driver, StubConfiguration configuration, String userId, HappyUserModel userModel) throws ConnectorException {
        if (!StringUtils.equals(userId, userModel.getUserId())) {
            throw new ConnectorException("User ID mismatch");
        }
    }

    @Override
    public void delete(HappyDriver driver, StubConfiguration configuration, String userId) throws ConnectorException {
        if (!StringUtils.equals("123456", userId)) {
            throw new ConnectorException("User ID mismatch");
        }
    }
}
