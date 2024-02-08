package com.mirakl.hybris.core.order.services;

import de.hybris.platform.core.model.order.OrderModel;

public interface MiraklCalculationService {

  /**
   * Calculates the total amount for the operator part of the order
   *
   * @param orderModel
   * @return the total amount for the operator part of the order
   */
  double calculateOperatorAmount(OrderModel orderModel);

  /**
   * Calculates the already captured amount
   *
   * @param orderModel
   * @return the amount already captured
   */
  double calculateAlreadyCapturedAmount(OrderModel orderModel);

}
