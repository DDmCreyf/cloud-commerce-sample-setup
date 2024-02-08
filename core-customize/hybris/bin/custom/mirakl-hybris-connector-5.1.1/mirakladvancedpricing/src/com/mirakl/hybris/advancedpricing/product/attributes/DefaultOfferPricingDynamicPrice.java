package com.mirakl.hybris.advancedpricing.product.attributes;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferPricingModel;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class DefaultOfferPricingDynamicPrice extends AbstractDynamicAttributeHandler<BigDecimal, OfferPricingModel> {

  protected JsonMarshallingService jsonMarshallingService;
  protected MiraklPriceService miraklPriceService;

  @Override
  public BigDecimal get(OfferPricingModel pricing) {
    OfferPricingData pricingData = jsonMarshallingService.fromJson(pricing.getPriceDetailsJSON(), OfferPricingData.class);
    return miraklPriceService.getPrice(pricingData);
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }
}
