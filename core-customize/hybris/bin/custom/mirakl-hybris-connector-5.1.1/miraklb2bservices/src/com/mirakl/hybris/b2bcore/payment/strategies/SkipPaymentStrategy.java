package com.mirakl.hybris.b2bcore.payment.strategies;

import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface SkipPaymentStrategy {

  /**
   * Allows to skip the payment and the refund of an order (Mainly used to handle B2B account payment). When it returns true, the
   * payment service won't be called but everything else works as usual.
   * 
   * @param order The order to evaluate
   * @return true or false depending on whether or not the payment should be skipped
   */
  boolean shouldSkipPayment(AbstractOrderModel order);
}
