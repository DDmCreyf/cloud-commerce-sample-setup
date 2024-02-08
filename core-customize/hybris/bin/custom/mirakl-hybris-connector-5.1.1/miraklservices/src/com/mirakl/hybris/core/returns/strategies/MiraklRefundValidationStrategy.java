package com.mirakl.hybris.core.returns.strategies;

import com.mirakl.hybris.beans.MiraklRefundRequestData;

public interface MiraklRefundValidationStrategy {

  /**
   * Validates a refund request
   * 
   * @param refundRequest the refund request to be validated
   * @return true if the refund request is valid, false otherwise
   */
  boolean isValidRefundRequest(MiraklRefundRequestData refundRequest);

}
