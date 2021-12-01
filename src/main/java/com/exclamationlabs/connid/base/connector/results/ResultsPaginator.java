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

package com.exclamationlabs.connid.base.connector.results;

import org.apache.commons.lang3.BooleanUtils;

/**
 * ResultsPaginator is used to store, if applicable, the current pagination
 * state of data being returned by a driver/invocator through getAll.
 */
public class ResultsPaginator {

    private Integer totalResults;
    private Integer pageSize;
    private Integer currentOffset;
    private Integer currentPageNumber;
    private Integer numberOfProcessedPages;
    private Integer numberOfProcessedResults;
    private Integer numberOfTotalPages;

    private Boolean noMoreResults;
    private Boolean allowPartialAttributeValues;

    private Object token;

    public ResultsPaginator() {
        setPageSize(null);
    }

    public ResultsPaginator(Integer pageSizeIn, Integer offsetIn) {
        setPageSize(pageSizeIn);
        setCurrentOffset(offsetIn);

        int pageNumber = 1;
        if (getCurrentOffset() > getPageSize()) {
            pageNumber = 1 + (getCurrentOffset() / getPageSize());
        }
        setCurrentPageNumber(pageNumber);

        setTotalResults(0);
        setNumberOfProcessedPages(0);
        setNumberOfProcessedResults(0);
        setNoMoreResults(false);
    }

    public boolean hasPagination() {
        return getPageSize() != null;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(Integer currentOffset) {
        this.currentOffset = currentOffset;
    }

    public Integer getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(Integer currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public Integer getNumberOfProcessedPages() {
        return numberOfProcessedPages;
    }

    public void setNumberOfProcessedPages(Integer numberOfProcessedPages) {
        this.numberOfProcessedPages = numberOfProcessedPages;
    }

    public Integer getNumberOfProcessedResults() {
        return numberOfProcessedResults;
    }

    public void setNumberOfProcessedResults(Integer numberOfProcessedResults) {
        this.numberOfProcessedResults = numberOfProcessedResults;
    }

    public Integer getNumberOfTotalPages() {
        return numberOfTotalPages;
    }

    public void setNumberOfTotalPages(Integer numberOfTotalPages) {
        this.numberOfTotalPages = numberOfTotalPages;
    }

    public Boolean getNoMoreResults() {
        return noMoreResults;
    }

    public void setNoMoreResults(Boolean noMoreResults) {
        this.noMoreResults = noMoreResults;
    }

    public Boolean getAllowPartialAttributeValues() {
        return allowPartialAttributeValues;
    }

    public void setAllowPartialAttributeValues(Boolean allowPartialAttributeValues) {
        this.allowPartialAttributeValues = allowPartialAttributeValues;
    }

    public Object getToken() {
        return token;
    }

    public void setToken(Object token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return hasPagination() ? ("[size:" + getPageSize() + ",offset:" + getCurrentOffset() +
                ",done=" + (BooleanUtils.isTrue(getNoMoreResults()) ? "true" : "false") + "]") : "none";
    }
}
