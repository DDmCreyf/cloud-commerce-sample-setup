package com.mirakl.hybris.advancedpricing.product.populators;

import org.apache.commons.lang3.tuple.Pair;

import com.mirakl.hybris.advancedpricing.product.utils.OfferPricingKey;
import com.mirakl.hybris.core.model.OfferPricingModel;

import de.hybris.platform.converters.Populator;

public class AsyncOfferPricingPopulator implements Populator<Pair<OfferPricingKey, String>, OfferPricingModel> {

  @Override
  public void populate(Pair<OfferPricingKey, String> keyAndPriceDetailsJSON, OfferPricingModel offerPricing) {
    offerPricing.setOfferId(keyAndPriceDetailsJSON.getLeft().getOfferId());
    offerPricing.setChannelCode(keyAndPriceDetailsJSON.getLeft().getChannel());
    offerPricing.setCustomerGroupId(keyAndPriceDetailsJSON.getLeft().getCustomerGroup());
    offerPricing.setCustomerOrganizationId(keyAndPriceDetailsJSON.getLeft().getCustomerOrg());
    offerPricing.setStartDate(keyAndPriceDetailsJSON.getLeft().getStartDate());
    offerPricing.setEndDate(keyAndPriceDetailsJSON.getLeft().getEndDate());
    offerPricing.setPriceDetailsJSON(keyAndPriceDetailsJSON.getRight());
  }

}
