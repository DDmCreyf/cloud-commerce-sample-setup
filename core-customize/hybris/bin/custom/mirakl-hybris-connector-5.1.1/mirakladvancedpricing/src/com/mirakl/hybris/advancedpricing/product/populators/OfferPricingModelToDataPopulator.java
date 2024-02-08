package com.mirakl.hybris.advancedpricing.product.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferPricingModel;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class OfferPricingModelToDataPopulator implements Populator<OfferPricingModel, OfferPricingData> {

  protected MiraklPriceService miraklPriceService;
  protected JsonMarshallingService jsonMarshallingService;

  @Override
  public void populate(OfferPricingModel source, OfferPricingData target) throws ConversionException {
    target.setChannelCode(source.getChannelCode());
    target.setCustomerGroupId(source.getCustomerGroupId());
    target.setCustomerOrganizationId(source.getCustomerOrganizationId());
    target.setStartDate(source.getStartDate());
    target.setEndDate(source.getEndDate());
    target.setOfferId(source.getOfferId());

    OfferPricingData pricingData = jsonMarshallingService.fromJson(source.getPriceDetailsJSON(), OfferPricingData.class);
    target.setPrice(miraklPriceService.getPrice(pricingData));
    target.setUnitDiscountPrice(pricingData.getUnitDiscountPrice());
    target.setUnitOriginPrice(pricingData.getUnitOriginPrice());
    target.setDiscountEndDate(pricingData.getDiscountEndDate());
    target.setDiscountStartDate(pricingData.getDiscountStartDate());
    target.setVolumePrices(pricingData.getVolumePrices());
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }
}
