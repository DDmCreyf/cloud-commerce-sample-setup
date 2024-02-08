package com.mirakl.hybris.core.util.flexiblesearch.impl;

public class Join extends AbstractQueryComponent {

  private Item joinedEntity;
  private Field joinedFieldSource;
  private Field joinedFieldTarget;

  private Join(Item joinedEntity) {
    this.joinedEntity = joinedEntity;
  }

  public static Join entity(Item joinedEntity) {
    return new Join(joinedEntity);
  }

  public Join on(Field joinedFieldSource, Field joinedFieldTarget) {
    this.joinedFieldSource = joinedFieldSource;
    this.joinedFieldTarget = joinedFieldTarget;
    return this;
  }

  public Item getJoinedEntity() {
    return joinedEntity;
  }

  public Field getJoinedFieldSource() {
    return joinedFieldSource;
  }

  public Field getJoinedFieldTarget() {
    return joinedFieldTarget;
  }

  @Override
  protected StringBuilder append(StringBuilder str) {
    str.append(" JOIN ");
    joinedEntity.append(str);
    str.append(" ON {");
    joinedFieldSource.append(str);
    str.append("}={");
    joinedFieldTarget.append(str);
    str.append("}");

    return str;
  }

}
