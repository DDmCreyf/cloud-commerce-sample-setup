package com.mirakl.hybris.promotions.converters.populators;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.order.strategies.MarketplaceDeliveryCostStrategy;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklCartRaoPopulator implements Populator<CartModel, CartRAO> {

  protected ShippingFeeService shippingFeeService;
  protected MarketplaceDeliveryCostStrategy marketplaceDeliveryCostStrategy;

  @Override
  public void populate(CartModel source, CartRAO target) throws ConversionException {
    target.setOperatorTotal(BigDecimal.valueOf(calculateOperatorAmount(source)));
    target.setMarketplaceDeliveryCost(BigDecimal.valueOf(marketplaceDeliveryCostStrategy.getMarketplaceDeliveryCost(source)));
  }

  protected double calculateOperatorAmount(CartModel cart) {
    // Non mixed cart: We can skip the calculation
    Double operatorAmount = cart.getTotalPrice();
    if (isEmpty(cart.getMarketplaceEntries())) {
      return operatorAmount;
    } else if (isEmpty(cart.getOperatorEntries())) {
      return 0.0;
    }

    // Mixed cart: We need to recalculate the operator amount
    for (AbstractOrderEntryModel marketplaceEntry : cart.getMarketplaceEntries()) {
      operatorAmount -= marketplaceEntry.getTotalPrice();
      operatorAmount -= marketplaceEntry.getLineShippingPrice();
    }
    return operatorAmount;
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

  @Required
  public void setMarketplaceDeliveryCostStrategy(MarketplaceDeliveryCostStrategy marketplaceDeliveryCostStrategy) {
    this.marketplaceDeliveryCostStrategy = marketplaceDeliveryCostStrategy;
  }
}
