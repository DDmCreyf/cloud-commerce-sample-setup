package com.mirakl.hybris.core.product.populators;

import com.mirakl.client.mmp.front.request.offer.MiraklGetOfferRequest;

public interface MiraklGetOfferRequestPopulator {

  /**
   * Populates a Mirakl OF22 request
   *
   * @param getOfferRequest the OF22 request object to populate
   */
  void populate(MiraklGetOfferRequest getOfferRequest);

}
