package com.mirakl.hybris.core.product.strategies;

import java.util.List;

import com.mirakl.hybris.beans.ComparableOfferData;


public interface ComparableOfferDataSortingStrategy {

  /**
   * Sorts {@link ComparableOfferData}s
   * 
   * @param wrappedOffers the offers to sort
   * @return The sorted result
   */
  <T> List<T> sort(List<ComparableOfferData<T>> wrappedOffers);
}
