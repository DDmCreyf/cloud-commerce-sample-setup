package com.mirakl.hybris.core.util.flexiblesearch.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mirakl.hybris.core.util.flexiblesearch.impl.Condition.Conjunction;

public class WhereClause extends AbstractQueryComponent {

  private List<Condition> conditions;

  public WhereClause() {
    conditions = new ArrayList<>();
  }

  public void addCondition(Condition condition) {
    if (conditions.isEmpty()) {
      // Force no conjunction for first condition
      condition.setConjunction(null);
    }
    conditions.add(condition);
  }

  public void addAndCondition(Condition condition) {
    condition.setConjunction(Conjunction.AND);
    addCondition(condition);
  }

  public void addOrCondition(Condition condition) {
    condition.setConjunction(Conjunction.OR);
    addCondition(condition);
  }

  public List<Condition> getConditions() {
    return conditions;
  }

  public Map<String, Object> getParams() {
    Map<String, Object> params = new HashMap<>();
    for (Condition condition : conditions) {
      params.put(condition.getParamName(), condition.getValue());
    }
    return params;
  }

  @Override
  protected StringBuilder append(StringBuilder str) {
    str.append(" WHERE");
    for (Condition condition : conditions) {
      condition.append(str);
    }

    return str;
  }

}
