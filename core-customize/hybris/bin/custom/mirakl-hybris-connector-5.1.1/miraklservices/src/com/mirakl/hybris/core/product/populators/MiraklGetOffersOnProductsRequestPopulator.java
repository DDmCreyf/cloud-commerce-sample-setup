package com.mirakl.hybris.core.product.populators;

import com.mirakl.client.mmp.request.product.MiraklGetOffersOnProductsRequest;

/**
 * Defines the mechanism to populate a MiraklGetOffersOnProductsRequest for the P11 request.
 */
public interface MiraklGetOffersOnProductsRequestPopulator {

  /**
   * Populates the MiraklGetOffersOnProductsRequest object for the P11 request.
   *
   * @param getOffersOnProductsRequest the request object that needs to be populated.
   */
  void populate(MiraklGetOffersOnProductsRequest getOffersOnProductsRequest);

}
