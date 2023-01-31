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

package com.exclamationlabs.connid.base.connector.driver.exception;

import org.identityconnectors.framework.common.exceptions.ConnectorSecurityException;

/**
 * This exception can be caught/thrown when a driver attempts a call but determines that it's access
 * token or credentials have expired. If this particular exception is thrown it is an indicator that
 * the driver cannot/should not attempt to reauthenticate and should instead throw the exception
 * back to MidPoint.
 */
public class DriverTokenExpiredException extends ConnectorSecurityException {

  public DriverTokenExpiredException() {
    super();
  }

  public DriverTokenExpiredException(String message) {
    super(message);
  }

  public DriverTokenExpiredException(String message, Throwable cause) {
    super(message, cause);
  }
}
