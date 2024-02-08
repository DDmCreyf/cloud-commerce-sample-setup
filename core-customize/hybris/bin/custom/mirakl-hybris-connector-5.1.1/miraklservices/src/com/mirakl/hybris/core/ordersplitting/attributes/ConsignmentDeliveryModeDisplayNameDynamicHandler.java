package com.mirakl.hybris.core.ordersplitting.attributes;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class ConsignmentDeliveryModeDisplayNameDynamicHandler extends AbstractDynamicAttributeHandler<String, ConsignmentModel> {

  @Override
  public String get(ConsignmentModel consignment) {
    if (consignment instanceof MarketplaceConsignmentModel
        && ((MarketplaceConsignmentModel) consignment).getShippingTypeLabel() != null) {
      return ((MarketplaceConsignmentModel) consignment).getShippingTypeLabel();
    }
    return consignment.getDeliveryMode().getName();
  }

}
