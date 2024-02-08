package com.mirakl.hybris.core.util.flexiblesearch.impl;

public class Condition extends AbstractQueryComponent {

  enum Conjunction {
    AND, OR
  }


  protected Conjunction conjunction;
  protected Field field;
  protected String operator;
  protected Object value;

  Condition(Field field, String operator, Object value) {
    super();
    this.field = field;
    this.operator = operator;
    this.value = value;
  }

  public static Condition condition(Field field, String operator, Object value) {
    return new Condition(field, operator, value);
  }

  public static Condition fieldEquals(Field field, Object value) {
    return new Condition(field, "=", value);
  }

  public Field getField() {
    return field;
  }

  public String getOperator() {
    return operator;
  }

  public Object getValue() {
    return value;
  }

  public Conjunction getConjunction() {
    return conjunction;
  }

  public void setConjunction(Conjunction conjunction) {
    this.conjunction = conjunction;
  }

  public String getParamName() {
    return field.getItemAlias() + "_" + field.getFieldName();
  }

  @Override
  protected StringBuilder append(StringBuilder str) {
    if (conjunction != null) {
      str.append(" ").append(conjunction.name());
    }
    str.append(" {");
    field.append(str);
    str.append("} ").append(operator).append(" ?").append(getParamName());

    return str;
  }

}
