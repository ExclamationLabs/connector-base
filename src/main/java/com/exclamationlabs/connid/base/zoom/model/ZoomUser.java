package com.exclamationlabs.connid.base.zoom.model;

import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import com.exclamationlabs.connid.base.zoom.attribute.ZoomUserAttribute;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ZoomUser implements UserIdentityModel {

    private String id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    private String password;

    private String email;

    private String timezone;

    private String language;

    @SerializedName("phone_number")
    private String phoneNumber;

    private String status;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("last_login_time")
    private String lastLoginTime;

    private String verified;

    @SerializedName("pmi")
    private Long personalMeetingId;

    private Integer type;

    @SerializedName("group_ids")
    private List<String> groupIds;

    @Override
    public String getIdentityIdValue() {
        return getId();
    }

    @Override
    public String getIdentityNameValue() {
        return getEmail();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public Long getPersonalMeetingId() {
        return personalMeetingId;
    }

    public void setPersonalMeetingId(Long personalMeetingId) {
        this.personalMeetingId = personalMeetingId;
    }

    public Integer getType() {
        return type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public String getAssignedGroupsAttributeName() {
        return ZoomUserAttribute.GROUP_IDS.name();
    }

    @Override
    public List<String> getAssignedGroupIds() {
        return getGroupIds();
    }
}
