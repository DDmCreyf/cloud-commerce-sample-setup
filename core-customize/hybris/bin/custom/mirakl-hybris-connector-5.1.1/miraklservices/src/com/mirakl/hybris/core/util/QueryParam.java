package com.mirakl.hybris.core.util;

public class QueryParam {
  
  protected String attribute;
  protected Object value;
  protected String operator;

  public QueryParam(String attribute, Object value, String operator) {
    super();
    this.attribute = attribute;
    this.value = value;
    this.operator = operator;
  }

  public String getAttribute() {
    return attribute;
  }

  public Object getValue() {
    return value;
  }

  public String getOperator() {
    return operator;
  }

}
