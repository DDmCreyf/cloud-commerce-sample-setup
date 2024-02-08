package com.mirakl.hybris.b2bcore.fulfilment.strategies.impl;

import static java.lang.String.format;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.b2bcore.payment.strategies.SkipPaymentStrategy;
import com.mirakl.hybris.core.fulfilment.strategies.impl.DefaultProcessOperatorPaymentStrategy;

import de.hybris.platform.core.model.order.OrderModel;

public class DefaultB2BProcessOperatorPaymentStrategy extends DefaultProcessOperatorPaymentStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultB2BProcessOperatorPaymentStrategy.class);

  protected SkipPaymentStrategy skipPaymentStrategy;

  @Override
  public boolean processPayment(OrderModel order) {

    if (skipPaymentStrategy.shouldSkipPayment(order)) {
      if (LOG.isDebugEnabled()) {
        LOG.info(format("Skipping Payment Action for order [%s]...", order.getCode()));
      }
      return true;
    }

    return super.processPayment(order);
  }

  @Required
  public void setSkipPaymentStrategy(SkipPaymentStrategy skipPaymentStrategy) {
    this.skipPaymentStrategy = skipPaymentStrategy;
  }
}
