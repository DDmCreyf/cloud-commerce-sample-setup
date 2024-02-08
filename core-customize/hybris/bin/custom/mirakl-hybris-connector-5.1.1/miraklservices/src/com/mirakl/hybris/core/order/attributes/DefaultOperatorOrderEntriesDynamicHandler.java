package com.mirakl.hybris.core.order.attributes;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

/**
 * Returns operator's {@link AbstractOrderEntryModel} list
 */
public class DefaultOperatorOrderEntriesDynamicHandler extends AbstractOrderEntryFilteringDynamicHandler {

  @Override
  protected boolean filter(AbstractOrderEntryModel orderEntry) {
    return orderEntry.getOfferId() == null;
  }
}
