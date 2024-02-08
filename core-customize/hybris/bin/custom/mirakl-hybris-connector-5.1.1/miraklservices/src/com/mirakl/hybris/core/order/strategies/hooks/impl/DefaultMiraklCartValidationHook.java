package com.mirakl.hybris.core.order.strategies.hooks.impl;

import static org.apache.commons.lang.StringUtils.isEmpty;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.strategies.hooks.MiraklCartValidationHook;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMiraklCartValidationHook implements MiraklCartValidationHook {

  protected OfferService offerService;
  protected ModelService modelService;
  protected CommonI18NService commonI18NService;

  @Override
  public CommerceCartModification beforeValidateCartEntry(CartEntryModel cartEntry) {
    String offerId = cartEntry.getOfferId();
    if (!isEmpty(offerId)) {
      try {
        OfferModel offer = offerService.getOfferForId(offerId);
        return validateCurrency(cartEntry, offer);
      } catch (UnknownIdentifierException e) {
        return getUnavailableOfferCartModification(cartEntry);
      }
    }
    return null;
  }

  protected CommerceCartModification validateCurrency(AbstractOrderEntryModel entry, OfferModel offer) {
    if (!entry.getOrder().getCurrency().equals(offer.getCurrency())
        || !commonI18NService.getCurrentCurrency().equals(offer.getCurrency())) {
      return getUnavailableOfferCartModification(entry);
    }
    return null;
  }

  protected CommerceCartModification getUnavailableOfferCartModification(AbstractOrderEntryModel entry) {
    final AbstractOrderModel cart = entry.getOrder();
    final CommerceCartModification modification = new CommerceCartModification();
    modification.setQuantity(entry.getQuantity());
    modification.setStatusCode(CommerceCartModificationStatus.UNAVAILABLE);
    modification.setQuantityAdded(0);
    modification.setEntry(getUnavailableCartEntry(entry));
    modelService.remove(entry);
    modelService.refresh(cart);
    return modification;
  }

  protected CartEntryModel getUnavailableCartEntry(AbstractOrderEntryModel entry) {
    final CartEntryModel cartEntry = new CartEntryModel() {
      @Override
      public Double getBasePrice() {
        return null;
      }

      @Override
      public Double getTotalPrice() {
        return null;
      }
    };
    cartEntry.setProduct(entry.getProduct());
    return cartEntry;
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setCommonI18NService(CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }
}
