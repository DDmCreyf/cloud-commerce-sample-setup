package com.mirakl.hybris.core.fulfilment.strategies;

import de.hybris.platform.core.model.order.OrderModel;

public interface ProcessOperatorPaymentStrategy {

  /**
   * Processes the payment of the operator consignments within the order
   *
   * @param order The order to process
   * @return True or false, whether or not the payment was successful
   */
  boolean processPayment(OrderModel order);
}
