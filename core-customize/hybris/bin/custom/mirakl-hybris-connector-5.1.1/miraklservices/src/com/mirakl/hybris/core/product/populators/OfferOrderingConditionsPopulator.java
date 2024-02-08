package com.mirakl.hybris.core.product.populators;

import com.mirakl.hybris.beans.OfferOrderingConditions;
import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class OfferOrderingConditionsPopulator implements Populator<OfferModel, OfferOrderingConditions> {

  @Override
  public void populate(OfferModel source, OfferOrderingConditions target) throws ConversionException {
    target.setMaxOrderQuantity(source.getMaxOrderQuantity());
    target.setMinOrderQuantity(source.getMinOrderQuantity());
    target.setPackageQuantity(source.getPackageQuantity());
  }

}
