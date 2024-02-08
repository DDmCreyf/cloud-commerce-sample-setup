package com.mirakl.hybris.advancedpricing.product.strategies;

import java.util.List;

import com.mirakl.hybris.beans.OfferPricingData;

public interface OfferPricingSortingStrategy {

  /**
   * Sorts prices by order of applicability
   * 
   * @param offerPricings the pricings to sort
   * @return a sorted list of pricings
   */
  List<OfferPricingData> sort(List<OfferPricingData> offerPricings);


}
