package com.mirakl.hybris.core.util.flexiblesearch;

import com.mirakl.hybris.core.util.flexiblesearch.impl.QueryBuilder;

public interface QueryDecorator {

  /**
   * Decorates a given {@link QueryBuilder} and adds specific clauses
   * 
   * @param queryBuilder the query builder to enrich
   * 
   */
  void decorate(QueryBuilder queryBuilder);

}
