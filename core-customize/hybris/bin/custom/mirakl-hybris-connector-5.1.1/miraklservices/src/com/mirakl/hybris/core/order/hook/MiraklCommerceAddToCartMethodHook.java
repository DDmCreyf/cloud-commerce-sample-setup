package com.mirakl.hybris.core.order.hook;

import com.mirakl.hybris.core.order.services.ShippingOptionsService;
import com.mirakl.hybris.core.order.strategies.SynchronousCartUpdateActivationStrategy;
import com.mirakl.hybris.core.product.services.OfferService;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


public class MiraklCommerceAddToCartMethodHook implements CommerceAddToCartMethodHook {

  protected OfferService offerService;
  protected ShippingOptionsService shippingOptionsService;
  protected CommerceCartCalculationStrategy commerceCartCalculationStrategy;
  protected SynchronousCartUpdateActivationStrategy synchronousCartUpdateActivationStrategy;

  @Override
  public void beforeAddToCart(final CommerceCartParameter parameters) {
    validateParameterNotNullStandardMessage("CommerceCartParameter", parameters);

    if (parameters.getOffer() != null) {
      if (synchronousCartUpdateActivationStrategy.isSynchronousCartUpdateEnabled()) {
        offerService.updateExistingOfferForId(parameters.getOffer().getId());
      }
      shippingOptionsService.resetSavedShippingOptions(parameters.getCart());
    }
  }

  @Override
  public void afterAddToCart(final CommerceCartParameter parameters, final CommerceCartModification result) {
    // Nothing to do here
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setShippingOptionsService(ShippingOptionsService shippingOptionsService) {
    this.shippingOptionsService = shippingOptionsService;
  }

  @Required
  public void setCommerceCartCalculationStrategy(CommerceCartCalculationStrategy commerceCartCalculationStrategy) {
    this.commerceCartCalculationStrategy = commerceCartCalculationStrategy;
  }

  @Required
  public void setSynchronousCartUpdateActivationStrategy(
      SynchronousCartUpdateActivationStrategy synchronousCartUpdateActivationStrategy) {
    this.synchronousCartUpdateActivationStrategy = synchronousCartUpdateActivationStrategy;
  }
}
