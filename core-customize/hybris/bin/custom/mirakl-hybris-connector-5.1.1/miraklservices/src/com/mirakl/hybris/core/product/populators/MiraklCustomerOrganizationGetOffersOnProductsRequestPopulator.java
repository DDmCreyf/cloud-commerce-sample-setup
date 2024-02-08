package com.mirakl.hybris.core.product.populators;

import com.mirakl.client.mmp.request.product.MiraklGetOffersOnProductsRequest;
import com.mirakl.hybris.core.user.strategies.MiraklCustomerOrganizationResolvingStrategy;
import org.springframework.beans.factory.annotation.Required;

public class MiraklCustomerOrganizationGetOffersOnProductsRequestPopulator implements MiraklGetOffersOnProductsRequestPopulator {

  protected MiraklCustomerOrganizationResolvingStrategy customerOrganizationResolvingStrategy;

  @Override
  public void populate(final MiraklGetOffersOnProductsRequest getOffersOnProductsRequest) {
    getOffersOnProductsRequest.setPricingCustomerOrganizationId(customerOrganizationResolvingStrategy.resolveCurrentUserOrganizationId());
    getOffersOnProductsRequest.setAllOffers(false);
  }


  @Required
  public void setCustomerOrganizationResolvingStrategy(final MiraklCustomerOrganizationResolvingStrategy customerOrganizationResolvingStrategy) {
    this.customerOrganizationResolvingStrategy = customerOrganizationResolvingStrategy;
  }
}
