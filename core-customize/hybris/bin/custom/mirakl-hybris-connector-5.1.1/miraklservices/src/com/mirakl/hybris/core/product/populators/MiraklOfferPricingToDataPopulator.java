package com.mirakl.hybris.core.product.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.client.mmp.domain.offer.price.MiraklVolumePrice;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.product.services.MiraklPriceService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklOfferPricingToDataPopulator implements Populator<MiraklOfferPricing, OfferPricingData> {

  protected MiraklPriceService miraklPriceService;
  protected Converter<MiraklVolumePrice, OfferVolumePricingData> offerVolumePricingDataConverter;

  @Override
  public void populate(MiraklOfferPricing source, OfferPricingData target) throws ConversionException {
    target.setChannelCode(source.getChannelCode());
    target.setDiscountStartDate(source.getDiscountStartDate());
    target.setDiscountEndDate(source.getDiscountEndDate());
    target.setUnitDiscountPrice(source.getUnitDiscountPrice());
    target.setUnitOriginPrice(source.getUnitOriginPrice());
    if (source.getVolumePrices() != null) {
      target.setVolumePrices(offerVolumePricingDataConverter.convertAll(source.getVolumePrices()));
    }
    miraklPriceService.computeAndPopulateApplicablePrice(target);
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }

  @Required
  public void setOfferVolumePricingDataConverter(
      Converter<MiraklVolumePrice, OfferVolumePricingData> offerVolumePricingDataConverter) {
    this.offerVolumePricingDataConverter = offerVolumePricingDataConverter;
  }

}
