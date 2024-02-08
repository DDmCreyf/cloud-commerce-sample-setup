package com.mirakl.hybris.core.product.populators;

import com.mirakl.client.mmp.front.request.offer.MiraklGetOfferRequest;
import com.mirakl.hybris.core.user.strategies.MiraklCustomerOrganizationResolvingStrategy;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

public class MiraklCustomerOrganizationGetOfferRequestPopulator implements MiraklGetOfferRequestPopulator {

  protected MiraklCustomerOrganizationResolvingStrategy customerOrganizationResolvingStrategy;

  @Override
  public void populate(MiraklGetOfferRequest getOfferRequest) {
    Optional.ofNullable(customerOrganizationResolvingStrategy.resolveCurrentUserOrganizationId()).ifPresent(getOfferRequest::setPricingCustomerOrganizationId);
  }

  @Required
  public void setCustomerOrganizationResolvingStrategy(final MiraklCustomerOrganizationResolvingStrategy customerOrganizationResolvingStrategy) {
    this.customerOrganizationResolvingStrategy = customerOrganizationResolvingStrategy;
  }

}
