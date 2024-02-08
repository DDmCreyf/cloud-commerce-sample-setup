package com.mirakl.hybris.core.user.strategies.impl;

import com.mirakl.hybris.core.user.strategies.MiraklCustomerGroupResolvingStrategy;

public class DefaultMiraklCustomerGroupResolvingStrategy implements MiraklCustomerGroupResolvingStrategy {

  @Override
  public String resolveCurrentUserCustomerGroupId() {
    // Default implementation does not do the resolution (provide your own strategy)
    return null;
  }

}
