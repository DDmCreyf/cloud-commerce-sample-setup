package com.mirakl.hybris.core.returns.strategies;

import java.util.List;

import de.hybris.platform.returns.model.RefundEntryModel;

public interface MiraklRefundLookupStrategy {

  /**
   * Retrieves all refund entries pending payment
   * 
   * @return refund entries
   */
  List<RefundEntryModel> getRefundEntriesPendingPayment();


  /**
   * Retrieves all processed refund entries pending confirmation to Mirakl
   * 
   * @return refund entries
   */
  List<RefundEntryModel> getProcessedRefundEntriesPendingConfirmation();
}
