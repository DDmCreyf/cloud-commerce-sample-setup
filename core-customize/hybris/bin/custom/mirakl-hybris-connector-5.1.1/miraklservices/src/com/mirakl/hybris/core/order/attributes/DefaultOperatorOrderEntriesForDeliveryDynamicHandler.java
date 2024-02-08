package com.mirakl.hybris.core.order.attributes;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

/**
 * Returns operator's {@link AbstractOrderEntryModel} list for delivery
 */
public class DefaultOperatorOrderEntriesForDeliveryDynamicHandler extends DefaultOperatorOrderEntriesDynamicHandler {

  @Override
  protected boolean filter(AbstractOrderEntryModel orderEntry) {
    return super.filter(orderEntry) && orderEntry.getDeliveryPointOfService() == null;
  }
}
