package com.mirakl.hybris.core.order.strategies.impl;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.strategies.MarketplaceConsignmentMessagesStrategy;

public class DefaultMarketplaceConsignmentMessagesStrategy implements MarketplaceConsignmentMessagesStrategy {
  @Override
  public boolean canWriteMessages(MarketplaceConsignmentModel consignment) {
    return true;
  }
}
