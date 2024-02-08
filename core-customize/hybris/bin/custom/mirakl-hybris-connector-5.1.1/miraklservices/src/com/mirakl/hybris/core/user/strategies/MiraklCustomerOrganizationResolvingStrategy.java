package com.mirakl.hybris.core.user.strategies;

import javax.annotation.Nullable;

import com.mirakl.hybris.beans.OrganizationData;

import de.hybris.platform.core.model.user.UserModel;

public interface MiraklCustomerOrganizationResolvingStrategy {

  /**
   * Resolves the Mirakl customer's organization
   * 
   * @param userModel the customer model
   * @return the Mirakl customer organization data or null
   */
  @Nullable
  OrganizationData resolveUserOrganization(UserModel userModel);

  /**
   * Resolves the Mirakl customer organization id of the current user
   * 
   * @return the Mirakl customer organization id or null
   */
  @Nullable
  String resolveCurrentUserOrganizationId();

}
