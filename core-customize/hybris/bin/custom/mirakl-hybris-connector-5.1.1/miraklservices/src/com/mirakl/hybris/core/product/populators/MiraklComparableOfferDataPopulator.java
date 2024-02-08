package com.mirakl.hybris.core.product.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ComparableOfferData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.MiraklPriceService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklComparableOfferDataPopulator implements Populator<OfferModel, ComparableOfferData<OfferModel>> {

  protected MiraklPriceService miraklPriceService;

  @Override
  public void populate(OfferModel source, ComparableOfferData<OfferModel> target) throws ConversionException {
    target.setOffer(source);
    target.setState(source.getState());
    OfferPricingData offerPricing = miraklPriceService.getOfferPricing(source);
    if (offerPricing != null) {
      target.setTotalPrice(miraklPriceService.getOfferTotalPrice(source, offerPricing));
    }
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }

}
