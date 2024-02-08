package com.mirakl.hybris.occ.order.strategies;

import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;

@FunctionalInterface
public interface MiraklStockLevelStatusStrategy {

  StockLevelStatus getStockLevelStatus(OfferModel offerForCode);
}
