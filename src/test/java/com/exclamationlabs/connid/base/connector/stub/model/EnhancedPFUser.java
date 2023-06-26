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

package com.exclamationlabs.connid.base.connector.stub.model;

import static com.exclamationlabs.connid.base.connector.stub.attribute.EnhancedPFUserAttribute.*;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.stub.attribute.EnhancedPFUserAttribute;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;

public class EnhancedPFUser implements IdentityModel {

  private String userId; // UID
  private String email; // NAME

  private String firstName;
  private String lastName;
  private String department;
  private String jobTitle;
  private String location;

  private String detail;

  public EnhancedPFUser() {}

  public EnhancedPFUser(
      String userId,
      String email,
      String firstName,
      String lastName,
      String department,
      String jobTitle,
      String location) {
    setUserId(userId);
    setEmail(email);
    setFirstName(firstName);
    setLastName(lastName);
    setDepartment(department);
    setJobTitle(jobTitle);
    setLocation(location);
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

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  @Override
  public String getIdentityIdValue() {
    return getUserId();
  }

  @Override
  public String getIdentityNameValue() {
    return getEmail();
  }

  @Override
  public String getValueBySearchableAttributeName(String attributeName) {
    String value = null;
    if (StringUtils.equalsIgnoreCase(attributeName, Uid.NAME)) {
      return getIdentityIdValue();
    }
    if (StringUtils.equalsIgnoreCase(attributeName, Name.NAME)) {
      return getIdentityNameValue();
    }
    switch (EnhancedPFUserAttribute.valueOf(attributeName)) {
      case USER_ID:
        value = getIdentityIdValue();
        break;
      case EMAIL:
        value = getIdentityNameValue();
        break;
      case FIRST_NAME:
        value = getFirstName();
        break;
      case LAST_NAME:
        value = getLastName();
        break;
      case LOCATION:
        value = getLocation();
        break;
      case DEPARTMENT:
        value = getDepartment();
        break;
      case JOB_TITLE:
        value = getJobTitle();
        break;
      default:
        break;
    }
    return value;
  }

  @Override
  public boolean equals(Object input) {
    return identityEquals(StubUser.class, this, input);
  }

  @Override
  public int hashCode() {
    return identityHashCode();
  }

  @Override
  public String toString() {
    return identityToString();
  }
}
