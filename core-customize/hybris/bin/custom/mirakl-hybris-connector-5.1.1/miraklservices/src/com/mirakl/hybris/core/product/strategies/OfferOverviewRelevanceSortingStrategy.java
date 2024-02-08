package com.mirakl.hybris.core.product.strategies;

import java.util.List;

import com.mirakl.hybris.beans.OfferOverviewData;


public interface OfferOverviewRelevanceSortingStrategy {

  /**
   * Sorts offers overviews
   * 
   * @param offers offers to sort
   * @return The sorted offers
   */
  List<OfferOverviewData> sort(List<OfferOverviewData> offers);
}
