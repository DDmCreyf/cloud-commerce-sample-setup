package com.mirakl.hybris.core.util.flexiblesearch.impl;

public class Field extends AbstractQueryComponent {

  private static final String FIELD_SEPARATOR = ":";
  private String itemAlias;
  private String fieldName;

  private Field(String itemAlias, String fieldName) {
    this.itemAlias = itemAlias;
    this.fieldName = fieldName;
  }

  public static Field field(String itemAlias, String field) {
    return new Field(itemAlias, field);
  }

  public String getItemAlias() {
    return itemAlias;
  }

  public String getFieldName() {
    return fieldName;
  }

  @Override
  public StringBuilder append(StringBuilder str) {
    return str.append(itemAlias).append(FIELD_SEPARATOR).append(fieldName);
  }

}
