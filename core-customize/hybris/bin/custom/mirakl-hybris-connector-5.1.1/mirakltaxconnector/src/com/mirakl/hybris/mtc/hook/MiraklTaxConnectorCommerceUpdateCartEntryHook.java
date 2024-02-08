package com.mirakl.hybris.mtc.hook;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklTaxConnectorCommerceUpdateCartEntryHook implements CommerceUpdateCartEntryHook {

  protected MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy;
  protected Converter<OfferModel, AbstractOrderEntryModel> orderEntryConverter;

  @Override
  public void afterUpdateCartEntry(CommerceCartParameter parameter, CommerceCartModification result) {
    if (!miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(parameter.getCart())) {
      return;
    }
    for (AbstractOrderEntryModel marketplaceEntry : parameter.getCart().getMarketplaceEntries()) {
      if (marketplaceEntry.getEntryNumber() == parameter.getEntryNumber()) {
        orderEntryConverter.convert(parameter.getOffer(), marketplaceEntry);
      }
    }
  }

  @Override
  public void beforeUpdateCartEntry(CommerceCartParameter parameter) {
    // Nothing to do here
  }

  @Required
  public void setMiraklTaxConnectorActivationStrategy(MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy) {
    this.miraklTaxConnectorActivationStrategy = miraklTaxConnectorActivationStrategy;
  }

  @Required
  public void setOrderEntryConverter(Converter<OfferModel, AbstractOrderEntryModel> orderEntryConverter) {
    this.orderEntryConverter = orderEntryConverter;
  }

}

