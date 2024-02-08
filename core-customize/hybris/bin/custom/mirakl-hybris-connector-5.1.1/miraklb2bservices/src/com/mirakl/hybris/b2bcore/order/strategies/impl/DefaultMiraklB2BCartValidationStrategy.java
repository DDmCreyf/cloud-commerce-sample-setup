package com.mirakl.hybris.b2bcore.order.strategies.impl;

import com.mirakl.hybris.core.order.strategies.impl.DefaultMiraklCartValidationStrategy;
import de.hybris.platform.core.model.order.CartModel;

/**
 * This class is a copy of de.hybris.platform.b2bacceleratorservices.strategies.impl.DefaultB2BCartValidationStrategy, extending
 * com.mirakl.hybris.core.order.strategies.impl.DefaultMiraklCartValidationStrategy rather than
 * de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy
 *
 * @since SAP Commerce 18.11
 */
public class DefaultMiraklB2BCartValidationStrategy extends DefaultMiraklCartValidationStrategy {

  private final static String CHECKOUT_PAYMENT_TYPE_CARD = "CARD";

  @Override
  protected void validateDelivery(final CartModel cartModel) {
    // No reference to the type CheckoutPaymentType for backward compatibility reasons
    if (cartModel.getPaymentType() == null || CHECKOUT_PAYMENT_TYPE_CARD.equals(cartModel.getPaymentType().getCode())) {
      super.validateDelivery(cartModel);
    }
  }
}
