package com.exclamationlabs.connid.base.zoom.model;

import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.google.gson.annotations.SerializedName;

public class ZoomGroup implements GroupIdentityModel {

    private String id;
    private String name;

    @SerializedName("total_members")
    private Integer totalMembers;

    @Override
    public String getIdentityIdValue() {
        return getId();
    }

    @Override
    public String getIdentityNameValue() {
        return getName();
    }

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

    public Integer getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Integer totalMembers) {
        this.totalMembers = totalMembers;
    }
}
