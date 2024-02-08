package com.mirakl.hybris.core.payment.populators;

import com.mirakl.client.mmp.domain.payment.debit.MiraklDebitOrder;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;

import de.hybris.platform.converters.Populator;

public class MiraklOrderPaymentPopulator implements Populator<MiraklDebitOrder, MiraklOrderPayment> {

  @Override
  public void populate(MiraklDebitOrder order, MiraklOrderPayment miraklOrderPayment) {
    miraklOrderPayment.setOrderId(order.getOrderId());
    miraklOrderPayment.setCustomerId(order.getCustomerId());
    miraklOrderPayment.setCurrencyIsoCode(order.getCurrencyIsoCode());
    miraklOrderPayment.setAmount(order.getAmount());
  }
}
