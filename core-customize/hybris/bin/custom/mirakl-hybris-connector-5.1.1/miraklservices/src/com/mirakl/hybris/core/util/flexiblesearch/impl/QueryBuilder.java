package com.mirakl.hybris.core.util.flexiblesearch.impl;

import com.mirakl.hybris.core.util.flexiblesearch.QueryDecorator;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {

  protected SelectClause selectClause;
  protected FromClause fromClause;
  protected WhereClause whereClause;
  protected GroupByClause groupByClause;
  protected List<QueryDecorator> queryDecorators;

  private QueryBuilder() {
    selectClause = new SelectClause();
    fromClause = new FromClause();
    whereClause = new WhereClause();
    groupByClause = new GroupByClause();
    queryDecorators = new ArrayList<>();
  }

  public static QueryBuilder query() {
    return new QueryBuilder();
  }

  public static QueryBuilder query(List<QueryDecorator> queryDecorators) {
    QueryBuilder queryBuilder = new QueryBuilder();
    queryBuilder.queryDecorators = queryDecorators;
    return queryBuilder;
  }

  public QueryBuilder selectCount(Field field) {
    selectClause.addSelectField(field, true);
    return this;
  }

  public QueryBuilder select(Field field) {
    selectClause.addSelectField(field);
    return this;
  }

  public QueryBuilder select(Field field, boolean isCount) {
    selectClause.addSelectField(field, isCount);
    return this;
  }

  public QueryBuilder from(Item item) {
    fromClause.setFromItem(item);
    return this;
  }

  public QueryBuilder join(Join joinEntity) {
    fromClause.addJoinEntity(joinEntity);
    return this;
  }

  public QueryBuilder where(Condition condition) {
    whereClause.addCondition(condition);
    return this;
  }

  public QueryBuilder and(Condition condition) {
    whereClause.addAndCondition(condition);
    return this;
  }

  public QueryBuilder or(Condition condition) {
    whereClause.addOrCondition(condition);
    return this;
  }

  public QueryBuilder groupBy(Field field) {
    groupByClause.addGroupByField(field);
    return this;
  }



  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    selectClause.append(str);
    fromClause.append(str);
    whereClause.append(str);
    groupByClause.append(str);

    return str.toString();
  }

  public FlexibleSearchQuery build() {
    for (QueryDecorator queryDecorator : queryDecorators) {
      queryDecorator.decorate(this);
    }
    return new FlexibleSearchQuery(this.toString(), whereClause.getParams());
  }

}
