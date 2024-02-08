package com.mirakl.hybris.core.order.strategies.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.strategies.DeliveryModeLookupStrategy;
import de.hybris.platform.commerceservices.strategies.impl.DefaultDeliveryModeLookupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

public class MiraklDeliveryModeLookupStrategy extends DefaultDeliveryModeLookupStrategy implements DeliveryModeLookupStrategy {

  protected DeliveryService deliveryService;
  protected String defaultFreeDeliveryModeCode;

  @Override
  public List<DeliveryModeModel> getSelectableDeliveryModesForOrder(AbstractOrderModel order) {
    if (!order.isMarketplaceOrder()) {
      return super.getSelectableDeliveryModesForOrder(order);
    }
    DeliveryModeModel defaultFreeDeliveryMode = deliveryService.getDeliveryModeForCode(defaultFreeDeliveryModeCode);
    if (defaultFreeDeliveryMode == null) {
      throw new IllegalStateException(
          String.format("No default free delivery mode [%s] found for the marketplace cart", defaultFreeDeliveryModeCode));
    }
    return Collections.singletonList(defaultFreeDeliveryMode);
  }

  @Required
  public void setDeliveryService(DeliveryService deliveryService) {
    this.deliveryService = deliveryService;
  }

  @Required
  public void setDefaultFreeDeliveryModeCode(String defaultFreeDeliveryModeCode) {
    this.defaultFreeDeliveryModeCode = defaultFreeDeliveryModeCode;
  }
}
