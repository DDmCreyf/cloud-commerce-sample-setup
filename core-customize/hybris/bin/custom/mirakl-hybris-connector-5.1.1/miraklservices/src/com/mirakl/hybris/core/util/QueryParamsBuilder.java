package com.mirakl.hybris.core.util;

import java.util.ArrayList;
import java.util.List;

public class QueryParamsBuilder {

  protected List<QueryParam> params = new ArrayList<>();

  private QueryParamsBuilder() {
    // No instanciation
  }

  public static QueryParamsBuilder queryParams() {
    return new QueryParamsBuilder();
  }

  public QueryParamsBuilder withAttributeEquals(String attribute, Object value) {
    params.add(new QueryParam(attribute, value, "="));
    return this;
  }

  public QueryParamsBuilder withAttributeMatchingCondition(String attribute, String operator, Object value) {
    params.add(new QueryParam(attribute, value, operator));
    return this;
  }

  public List<QueryParam> build() {
    return params;
  }
}
