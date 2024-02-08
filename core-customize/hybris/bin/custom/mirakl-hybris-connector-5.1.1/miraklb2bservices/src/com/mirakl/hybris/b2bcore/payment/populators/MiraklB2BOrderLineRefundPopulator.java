package com.mirakl.hybris.b2bcore.payment.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.payment.MiraklPaymentStatus;
import com.mirakl.client.mmp.domain.payment.refund.MiraklOrderLineRefund;
import com.mirakl.hybris.b2bcore.payment.strategies.SkipPaymentStrategy;
import com.mirakl.hybris.core.payment.populators.MiraklOrderLineRefundPopulator;

import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class MiraklB2BOrderLineRefundPopulator extends MiraklOrderLineRefundPopulator {

  protected SkipPaymentStrategy skipPaymentStrategy;

  @Override
  public void populate(RefundEntryModel refundEntry, MiraklOrderLineRefund orderLineRefund) throws ConversionException {
    if (skipPaymentStrategy.shouldSkipPayment(refundEntry.getOrderEntry().getOrder())) {
      orderLineRefund.setPaymentStatus(MiraklPaymentStatus.OK);
      orderLineRefund.setRefundId(refundEntry.getMiraklRefundId());
      orderLineRefund.setAmount(refundEntry.getAmount());
      orderLineRefund.setCurrencyIsoCode(MiraklIsoCurrencyCode.valueOf(refundEntry.getCurrency().getIsocode()));
      return;
    }
    super.populate(refundEntry, orderLineRefund);
  }

  @Required
  public void setSkipPaymentStrategy(SkipPaymentStrategy skipPaymentStrategy) {
    this.skipPaymentStrategy = skipPaymentStrategy;
  }
}
