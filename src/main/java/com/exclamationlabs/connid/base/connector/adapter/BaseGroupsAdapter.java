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

package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import org.identityconnectors.framework.common.objects.*;

import java.util.List;
import java.util.Set;

/**
 * Base groups adapter that needs to be subclassed in order to map a specific group model
 * data type to ConnId attributes and vice versa.  This extends BaseAdapter
 * so that the Adapter has composition and can reference the driver.
 */
public abstract class BaseGroupsAdapter<U extends UserIdentityModel, G extends GroupIdentityModel>
        extends BaseAdapter<U,G> {

    protected abstract G constructGroup(Set<Attribute> attributes, boolean creation);

    protected abstract ConnectorObject constructConnectorObject(G modelType);

    @Override
    protected ObjectClass getType() {
        return ObjectClass.GROUP;
    }

    @Override
    public Uid create(Set<Attribute> attributes) {
        G group = constructGroup(attributes, true);
        String newGroupId = getDriver().createGroup(group);

        return new Uid(newGroupId);
    }

    @Override
    public Uid update(Uid uid, Set<Attribute> attributes) {
        G group = constructGroup(attributes, false);
        getDriver().updateGroup(uid.getUidValue(), group);
        return uid;
    }

    @Override
    public void delete(Uid uid) {
        getDriver().deleteGroup(uid.getUidValue());
    }

    @Override
    public void get(String query, ResultsHandler resultsHandler) {
        if (queryAllRecords(query)) {
            // query for all groups
            List<G> allGroups = getDriver().getGroups();

            for (GroupIdentityModel currentGroup : allGroups) {
                resultsHandler.handle(
                        new ConnectorObjectBuilder()
                                .setUid(currentGroup.getIdentityIdValue())
                                .setName(currentGroup.getIdentityNameValue())
                                .setObjectClass(getType())
                                .build());
            }
        } else {
            // Query for single group
            G singleGroup = getDriver().getGroup(query);
            if (singleGroup != null) {
                resultsHandler.handle(constructConnectorObject(singleGroup));
            }
        }
    }
}
