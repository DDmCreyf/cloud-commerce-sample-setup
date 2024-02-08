package com.mirakl.hybris.advancedpricing.product.attributes;

import static java.lang.String.format;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.OfferPricingModel;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class DefaultOfferPricingDynamicPriceDetailJSONPretty extends AbstractDynamicAttributeHandler<String, OfferPricingModel> {

  private static Logger LOG = Logger.getLogger(DefaultOfferPricingDynamicPriceDetailJSONPretty.class);

  protected JsonMarshallingService jsonMarshallingService;

  @Override
  public String get(OfferPricingModel pricing) {
    try {
      return jsonMarshallingService.prettify(pricing.getPriceDetailsJSON());
    } catch (Exception e) {
      if (LOG.isTraceEnabled()) {
        LOG.trace(format("Unable to prettify price details: %s", pricing.getPriceDetailsJSON()));
      }
      return pricing.getPriceDetailsJSON();
    }
  }

  @Override
  public void set(OfferPricingModel offerPricing, String priceDetailsJson) {
    offerPricing.setPriceDetailsJSON(priceDetailsJson);
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }
}
