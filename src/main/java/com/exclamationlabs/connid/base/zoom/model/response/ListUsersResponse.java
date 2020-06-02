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

package com.exclamationlabs.connid.base.zoom.model.response;

import com.exclamationlabs.connid.base.zoom.model.ZoomUser;

import java.util.List;

public class ListUsersResponse {

    private List<ZoomUser> users;

    public List<ZoomUser> getUsers() {
        return users;
    }

    public void setUsers(List<ZoomUser> users) {
        this.users = users;
    }
}
