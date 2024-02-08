package com.mirakl.hybris.advancedpricing.product.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.advancedpricing.product.utils.OfferPricingCriteriaBuilder;
import com.mirakl.hybris.core.user.strategies.MiraklCustomerGroupResolvingStrategy;
import com.mirakl.hybris.core.user.strategies.MiraklCustomerOrganizationResolvingStrategy;

public class CustomerOfferPricingCriteriaBuilderPopulator implements OfferPricingCriteriaBuilderPopulator {

  protected MiraklCustomerGroupResolvingStrategy customerGroupResolvingStrategy;
  protected MiraklCustomerOrganizationResolvingStrategy customerOrganizationResolvingStrategy;

  @Override
  public void populate(OfferPricingCriteriaBuilder builder) {
    builder.withCustomerGroupId(customerGroupResolvingStrategy.resolveCurrentUserCustomerGroupId()) //
        .withCustomerOrganizationId(customerOrganizationResolvingStrategy.resolveCurrentUserOrganizationId());
  }

  @Required
  public void setCustomerGroupResolvingStrategy(MiraklCustomerGroupResolvingStrategy customerGroupResolvingStrategy) {
    this.customerGroupResolvingStrategy = customerGroupResolvingStrategy;
  }

  @Required
  public void setCustomerOrganizationResolvingStrategy(
      MiraklCustomerOrganizationResolvingStrategy customerOrganizationResolvingStrategy) {
    this.customerOrganizationResolvingStrategy = customerOrganizationResolvingStrategy;
  }
}
