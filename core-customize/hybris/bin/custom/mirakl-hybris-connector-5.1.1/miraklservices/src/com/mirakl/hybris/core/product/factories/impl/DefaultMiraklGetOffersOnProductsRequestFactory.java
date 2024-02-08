package com.mirakl.hybris.core.product.factories.impl;

import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.request.product.MiraklGetOffersOnProductsRequest;
import com.mirakl.hybris.core.product.factories.MiraklGetOffersOnProductsRequestFactory;
import com.mirakl.hybris.core.product.populators.MiraklGetOffersOnProductsRequestPopulator;

/**
 * This factory relies on populators to fill in the request fields.
 */
public class DefaultMiraklGetOffersOnProductsRequestFactory implements MiraklGetOffersOnProductsRequestFactory {
  protected List<MiraklGetOffersOnProductsRequestPopulator> searchRequestPopulators;


  @Override
  public MiraklGetOffersOnProductsRequest buildProductsOfferSearchRequest(final List<String> productCodes) {
    MiraklGetOffersOnProductsRequest offersOnProductRequest = new MiraklGetOffersOnProductsRequest(productCodes);
    ListUtils.emptyIfNull(searchRequestPopulators).forEach(populator -> populator.populate(offersOnProductRequest));
    return offersOnProductRequest;

  }

  @Required
  public void setSearchRequestPopulators(final List<MiraklGetOffersOnProductsRequestPopulator> searchRequestPopulators) {
    this.searchRequestPopulators = searchRequestPopulators;
  }

}
