package com.exclamationlabs.connid.base.connector.adapter.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IdentityResult {

    private final long generatedTimestamp;
    private final String uuid;

    private long lastUsedTimestamp;
    private List<IdentityResultRecord> cachedResults;
    private Map<String, String> searchCriteria;


    public IdentityResult() {
        uuid = UUID.randomUUID().toString();
        long currentTimestamp = System.currentTimeMillis();
        lastUsedTimestamp = currentTimestamp;
        generatedTimestamp = currentTimestamp;
    }

    public long getGeneratedTimestamp() {
        return generatedTimestamp;
    }

    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    public List<IdentityResultRecord> getCachedResults() {
        return cachedResults;
    }

    public void setCachedResults(List<IdentityResultRecord> cachedResults) {
        this.cachedResults = cachedResults;
    }

    public Map<String, String> getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(Map<String, String> searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public String getUuid() {
        return uuid;
    }

    public void addResult(String id, String name) {
        if (cachedResults == null) {
            cachedResults = new ArrayList<>();
        }
        cachedResults.add(new IdentityResultRecord(id, name));
    }
}
