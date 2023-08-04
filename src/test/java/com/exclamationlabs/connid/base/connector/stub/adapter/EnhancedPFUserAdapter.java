/*
    Copyright 2020 Exclamation Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.exclamationlabs.connid.base.connector.stub.adapter;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.*;
import static com.exclamationlabs.connid.base.connector.stub.attribute.EnhancedPFUserAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.adapter.EnhancedPaginationAndFiltering;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.stub.configuration.EnhancedPFConfiguration;
import com.exclamationlabs.connid.base.connector.stub.model.EnhancedPFUser;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.identityconnectors.framework.common.objects.*;

public class EnhancedPFUserAdapter extends BaseAdapter<EnhancedPFUser, EnhancedPFConfiguration>
    implements EnhancedPaginationAndFiltering {

  @Override
  public ObjectClass getType() {
    return new ObjectClass("pfUser");
  }

  @Override
  public Class<EnhancedPFUser> getIdentityModelClass() {
    return EnhancedPFUser.class;
  }

  @Override
  public Set<ConnectorAttribute> getConnectorAttributes() {
    Set<ConnectorAttribute> result = new HashSet<>();
    result.add(new ConnectorAttribute(Uid.NAME, USER_ID.name(), STRING, NOT_UPDATEABLE));
    result.add(new ConnectorAttribute(Name.NAME, EMAIL.name(), STRING, NOT_UPDATEABLE));

    result.add(new ConnectorAttribute(FIRST_NAME.name(), STRING));
    result.add(new ConnectorAttribute(LAST_NAME.name(), STRING));
    result.add(new ConnectorAttribute(DEPARTMENT.name(), STRING));
    result.add(new ConnectorAttribute(LOCATION.name(), STRING));
    result.add(new ConnectorAttribute(JOB_TITLE.name(), STRING));
    result.add(new ConnectorAttribute(DETAIL.name(), STRING));

    return result;
  }

  @Override
  protected EnhancedPFUser constructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate) {
    // Read-only connector, not used
    return new EnhancedPFUser();
  }

  @Override
  protected Set<Attribute> constructAttributes(EnhancedPFUser user) {
    Set<Attribute> attributes = new HashSet<>();

    attributes.add(AttributeBuilder.build(FIRST_NAME.name(), user.getFirstName()));
    attributes.add(AttributeBuilder.build(LAST_NAME.name(), user.getLastName()));
    attributes.add(AttributeBuilder.build(JOB_TITLE.name(), user.getJobTitle()));
    attributes.add(AttributeBuilder.build(DEPARTMENT.name(), user.getDepartment()));
    attributes.add(AttributeBuilder.build(LOCATION.name(), user.getLocation()));
    attributes.add(AttributeBuilder.build(DETAIL.name(), user.getDetail()));

    return attributes;
  }

  @Override
  public boolean getSearchResultsContainsAllAttributes() {
    return false;
  }

  @Override
  public boolean getSearchResultsContainsNameAttribute() {
    return false;
  }

  @Override
  public boolean getOneByName() {
    return false;
  }

  @Override
  public Set<String> getSearchResultsAttributesPresent() {
    return Collections.emptySet();
  }

  @Override
  public boolean getFilteringRequiresFullImport() {
    return false;
  }
}
