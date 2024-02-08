package com.mirakl.hybris.core.order.strategies.impl;

import com.mirakl.hybris.core.order.strategies.MarketplaceDeliveryCostStrategy;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public class DefaultMarketplaceDeliveryCostStrategy implements MarketplaceDeliveryCostStrategy {

  @Override
  public double getMarketplaceDeliveryCost(AbstractOrderModel order) {
    double marketplaceDeliveryCost = 0.0;
    for (AbstractOrderEntryModel entryModel : order.getEntries()) {
      marketplaceDeliveryCost += entryModel.getLineShippingPrice();
    }
    return marketplaceDeliveryCost;
  }

}
