package com.mirakl.hybris.core.product.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.offer.price.MiraklVolumePrice;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.product.services.MiraklPriceService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklVolumePriceToDataPopulator
    implements Populator<MiraklVolumePrice, OfferVolumePricingData> {

  protected MiraklPriceService miraklPriceService;

  @Override
  public void populate(MiraklVolumePrice source, OfferVolumePricingData target)
      throws ConversionException {
    target.setQuantityThreshold(source.getQuantityThreshold());
    target.setUnitDiscountPrice(source.getUnitDiscountPrice());
    target.setUnitOriginPrice(source.getUnitOriginPrice());
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }

}
