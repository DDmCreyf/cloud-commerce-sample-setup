package com.mirakl.hybris.core.ordersplitting.attributes;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class ConsignmentStatusDisplayNameDynamicHandler extends AbstractDynamicAttributeHandler<String, ConsignmentModel> {

  @Override
  public String get(ConsignmentModel consignment) {
    if (consignment instanceof MarketplaceConsignmentModel
        && ((MarketplaceConsignmentModel) consignment).getMiraklOrderStatus() != null) {
      return ((MarketplaceConsignmentModel) consignment).getMiraklOrderStatus().getCode();
    }
    return consignment.getStatus().getCode();
  }

}
