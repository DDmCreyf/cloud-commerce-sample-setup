package com.mirakl.hybris.core.ordersplitting.attributes;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class ConsignmentShopDisplayNameDynamicHandler extends AbstractDynamicAttributeHandler<String, ConsignmentModel> {

  @Override
  public String get(ConsignmentModel consignment) {
    return consignment instanceof MarketplaceConsignmentModel ? ((MarketplaceConsignmentModel) consignment).getShopName() : "";
  }

}
