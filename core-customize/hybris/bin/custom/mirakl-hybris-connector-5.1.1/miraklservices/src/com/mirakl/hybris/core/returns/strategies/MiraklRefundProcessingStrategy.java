package com.mirakl.hybris.core.returns.strategies;

import de.hybris.platform.returns.model.RefundEntryModel;

public interface MiraklRefundProcessingStrategy {

  /**
   * Processes a refund entry
   * 
   * @param refundEntry the refund entry to be processed
   * @return true if the processing was successful, false otherwise
   */
  boolean processRefund(RefundEntryModel refundEntry);
}
