package com.mirakl.hybris.core.order.services;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

/**
 * Used by actions doing captures
 */
public interface TakePaymentService {

  /**
   * Captures the full amount of the order
   *
   * @param order the order to perform the capture for
   * @return the capture transaction entry
   */
  PaymentTransactionEntryModel fullCapture(AbstractOrderModel order);

  /**
   * Performs a partial capture of the given amount
   *
   * @param order           the order to perform the capture for
   * @param amountToCapture the amount to capture
   * @return the capture transaction entry
   */
  PaymentTransactionEntryModel partialCapture(AbstractOrderModel order, double amountToCapture);

  /**
   * Returns the transaction payment to be used for a partial capture
   *
   * @param order           the order to perform the capture for
   * @param amountToCapture the amount to capture
   * @return the transaction to be used for capture
   */
  PaymentTransactionModel getPaymentTransactionToUseForCapture(final AbstractOrderModel order, double amountToCapture);

}
