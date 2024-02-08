package com.mirakl.hybris.core.util.flexiblesearch.impl;

public class Item extends AbstractQueryComponent {

  private String itemTypeCode;
  private String itemAlias;

  private Item(String itemTypeCode, String itemAlias) {
    this.itemTypeCode = itemTypeCode;
    this.itemAlias = itemAlias;
  }

  public static Item item(String itemTypeCode, String itemAlias) {
    return new Item(itemTypeCode, itemAlias);
  }

  public String getItemTypeCode() {
    return itemTypeCode;
  }

  public String getItemAlias() {
    return itemAlias;
  }

  @Override
  protected StringBuilder append(StringBuilder str) {
    return str.append(itemTypeCode).append(" AS ").append(itemAlias);
  }

}
