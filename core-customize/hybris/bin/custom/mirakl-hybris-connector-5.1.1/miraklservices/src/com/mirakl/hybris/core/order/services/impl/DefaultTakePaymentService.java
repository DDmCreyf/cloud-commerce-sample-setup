package com.mirakl.hybris.core.order.services.impl;

import static com.google.common.base.Verify.verifyNotNull;
import static java.lang.String.format;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.order.services.TakePaymentService;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

public class DefaultTakePaymentService implements TakePaymentService {

  private static final Logger LOG = Logger.getLogger(DefaultTakePaymentService.class);

  protected PaymentService paymentService;

  @Override
  public PaymentTransactionEntryModel fullCapture(AbstractOrderModel order) {
    PaymentTransactionEntryModel txnEntry = null;
    for (final PaymentTransactionModel txn : order.getPaymentTransactions()) {
      txnEntry = paymentService.capture(txn);
      if (!TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus())) {
        return txnEntry;
      }
    }
    return verifyNotNull(txnEntry);
  }

  @Override
  public PaymentTransactionEntryModel partialCapture(AbstractOrderModel order, double amountToCapture) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Performing a partial capture of [%s] for order [%s]", amountToCapture, order.getCode()));
    }
    PaymentTransactionModel txn = getPaymentTransactionToUseForCapture(order, amountToCapture);

    return paymentService.partialCapture(txn, BigDecimal.valueOf(amountToCapture));
  }

  @Override
  public PaymentTransactionModel getPaymentTransactionToUseForCapture(AbstractOrderModel order, final double amountToCapture) {
    return order.getPaymentTransactions().get(0);
  }


  @Required
  public void setPaymentService(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

}
