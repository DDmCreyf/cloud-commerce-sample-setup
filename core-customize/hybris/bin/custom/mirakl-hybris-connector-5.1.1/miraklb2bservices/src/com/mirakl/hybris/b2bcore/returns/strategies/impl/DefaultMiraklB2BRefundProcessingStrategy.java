package com.mirakl.hybris.b2bcore.returns.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.b2bcore.payment.strategies.SkipPaymentStrategy;
import com.mirakl.hybris.core.returns.strategies.impl.DefaultMiraklRefundProcessingStrategy;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.model.RefundEntryModel;

public class DefaultMiraklB2BRefundProcessingStrategy extends DefaultMiraklRefundProcessingStrategy {

  protected SkipPaymentStrategy skipPaymentStrategy;

  @Override
  public boolean processRefund(RefundEntryModel refundEntry) {

    if (skipPaymentStrategy.shouldSkipPayment(refundEntry.getOrderEntry().getOrder())) {
      refundEntry.setStatus(ReturnStatus.COMPLETED);
      modelService.save(refundEntry);
      return true;
    }

    return super.processRefund(refundEntry);
  }

  @Required
  public void setSkipPaymentStrategy(SkipPaymentStrategy skipPaymentStrategy) {
    this.skipPaymentStrategy = skipPaymentStrategy;
  }
}
