package com.mirakl.hybris.core.util.flexiblesearch.impl;

import org.apache.commons.lang3.BooleanUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectClause extends AbstractQueryComponent {

  private Map<Field, Boolean> selectFields;

  public SelectClause() {
    selectFields = new LinkedHashMap<>();
  }

  @Override
  protected StringBuilder append(StringBuilder queryString) {
    return queryString.append("SELECT ")
        .append(selectFields.entrySet().stream().map(SelectClause::buildSelectField).collect(Collectors.joining(", ")));
  }

  public void addSelectField(Field field) {
    addSelectField(field, Boolean.FALSE);
  }

  public void addSelectField(Field field, Boolean isCount) {
    selectFields.put(field, isCount);
  }

  private static StringBuilder buildSelectField(Map.Entry<Field, Boolean> select) {
    StringBuilder selectField = new StringBuilder();
    if (BooleanUtils.isTrue(select.getValue())) {
      selectField.append("COUNT(");
    }
    selectField.append("{") //
        .append(select.getKey()) //
        .append("}");
    if (BooleanUtils.isTrue(select.getValue())) {
      selectField.append(")");
    }
    return selectField;
  }

}
