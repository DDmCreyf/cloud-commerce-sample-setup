package com.mirakl.hybris.core.product.services;

import com.mirakl.client.mmp.domain.offer.MiraklOffer;
import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;

import java.util.List;
import java.util.Map;

/**
 * Realtimely accesses offer-related information from Mirakl APIs
 */
public interface MiraklRealtimeOfferService {

  /**
   * Retrieves the Offers existing on each of the products whose code is provided
   *
   * @param productCodes the list of product codes for which Offers will be retrieved
   * @return a Map whose keys is the code of a productCode, and the value the list of Offers attached to the given product
   */
  Map<String, List<MiraklOfferOnProduct>> getOffersForProductCodes(List<String> productCodes);

  /**
   * Retrieves the offer having the provided id
   * @param offerId the id of the offer
   * @return the Offer having the provided id
   */
  MiraklOffer getOffer(final String offerId);

}
