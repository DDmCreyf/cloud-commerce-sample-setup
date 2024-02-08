package com.mirakl.hybris.mtc.hook;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.order.hook.MiraklCommerceCartCalculationMethodHook;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;

import de.hybris.platform.commerceservices.order.hook.CommerceCartCalculationMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

public class MiraklTaxConnectorCommerceCartCalculationMethodHook extends MiraklCommerceCartCalculationMethodHook
    implements CommerceCartCalculationMethodHook {

  protected MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy;

  @Override
  protected boolean shouldUpdateCartCalculationJSON(CommerceCartParameter parameter) {
    return (miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()
        || miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(parameter.getCart()))
        && !parameter.getCart().getMarketplaceEntries().isEmpty();
  }

  @Required
  public void setMiraklTaxConnectorActivationStrategy(MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy) {
    this.miraklTaxConnectorActivationStrategy = miraklTaxConnectorActivationStrategy;
  }
}
