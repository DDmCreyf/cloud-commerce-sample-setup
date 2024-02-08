package com.mirakl.hybris.core.payment.strategies;

import com.mirakl.hybris.core.payment.events.RefundRequestReceivedEvent;

public interface RefundRequestEventHandlingStrategy {
  /**
   * Handles a received refund request event
   * 
   * @param event the received event
   */
  void handleEvent(RefundRequestReceivedEvent event);
}
