package com.mirakl.hybris.core.product.strategies;

import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;
import com.mirakl.client.mmp.domain.product.MiraklProductWithOffers;

import java.util.List;

/**
 * Strategy for filtering out offers retrieved from real-time calls to Mirakl Offer APIs
 */
public interface MiraklRealtimeOffersFilteringStrategy {

  /**
   * Filters Offers linked to a Product, as provided by real-time calls to Mirakl P11 API
   * 
   * @param productWithOffers the MiraklProductWithOffers object listing a product and its offers
   * @return the filtered list of Offers
   */
  List<MiraklOfferOnProduct> getFilteredOffers(final MiraklProductWithOffers productWithOffers);
 
  /**
   * Filters the list of productCodes that will be used as input of the P11 call.
   * 
   * @param productCodes the list of productCodes intended to be sent for retrieving Offers
   * @return the actual list of productCodes that will be sent as input of the P11 call
   */
  List<String> getFilteredProductCodes(final List<String> productCodes);
}
