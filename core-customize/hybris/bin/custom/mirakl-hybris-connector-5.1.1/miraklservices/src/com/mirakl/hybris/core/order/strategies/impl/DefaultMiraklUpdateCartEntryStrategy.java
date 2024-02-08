package com.mirakl.hybris.core.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.CartAdjustment;
import com.mirakl.hybris.core.order.strategies.CommonMiraklCartStrategy;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;

public class DefaultMiraklUpdateCartEntryStrategy extends DefaultCommerceUpdateCartEntryStrategy {

  protected CommonMiraklCartStrategy commonCartStrategy;
  protected OfferService offerService;

  @Override
  public CommerceCartModification updateQuantityForCartEntry(final CommerceCartParameter parameters)
      throws CommerceCartModificationException {
    final CartModel cartModel = parameters.getCart();
    validateParameterNotNull(cartModel, "Cart model cannot be null");

    final long newQuantity = parameters.getQuantity();
    final long entryNumber = parameters.getEntryNumber();
    final AbstractOrderEntryModel entryToUpdate = getEntryForNumber(cartModel, (int) entryNumber);

    if (entryToUpdate.getOfferId() == null) {
      return super.updateQuantityForCartEntry(parameters);
    }

    if (parameters.getOffer() == null) {
      parameters.setOffer(offerService.getOfferForIdIgnoreSearchRestrictions(entryToUpdate.getOfferId()));
    }

    beforeUpdateCartEntry(parameters);
    validateEntryBeforeModification(newQuantity, entryToUpdate);

    CartAdjustment addToCartResult = commonCartStrategy.calculateCartAdjustment(parameters);
    final long actualAllowedQuantityChange = addToCartResult.getAllowedQuantityChange();
    final Integer maxOrderQuantity = addToCartResult.getMaxOrderQuantity();

    CommerceCartModification modification =
        modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);
    modification.setStatusCode(addToCartResult.getStatus());
    if (modification.getEntry().equals(entryToUpdate)) {
      modification.getEntry().setOfferId(entryToUpdate.getOfferId());
    }

    afterUpdateCartEntry(parameters, modification);

    return modification;
  }

  @Required
  public void setCommonCartStrategy(CommonMiraklCartStrategy commonCartStrategy) {
    this.commonCartStrategy = commonCartStrategy;
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

}
