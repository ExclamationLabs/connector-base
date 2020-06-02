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

import java.util.List;

public class ErrorResponse {

    private Integer code;
    private String message;

    private List<ErrorData> errors;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ErrorData> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorData> errors) {
        this.errors = errors;
    }

    public String getErrorDetails() {
        if (getErrors() == null || getErrors().isEmpty()) {
            return null;
        } else {
            StringBuilder response = new StringBuilder();
            for (ErrorData data : getErrors()) {
                response.append("; FIELD:");
                response.append(data.getField());
                response.append(", MESSAGE:");
                response.append(data.getMessage());
            }
            return response.toString();
        }
    }
}
