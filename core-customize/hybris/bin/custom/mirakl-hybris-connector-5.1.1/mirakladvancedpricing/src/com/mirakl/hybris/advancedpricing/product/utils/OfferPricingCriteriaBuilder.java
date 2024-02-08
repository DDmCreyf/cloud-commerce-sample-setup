package com.mirakl.hybris.advancedpricing.product.utils;

import java.util.Date;

import com.mirakl.hybris.beans.OfferPricingCriteria;

public class OfferPricingCriteriaBuilder {

  private OfferPricingCriteria criteria;

  private OfferPricingCriteriaBuilder(String offerId) {
    criteria = new OfferPricingCriteria();
    criteria.setOfferId(offerId);
  }

  public static OfferPricingCriteriaBuilder offerPricingCriteria(String offerId) {
    return new OfferPricingCriteriaBuilder(offerId);
  }

  public OfferPricingCriteriaBuilder withOfferId(String offerId) {
    criteria.setOfferId(offerId);
    return this;
  }

  public OfferPricingCriteriaBuilder withChannelCode(String channelCode) {
    criteria.setChannelCode(channelCode);
    return this;
  }

  public OfferPricingCriteriaBuilder withCustomerOrganizationId(String customerOrganizationId) {
    criteria.setCustomerOrganizationId(customerOrganizationId);
    return this;
  }

  public OfferPricingCriteriaBuilder withCustomerGroupId(String customerGroupId) {
    criteria.setCustomerGroupId(customerGroupId);
    return this;
  }

  public OfferPricingCriteriaBuilder withDate(Date startDate) {
    criteria.setDate(startDate);
    return this;
  }

  public OfferPricingCriteria build() {
    if (criteria.getDate() == null) {
      criteria.setDate(new Date());
    }
    return criteria;
  }


}
