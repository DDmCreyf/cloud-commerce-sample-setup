package com.mirakl.hybris.core.util.flexiblesearch.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import jersey.repackaged.com.google.common.base.Joiner;

public class GroupByClause extends AbstractQueryComponent {

  private List<Field> groupByFields;

  public GroupByClause() {
    groupByFields = new ArrayList<>();
  }

  public void addGroupByField(Field field) {
    groupByFields.add(field);
  }

  public List<Field> getGroupByFields() {
    return groupByFields;
  }

  @Override
  protected StringBuilder append(StringBuilder str) {
    if (CollectionUtils.isNotEmpty(groupByFields)) {
      str.append(" GROUP BY {") //
          .append(Joiner.on("},{").join(groupByFields)) //
          .append("}");
    }

    return str;
  }

}
