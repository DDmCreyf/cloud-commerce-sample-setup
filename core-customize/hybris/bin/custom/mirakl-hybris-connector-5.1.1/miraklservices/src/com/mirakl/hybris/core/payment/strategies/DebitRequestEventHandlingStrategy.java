package com.mirakl.hybris.core.payment.strategies;

import com.mirakl.hybris.core.payment.events.DebitRequestReceivedEvent;

public interface DebitRequestEventHandlingStrategy {

  /**
   * Handles a received debit request event
   * 
   * @param event the received event
   */
  void handleEvent(DebitRequestReceivedEvent event);
}
