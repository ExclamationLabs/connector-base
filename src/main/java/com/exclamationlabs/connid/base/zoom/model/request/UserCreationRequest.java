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

package com.exclamationlabs.connid.base.zoom.model.request;

import com.exclamationlabs.connid.base.zoom.model.ZoomUser;
import com.google.gson.annotations.SerializedName;

public final class UserCreationRequest {

    private final String action;

    @SerializedName("user_info")
    private final ZoomUser user;

    public UserCreationRequest(String actionInput, ZoomUser userInput) {
        action = actionInput;
        user = userInput;
    }

    public String getAction() {
        return action;
    }

    public ZoomUser getUser() {
        return user;
    }
}
