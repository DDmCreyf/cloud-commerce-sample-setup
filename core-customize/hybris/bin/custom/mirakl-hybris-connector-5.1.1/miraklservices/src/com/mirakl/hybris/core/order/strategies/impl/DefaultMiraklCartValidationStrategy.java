package com.mirakl.hybris.core.order.strategies.impl;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.strategies.hooks.MiraklCartValidationHook;
import com.mirakl.hybris.core.product.services.OfferService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static java.lang.String.format;

public class DefaultMiraklCartValidationStrategy extends DefaultCartValidationStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultMiraklCartValidationStrategy.class);

  protected OfferService offerService;
  protected MiraklCartValidationHook validationHook;

  @Override
  protected CommerceCartModification validateCartEntry(CartModel cart, CartEntryModel cartEntry) {
    CommerceCartModification cartModification = validationHook.beforeValidateCartEntry(cartEntry);
    if (cartModification != null) {
      return cartModification;
    }
    return super.validateCartEntry(cart, cartEntry);
  }

  @Override
  protected Long getStockLevel(CartEntryModel cartEntryModel) {
    if (cartEntryModel.getOfferId() != null) {
      try {
        return getStockForOffer(cartEntryModel.getOfferId());
      } catch (UnknownIdentifierException e) {
        LOG.error(format("Unable to find stock level for offer [%s]. Offer may have been disabled or deleted.",
            cartEntryModel.getOfferId()), e);
        return 0L;
      }
    }

    return super.getStockLevel(cartEntryModel);
  }

  protected Long getStockForOffer(String offerId) {
    OfferModel offerModel = offerService.getOfferForId(offerId);
    return offerModel.getQuantity() == null ? 0 : offerModel.getQuantity().longValue();
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setValidationHook(MiraklCartValidationHook validationHook) {
    this.validationHook = validationHook;
  }
}
