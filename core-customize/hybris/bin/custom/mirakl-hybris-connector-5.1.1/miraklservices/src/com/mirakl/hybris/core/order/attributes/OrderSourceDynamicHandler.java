package com.mirakl.hybris.core.order.attributes;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import com.mirakl.hybris.core.enums.OrderSource;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class OrderSourceDynamicHandler extends AbstractDynamicAttributeHandler<OrderSource, AbstractOrderModel> {

  @Override
  public OrderSource get(AbstractOrderModel order) {
    if (order.isMarketplaceOrder()) {
      return OrderSource.MARKETPLACE;
    }
    if (isNotEmpty(order.getMarketplaceEntries())) {
      return OrderSource.MIXED;
    }
    return OrderSource.OPERATOR;
  }

}
