package com.mirakl.hybris.facades.product.populators;

import com.mirakl.hybris.beans.ComparableOfferData;
import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.enums.OfferState;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Provides comparison data for OfferData objects
 */
public class MiraklOfferDataToComparableOfferDataPopulator implements Populator<OfferData, ComparableOfferData<OfferData>> {

  @Override
  public void populate(OfferData source, ComparableOfferData<OfferData> target) throws ConversionException {
    target.setOffer(source);
    target.setState(OfferState.valueOf(source.getStateCode()));

    PriceData price = source.getTotalPrice();
    if (price != null) {
      target.setTotalPrice(price.getValue());
    }
  }
}
