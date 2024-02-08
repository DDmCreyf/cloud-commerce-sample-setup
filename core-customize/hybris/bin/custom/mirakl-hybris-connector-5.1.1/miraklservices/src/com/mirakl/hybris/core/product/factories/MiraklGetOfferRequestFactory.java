package com.mirakl.hybris.core.product.factories;

import com.mirakl.client.mmp.front.request.offer.MiraklGetOfferRequest;

public interface MiraklGetOfferRequestFactory {

  /**
   * Builds a request object containing relevant fields for calling Mirakl OF22 API to fetch an offer
   *
   * @param offerId the offer's id
   * @return a request with all relevant search criteria filled in
   */
  MiraklGetOfferRequest buildGetOfferRequest(final String offerId);
}
