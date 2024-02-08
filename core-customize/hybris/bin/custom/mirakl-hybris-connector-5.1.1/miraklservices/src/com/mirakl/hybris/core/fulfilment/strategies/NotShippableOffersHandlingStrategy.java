package com.mirakl.hybris.core.fulfilment.strategies;

import com.mirakl.hybris.core.fulfilment.events.NotShippableOffersEvent;

public interface NotShippableOffersHandlingStrategy {

  /**
   * Handles not shippable offers
   * 
   * @param event
   */
  void handleEvent(NotShippableOffersEvent event);
}
