package com.exclamationlabs.connid.base.connector.model;

public class DefaultGroup implements AccessManagementModel {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAccessManagementIdValue() {
        return getId();
    }

    @Override
    public String getAccessManagementNameValue() {
        return getName();
    }
}
