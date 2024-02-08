package com.mirakl.hybris.core.product.factories.impl;

import com.mirakl.client.mmp.front.request.offer.MiraklGetOfferRequest;
import com.mirakl.hybris.core.product.factories.MiraklGetOfferRequestFactory;
import com.mirakl.hybris.core.product.populators.MiraklGetOfferRequestPopulator;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * This factory relies on populators to fill in the request fields.
 */
public class DefaultMiraklGetOfferRequestFactory implements MiraklGetOfferRequestFactory {
  protected List<MiraklGetOfferRequestPopulator> searchRequestPopulators;

  @Required
  public void setSearchRequestPopulators(final List<MiraklGetOfferRequestPopulator> searchRequestPopulators) {
    this.searchRequestPopulators = searchRequestPopulators;
  }

  @Override
  public MiraklGetOfferRequest buildGetOfferRequest(String offerId) {
    MiraklGetOfferRequest offersOnProductRequest = new MiraklGetOfferRequest(offerId);
    ListUtils.emptyIfNull(searchRequestPopulators).forEach(populator -> populator.populate(offersOnProductRequest));
    return offersOnProductRequest;
  }
}
