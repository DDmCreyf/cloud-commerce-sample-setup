package com.mirakl.hybris.core.payment.populators;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.payment.MiraklPaymentStatus;
import com.mirakl.client.mmp.domain.payment.refund.MiraklOrderLineRefund;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklOrderLineRefundPopulator implements Populator<RefundEntryModel, MiraklOrderLineRefund> {

  @Override
  public void populate(RefundEntryModel refundEntry, MiraklOrderLineRefund orderLineRefund) throws ConversionException {
    PaymentTransactionEntryModel txnEntry = refundEntry.getPaymentTransactionEntry();
    boolean refundAccepted = TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus());
    orderLineRefund.setPaymentStatus(refundAccepted ? MiraklPaymentStatus.OK : MiraklPaymentStatus.REFUSED);
    orderLineRefund.setTransactionDate(txnEntry.getTime());
    orderLineRefund.setTransactionNumber(txnEntry.getCode());
    orderLineRefund.setRefundId(refundEntry.getMiraklRefundId());
    orderLineRefund.setAmount(refundEntry.getAmount());
    orderLineRefund.setCurrencyIsoCode(MiraklIsoCurrencyCode.valueOf(refundEntry.getCurrency().getIsocode()));
  }

}
