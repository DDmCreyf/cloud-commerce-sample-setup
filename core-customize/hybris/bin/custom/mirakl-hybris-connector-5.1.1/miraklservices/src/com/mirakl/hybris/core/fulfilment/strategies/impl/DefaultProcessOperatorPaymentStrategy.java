package com.mirakl.hybris.core.fulfilment.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.fulfilment.strategies.ProcessOperatorPaymentStrategy;
import com.mirakl.hybris.core.order.services.MiraklCalculationService;
import com.mirakl.hybris.core.order.services.TakePaymentService;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public class DefaultProcessOperatorPaymentStrategy implements ProcessOperatorPaymentStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultProcessOperatorPaymentStrategy.class);

  protected TakePaymentService takePaymentService;

  protected CommonI18NService commonI18NService;

  protected MiraklCalculationService miraklCalculationService;

  protected ModelService modelService;

  @Override
  public boolean processPayment(OrderModel order) {

    if (isEmpty(order.getOperatorEntries())) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(format("No operator entries for order [%s]. No capture needed at this level.", order.getCode()));
      }
      return true;
    }

    boolean needsPartialCapture = isNotEmpty(order.getMarketplaceEntries());
    PaymentTransactionEntryModel txnEntry = needsPartialCapture ? partialCapture(order) : fullCapture(order);
    if (!TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus())) {
      failedCapture(order, txnEntry);
      return false;
    }

    successfulCapture(order, txnEntry, needsPartialCapture);
    return true;
  }

  protected PaymentTransactionEntryModel fullCapture(OrderModel order) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Performing a full capture for order [%s]", order.getCode()));
    }
    return takePaymentService.fullCapture(order);
  }

  protected PaymentTransactionEntryModel partialCapture(OrderModel order) {
    double operatorAmount = miraklCalculationService.calculateOperatorAmount(order);
    double alreadyCapturedAmount = miraklCalculationService.calculateAlreadyCapturedAmount(order);
    double amountToCapture =
        commonI18NService.roundCurrency(operatorAmount - alreadyCapturedAmount, order.getCurrency().getDigits());

    return takePaymentService.partialCapture(order, amountToCapture);
  }

  protected void failedCapture(final OrderModel order, PaymentTransactionEntryModel txnEntry) {
    LOG.error(format("The payment transaction capture has failed. Order: %s. Txn: %s", order.getCode(),
        txnEntry.getPaymentTransaction().getCode()));
    setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
  }

  protected void successfulCapture(final OrderModel order, PaymentTransactionEntryModel txnEntry, boolean partialCapture) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("A payment transaction for operator entries has been captured. Order: %s. Txn: %s", order.getCode(),
          txnEntry.getPaymentTransaction().getCode()));
    }
    if (!partialCapture) {
      setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
    }
  }

  protected void setOrderStatus(OrderModel order, OrderStatus orderStatus) {
    order.setStatus(orderStatus);
    this.modelService.save(order);
  }

  @Required
  public void setTakePaymentService(TakePaymentService takePaymentService) {
    this.takePaymentService = takePaymentService;
  }

  @Required
  public void setCommonI18NService(CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }

  @Required
  public void setMiraklCalculationService(MiraklCalculationService miraklCalculationService) {
    this.miraklCalculationService = miraklCalculationService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }
}
