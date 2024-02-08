package com.mirakl.hybris.core.order.populators;

import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklAbstractOrderEntryModelPopulator implements Populator<OfferModel, AbstractOrderEntryModel> {
  @Override
  public void populate(OfferModel offerModel, AbstractOrderEntryModel abstractOrderEntryModel) throws ConversionException {
    abstractOrderEntryModel.setOfferId(offerModel.getId());
    abstractOrderEntryModel.setShopId(offerModel.getShop().getId());
    abstractOrderEntryModel.setShopName(offerModel.getShop().getName());
  }
}
