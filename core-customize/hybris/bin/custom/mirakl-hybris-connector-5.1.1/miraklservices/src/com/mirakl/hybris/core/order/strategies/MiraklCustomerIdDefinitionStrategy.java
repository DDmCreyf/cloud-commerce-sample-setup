package com.mirakl.hybris.core.order.strategies;

import de.hybris.platform.core.model.user.CustomerModel;

public interface MiraklCustomerIdDefinitionStrategy {

  /**
   * Defines the customer ID that is sent to Mirakl when an order is placed
   * 
   * @param customer the {@link CustomerModel} for which the ID is determined
   * @return the customer ID to communicate to Mirakl
   */
  String getMiraklCustomerId(CustomerModel customer);
}
