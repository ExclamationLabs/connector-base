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

package com.exclamationlabs.connid.base.zoom.model.response.fault;

public interface ErrorResponseCode {
    int VALIDATION_FAILED = 300;

    int USER_NOT_FOUND = 1001;
    int USER_ALREADY_EXISTS = 1005;

    int GROUP_NOT_FOUND = 4130;
    int GROUP_NAME_ALREADY_EXISTS = 4132;
}
