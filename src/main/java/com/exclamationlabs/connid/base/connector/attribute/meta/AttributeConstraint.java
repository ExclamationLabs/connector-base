package com.exclamationlabs.connid.base.connector.attribute.meta;

public class AttributeConstraint {

  private AttributeConstraintDirection direction;

  private AttributeConstraintRule rule;

  private String ruleData;

  public AttributeConstraintDirection getDirection() {
    return direction;
  }

  public void setDirection(AttributeConstraintDirection direction) {
    this.direction = direction;
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
