package com.mirakl.hybris.core.order.strategies;

import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface MarketplaceDeliveryCostStrategy {
  
  /**
   * Gets the marketplace delivery costs for the given order
   *
   * @param order the order
   * @return the total marketplace delivery cost
   */
  double getMarketplaceDeliveryCost(AbstractOrderModel order);

}
