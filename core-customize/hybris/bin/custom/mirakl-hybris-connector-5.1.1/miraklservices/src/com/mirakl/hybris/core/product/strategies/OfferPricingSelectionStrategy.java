package com.mirakl.hybris.core.product.strategies;

import java.util.List;

import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;

public interface OfferPricingSelectionStrategy {

  /**
   * Selects the applicable offer pricing among those stored within a given offer
   * 
   * @param offer
   * @return applicable offer pricing
   */
  OfferPricingData selectApplicableOfferPricing(OfferModel offer);

  /**
   * Selects the applicable offer pricing
   * 
   * @param offerPricings
   * @return applicable offer pricing
   */
  OfferPricingData selectApplicableOfferPricing(List<OfferPricingData> offerPricings);

}
