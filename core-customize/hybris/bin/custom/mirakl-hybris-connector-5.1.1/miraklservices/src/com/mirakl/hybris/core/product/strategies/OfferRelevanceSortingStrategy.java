package com.mirakl.hybris.core.product.strategies;

import java.util.List;

import com.mirakl.hybris.core.model.OfferModel;


public interface OfferRelevanceSortingStrategy {

  /**
   * Sorts the offers
   * 
   * @param offerList
   * @return The offers sorted
   */
  List<OfferModel> sort(List<OfferModel> offerList);
}
