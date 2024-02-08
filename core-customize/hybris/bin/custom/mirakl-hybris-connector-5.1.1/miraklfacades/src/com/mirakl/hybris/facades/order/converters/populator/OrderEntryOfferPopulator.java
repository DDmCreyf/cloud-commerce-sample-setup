package com.mirakl.hybris.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

public class OrderEntryOfferPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData> {

  @Override
  public void populate(AbstractOrderEntryModel source, OrderEntryData target) {
    target.setOfferId(source.getOfferId());
    target.setShopId(source.getShopId());
    target.setShopName(source.getShopName());
  }
}
