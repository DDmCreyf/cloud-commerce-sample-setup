package com.mirakl.hybris.core.order.attributes;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

/**
 * Returns only marketplace entries
 */
public class DefaultMarketplaceOrderEntriesDynamicHandler extends AbstractOrderEntryFilteringDynamicHandler {

  @Override
  protected boolean filter(AbstractOrderEntryModel orderEntry) {
    return orderEntry.getOfferId() != null;
  }
}
