package com.mirakl.hybris.core.util.flexiblesearch.impl;

public abstract class AbstractQueryComponent {

  protected abstract StringBuilder append(StringBuilder stringBuilder);

  @Override
  public String toString() {
    return append(new StringBuilder()).toString();
  }
}
