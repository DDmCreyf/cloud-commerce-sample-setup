package com.mirakl.hybris.advancedpricing.order.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.request.shipping.MiraklGetShippingRatesRequest;
import com.mirakl.hybris.core.order.factories.MiraklGetShippingRatesRequestPopulator;
import com.mirakl.hybris.core.user.strategies.MiraklCustomerOrganizationResolvingStrategy;

import de.hybris.platform.core.model.order.AbstractOrderModel;

public class DefaultMiraklAdvancedPricingGetShippingRatesRequestPopulator implements MiraklGetShippingRatesRequestPopulator {

  protected MiraklCustomerOrganizationResolvingStrategy customerOrganizationResolvingStrategy;

  @Override
  public MiraklGetShippingRatesRequest populate(AbstractOrderModel order, MiraklGetShippingRatesRequest request) {
    String customerOrganizationId = customerOrganizationResolvingStrategy.resolveCurrentUserOrganizationId();
    if (customerOrganizationId != null) {
      request.setPricingCustomerOrganizationId(customerOrganizationId);
    }

    return request;
  }

  @Required
  public void setCustomerOrganizationResolvingStrategy(
      MiraklCustomerOrganizationResolvingStrategy customerOrganizationResolvingStrategy) {
    this.customerOrganizationResolvingStrategy = customerOrganizationResolvingStrategy;
  }
}
