package com.mirakl.hybris.core.returns.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.returns.strategies.MiraklRefundProcessingStrategy;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMiraklRefundProcessingStrategy implements MiraklRefundProcessingStrategy {

  protected PaymentService paymentService;

  protected ModelService modelService;

  @Override
  public boolean processRefund(RefundEntryModel refundEntry) {
    if (refundEntry.getPaymentTransactionEntry() != null) {
      throw new IllegalStateException(format("Refund entry [%s] has already been processed", refundEntry.getMiraklRefundId()));
    }

    PaymentTransactionEntryModel txnEntry =
        paymentService.refundFollowOn(getPaymentTransactionForRefund(refundEntry), refundEntry.getAmount());
    refundEntry.setStatus(TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus()) ? ReturnStatus.COMPLETED
        : ReturnStatus.PAYMENT_REVERSAL_FAILED);
    refundEntry.setPaymentTransactionEntry(txnEntry);
    modelService.save(refundEntry);

    return true;
  }

  protected PaymentTransactionModel getPaymentTransactionForRefund(RefundEntryModel refundEntry) {
    AbstractOrderModel order = refundEntry.getOrderEntry().getOrder();
    List<PaymentTransactionModel> paymentTransactions = order.getPaymentTransactions();
    if (isEmpty(paymentTransactions)) {
      throw new IllegalStateException(format("Cannot find a payment transaction within order [%s] to perform a refund [%s]",
          order.getCode(), refundEntry.getMiraklRefundId()));
    }

    return paymentTransactions.get(0);
  }

  @Required
  public void setPaymentService(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}
