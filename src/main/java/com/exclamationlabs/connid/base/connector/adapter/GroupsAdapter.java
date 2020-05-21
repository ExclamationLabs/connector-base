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

public interface GroupsAdapter<U extends UserIdentityModel, G extends GroupIdentityModel>
        extends Adapter<U, G> {

    @Override
    default ObjectClass getType() {
        return ObjectClass.ACCOUNT;
    }

    G constructModel(Set<Attribute> attributes, boolean creation);

    ConnectorObject constructConnectorObject(G modelType);

    @Override
    default Uid create(Set<Attribute> attributes) {
        G group = constructModel(attributes, true);
        String newGroupId = getDriver().createGroup(group);

        return new Uid(newGroupId);
    }

    @Override
    default Uid update(Uid uid, Set<Attribute> attributes) {
        G group = constructModel(attributes, false);
        getDriver().updateGroup(uid.getUidValue(), group);
        return uid;
    }

    @Override
    default void delete(Uid uid) {
        getDriver().deleteGroup(uid.getUidValue());
    }

    @Override
    default void get(String query, ResultsHandler resultsHandler) {
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
