package com.exclamationlabs.connid.base.connector.adapter.result;

public final class IdentityResultRecord {

    private final String id;
    private final String name;

    public IdentityResultRecord(String idIn, String nameIn) {
        id = idIn;
        name = nameIn;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
