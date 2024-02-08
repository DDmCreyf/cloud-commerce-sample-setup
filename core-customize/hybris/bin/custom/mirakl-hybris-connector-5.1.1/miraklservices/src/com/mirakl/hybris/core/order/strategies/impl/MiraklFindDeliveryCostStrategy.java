package com.mirakl.hybris.core.order.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.order.services.MiraklOrderService;
import com.mirakl.hybris.core.order.strategies.MarketplaceDeliveryCostStrategy;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.strategies.calculation.FindDeliveryCostStrategy;
import de.hybris.platform.util.PriceValue;

/**
 * Strategy returning total delivery cost of {@link AbstractOrderModel} including Mirakl line shipping options retrieved from
 * {@link com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees}
 */
public class MiraklFindDeliveryCostStrategy implements FindDeliveryCostStrategy {

  protected MarketplaceDeliveryCostStrategy marketplaceDeliveryCostStrategy;
  protected FindDeliveryCostStrategy operatorFindDeliveryCostStrategy;

  @Override
  public PriceValue getDeliveryCost(AbstractOrderModel order) {
    PriceValue deliveryCost = operatorFindDeliveryCostStrategy.getDeliveryCost(order);
    return new PriceValue(deliveryCost.getCurrencyIso(), getTotalDeliveryCost(order, deliveryCost), deliveryCost.isNet());
  }

  protected double getTotalDeliveryCost(AbstractOrderModel order, PriceValue deliveryCost) {
    return deliveryCost.getValue() + marketplaceDeliveryCostStrategy.getMarketplaceDeliveryCost(order);
  }
  
  @Required
  public void setOperatorFindDeliveryCostStrategy(FindDeliveryCostStrategy operatorFindDeliveryCostStrategy) {
    this.operatorFindDeliveryCostStrategy = operatorFindDeliveryCostStrategy;
  }
  
  @Required
  public void setMarketplaceDeliveryCostStrategy(MarketplaceDeliveryCostStrategy marketplaceDeliveryCostStrategy) {
    this.marketplaceDeliveryCostStrategy = marketplaceDeliveryCostStrategy;
  }
}
