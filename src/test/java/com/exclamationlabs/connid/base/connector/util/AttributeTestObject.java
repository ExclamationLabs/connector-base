package com.exclamationlabs.connid.base.connector.util;

import java.time.ZonedDateTime;

public class AttributeTestObject {
  private String stringValue;
  private Integer integerValue;
  private Long longValue;
  private Float floatValue;
  private Double doubleValue;
  private Boolean booleanValue;
  private ZonedDateTime dateValue;
  private String ignoreValue;

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  public Integer getIntegerValue() {
    return integerValue;
  }

  public void setIntegerValue(Integer integerValue) {
    this.integerValue = integerValue;
  }

  public Long getLongValue() {
    return longValue;
  }

  public void setLongValue(Long longValue) {
    this.longValue = longValue;
  }

  public Float getFloatValue() {
    return floatValue;
  }

  public void setFloatValue(Float floatValue) {
    this.floatValue = floatValue;
  }

  public Double getDoubleValue() {
    return doubleValue;
  }

  public void setDoubleValue(Double doubleValue) {
    this.doubleValue = doubleValue;
  }

  public Boolean getBooleanValue() {
    return booleanValue;
  }

  public void setBooleanValue(Boolean booleanValue) {
    this.booleanValue = booleanValue;
  }

  public ZonedDateTime getDateValue() {
    return dateValue;
  }

  public void setDateValue(ZonedDateTime dateValue) {
    this.dateValue = dateValue;
  }

  public String getIgnoreValue() {
    return ignoreValue;
  }

  public void setIgnoreValue(String ignoreValue) {
    this.ignoreValue = ignoreValue;
  }

  @Override
  public String toString() {
    return "AttributeTestObject{"
        + "stringValue='"
        + stringValue
        + '\''
        + ", integerValue="
        + integerValue
        + ", longValue="
        + longValue
        + ", floatValue="
        + floatValue
        + ", doubleValue="
        + doubleValue
        + ", booleanValue="
        + booleanValue
        + ", dateValue="
        + dateValue
        + ", ignoreValue='"
        + ignoreValue
        + '\''
        + '}';
  }
}
