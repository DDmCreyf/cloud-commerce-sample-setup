package com.mirakl.hybris.core.user.strategies;

import javax.annotation.Nullable;

public interface MiraklCustomerGroupResolvingStrategy {

  /**
   * Resolves the Mirakl customer group id of the current user
   * 
   * @return the Mirakl customer group id or null
   */
  @Nullable
  String resolveCurrentUserCustomerGroupId();

}
