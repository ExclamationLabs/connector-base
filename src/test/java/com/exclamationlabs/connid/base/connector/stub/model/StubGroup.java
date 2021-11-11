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

package com.exclamationlabs.connid.base.connector.stub.model;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;

import java.util.Set;

public class StubGroup implements IdentityModel {

    private String id;
    private String name;
    private Set<String> supergroupIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getSupergroupIds() {
        return supergroupIds;
    }

    public void setSupergroupIds(Set<String> supergroupIds) {
        this.supergroupIds = supergroupIds;
    }

    @Override
    public String getIdentityIdValue() {
        return getId();
    }

    @Override
    public String getIdentityNameValue() {
        return getName();
    }
}
