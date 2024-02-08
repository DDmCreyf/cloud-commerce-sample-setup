package com.mirakl.hybris.core.user.strategies.impl;

import com.mirakl.hybris.beans.OrganizationData;
import com.mirakl.hybris.core.user.strategies.MiraklCustomerOrganizationResolvingStrategy;

import de.hybris.platform.core.model.user.UserModel;

public class DefaultMiraklCustomerOrganizationResolvingStrategy implements MiraklCustomerOrganizationResolvingStrategy {

  @Override
  public OrganizationData resolveUserOrganization(UserModel userModel) {
    // Default implementation does not do the customer organization resolution. Provide your own strategy.
    return null;
  }

  @Override
  public String resolveCurrentUserOrganizationId() {
    // Default implementation does not do the customer organization resolution. Provide your own strategy.
    return null;
  }

}
