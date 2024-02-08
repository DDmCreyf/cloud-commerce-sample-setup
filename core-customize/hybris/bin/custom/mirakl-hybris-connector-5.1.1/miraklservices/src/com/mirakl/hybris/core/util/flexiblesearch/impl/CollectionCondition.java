package com.mirakl.hybris.core.util.flexiblesearch.impl;

import java.util.Collection;

/**
 * Represents a collection condition in a flexible search query.
 */
public class CollectionCondition extends Condition {

  CollectionCondition(Field field, String operator, Object value) {
    super(field, operator, value);
  }

  public static Condition fieldIn(Field field, Collection<String> collection) {
    return new CollectionCondition(field, "IN", collection);
  }

  @Override
  protected StringBuilder append(StringBuilder str) {
    if (conjunction != null) {
      str.append(" ").append(conjunction.name());
    }

    str.append(" {")
        .append(field)
        .append("} ")
        .append(operator)
        .append(" (?")
        .append(getParamName())
        .append(")");

    return str;
  }
}
