package com.exclamationlabs.connid.base.connector.attribute.meta;

public class AttributeConstraint {
  private Boolean outbound = Boolean.FALSE;
  private Boolean inbound = Boolean.FALSE;

  private AttributeConstraintRule rule;

  private String ruleData;

  public Boolean getOutbound() {
    return outbound;
  }

  public void setOutbound(Boolean outbound) {
    this.outbound = outbound;
  }

  public Boolean getInbound() {
    return inbound;
  }

  public void setInbound(Boolean inbound) {
    this.inbound = inbound;
  }

  public AttributeConstraintRule getRule() {
    return rule;
  }

  public void setRule(AttributeConstraintRule rule) {
    this.rule = rule;
  }

  public String getRuleData() {
    return ruleData;
  }

  public void setRuleData(String ruleData) {
    this.ruleData = ruleData;
  }
}
