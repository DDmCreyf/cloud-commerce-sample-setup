package com.mirakl.hybris.core.product.strategies.impl;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.OfferPricingSelectionStrategy;

public class DefaultOfferPricingSelectionStrategy implements OfferPricingSelectionStrategy {
  protected OfferService offerService;

  @Override
  public OfferPricingData selectApplicableOfferPricing(OfferModel offer) {
    return selectApplicableOfferPricing(offerService.loadAllOfferPricings(offer));
  }

  @Override
  public OfferPricingData selectApplicableOfferPricing(List<OfferPricingData> offerPricings) {
    if (isEmpty(offerPricings)) {
      return null;
    }

    for (OfferPricingData offerPricing : offerPricings) {
      if (isBlank(offerPricing.getChannelCode())) {
        return offerPricing;
      }
    }
    return null;
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

}
