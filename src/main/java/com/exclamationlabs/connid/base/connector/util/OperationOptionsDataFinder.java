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

package com.exclamationlabs.connid.base.connector.util;

import com.exclamationlabs.connid.base.connector.logging.Logger;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.identityconnectors.framework.common.objects.OperationOptions;

/**
 * Utility to inspect utilized values that are held inside the Connid framework OperationOptions
 * data map.
 */
public class OperationOptionsDataFinder {

  public static final String PAGE_SIZE_KEY = "PAGE_SIZE";
  public static final String RESULTS_OFFSET_KEY = "PAGED_RESULTS_OFFSET";

  private OperationOptionsDataFinder() {}

  public static boolean hasPagingOptions(OperationOptions oo) {
    return hasPagingOptions(oo.getOptions());
  }

  public static boolean hasPagingOptions(Map<String, Object> operationOptionsData) {
    return operationOptionsData != null
        && operationOptionsData.containsKey(PAGE_SIZE_KEY)
        && operationOptionsData.containsKey(RESULTS_OFFSET_KEY);
  }

  public static boolean hasValidPagingOptions(Map<String, Object> operationOptionsData) {
    boolean valid = false;
    if (hasPagingOptions(operationOptionsData)) {
      Integer pageSize = getPageSize(operationOptionsData);
      if (pageSize != null && pageSize > 1) {
        if (getPageResultsOffset(operationOptionsData) != null) {
          valid = true;
        }
      }
    }
    return valid;
  }

  public static Integer getPageSize(OperationOptions oo) {
    return getPageSize(oo.getOptions());
  }

  public static Integer getPageSize(Map<String, Object> operationOptionsData) {
    Integer pageSize = null;
    if (operationOptionsData != null && operationOptionsData.containsKey(PAGE_SIZE_KEY)) {
      try {
        pageSize = Integer.parseInt(operationOptionsData.get(PAGE_SIZE_KEY).toString());
      } catch (NumberFormatException nfe) {
        Logger.warn(
            OperationOptionsDataFinder.class,
            String.format(
                "Invalid page size from OperationOptions: %s",
                operationOptionsData.get(PAGE_SIZE_KEY).toString()));
      }
    }

    return pageSize;
  }

  public static Integer getPageResultsOffset(OperationOptions oo) {
    return getPageResultsOffset(oo.getOptions());
  }

  public static Integer getPageResultsOffset(Map<String, Object> operationOptionsData) {
    Integer offset = null;
    if (operationOptionsData != null && operationOptionsData.containsKey(RESULTS_OFFSET_KEY)) {
      try {
        offset = Integer.parseInt(operationOptionsData.get(RESULTS_OFFSET_KEY).toString());
      } catch (NumberFormatException nfe) {
        Logger.warn(
            OperationOptionsDataFinder.class,
            String.format(
                "Invalid page results offset from OperationOptions: %s",
                operationOptionsData.get(RESULTS_OFFSET_KEY).toString()));
      }
    }

    return offset;
  }

  public static Integer getPageNumber(OperationOptions oo) {
    return getPageNumber(oo.getOptions());
  }

  public static Integer getPageNumber(Map<String, Object> operationOptionsData) {
    Integer offset = getPageResultsOffset(operationOptionsData);
    Integer pageSize = getPageSize(operationOptionsData);

    int pageNumber = 1;

    if (offset != null && pageSize != null && offset > pageSize) {
      pageNumber = 1 + (offset / pageSize);
    }

    return pageNumber;
  }

  public static Boolean getAllowPartialAttributeValues(Map<String, Object> operationOptionsData) {
    Object value = operationOptionsData.get(OperationOptions.OP_ALLOW_PARTIAL_ATTRIBUTE_VALUES);
    return (value instanceof Boolean && BooleanUtils.isTrue((Boolean) value));
  }
}
