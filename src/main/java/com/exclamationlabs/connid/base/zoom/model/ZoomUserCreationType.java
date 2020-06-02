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

package com.exclamationlabs.connid.base.zoom.model;

public enum ZoomUserCreationType {

    CREATE("create"),
    AUTO_CREATE("autoCreate"),
    CUSTOM_CREATE("custCreate"),
    SSO_CREATE("ssoCreate");

    private String zoomName;

    ZoomUserCreationType(String name) {
        zoomName = name;
    }

    public String getZoomName() {
        return zoomName;
    }
}
