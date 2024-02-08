package com.mirakl.hybris.core.order.strategies.impl;

import com.mirakl.hybris.core.order.strategies.MiraklCustomerIdDefinitionStrategy;

import de.hybris.platform.core.model.user.CustomerModel;

public class DefaultMiraklCustomerIdDefinitionStrategy implements MiraklCustomerIdDefinitionStrategy {

  @Override
  public String getMiraklCustomerId(CustomerModel customer) {
    return customer.getContactEmail();
  }

}
