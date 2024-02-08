package com.mirakl.hybris.b2badvancedpricing.user.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.user.strategies.MiraklCustomerGroupResolvingStrategy;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.user.UserService;

public class SampleMiraklCustomerGroupResolvingStrategy implements MiraklCustomerGroupResolvingStrategy {

  protected UserService userService;
  protected B2BCommerceUnitService b2bCommerceUnitService;

  @Override
  public String resolveCurrentUserCustomerGroupId() {
    B2BUnitModel rootUnit = null;
    if (userService.getCurrentUser() instanceof B2BCustomerModel) {
      rootUnit = b2bCommerceUnitService.getRootUnit();
    }
    return rootUnit != null ? rootUnit.getUid() : null;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setB2bCommerceUnitService(B2BCommerceUnitService b2bCommerceUnitService) {
    this.b2bCommerceUnitService = b2bCommerceUnitService;
  }

}
