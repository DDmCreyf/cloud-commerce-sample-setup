package com.mirakl.hybris.occ.order.strategies.impl;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.occ.order.strategies.MiraklStockLevelStatusStrategy;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;

public class DefaultMiraklStockLevelStatusStrategy implements MiraklStockLevelStatusStrategy {

  @Override
  public StockLevelStatus getStockLevelStatus(OfferModel offer) {
    return offer.getQuantity() > 0 ? StockLevelStatus.INSTOCK : StockLevelStatus.OUTOFSTOCK;
  }

}
