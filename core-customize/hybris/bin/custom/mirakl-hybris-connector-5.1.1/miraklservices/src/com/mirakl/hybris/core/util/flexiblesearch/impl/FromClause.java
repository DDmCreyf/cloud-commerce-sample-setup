package com.mirakl.hybris.core.util.flexiblesearch.impl;

import java.util.ArrayList;
import java.util.List;

public class FromClause extends AbstractQueryComponent {

  private Item fromItem;
  private List<Join> joinedEntities;

  public FromClause() {
    joinedEntities = new ArrayList<>();
  }

  public void setFromItem(Item fromItem) {
    this.fromItem = fromItem;
  }

  public void addJoinEntity(Join join) {
    joinedEntities.add(join);
  }

  public Item getFromItem() {
    return fromItem;
  }

  public List<Join> getJoinedEntities() {
    return joinedEntities;
  }

  @Override
  public StringBuilder append(StringBuilder str) {
    str.append(" FROM {");
    fromItem.append(str);
    for (Join joinEntity : joinedEntities) {
      joinEntity.append(str);
    }
    str.append("}");

    return str;
  }

}
