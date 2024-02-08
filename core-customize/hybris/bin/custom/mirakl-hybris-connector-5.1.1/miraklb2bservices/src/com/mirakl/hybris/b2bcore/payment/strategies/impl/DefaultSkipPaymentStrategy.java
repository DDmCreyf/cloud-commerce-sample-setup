package com.mirakl.hybris.b2bcore.payment.strategies.impl;

import com.mirakl.hybris.b2bcore.payment.strategies.SkipPaymentStrategy;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

public class DefaultSkipPaymentStrategy implements SkipPaymentStrategy {

  @Override
  public boolean shouldSkipPayment(AbstractOrderModel order) {
    return order.getPaymentInfo() instanceof InvoicePaymentInfoModel;
  }

}
