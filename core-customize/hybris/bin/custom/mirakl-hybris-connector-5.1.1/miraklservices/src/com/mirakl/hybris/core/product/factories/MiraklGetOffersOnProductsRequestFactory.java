package com.mirakl.hybris.core.product.factories;

import com.mirakl.client.mmp.request.product.MiraklGetOffersOnProductsRequest;

import java.util.List;

public interface MiraklGetOffersOnProductsRequestFactory {

  /**
   * Builds a request object containing relevant fields for calling Mirakl P11 API to fetch products offers
   *
   * @param productCodes the list of product code the request will target
   * @return a request with all relevant search criteria filled in
   */
  MiraklGetOffersOnProductsRequest buildProductsOfferSearchRequest(List<String> productCodes);
}
