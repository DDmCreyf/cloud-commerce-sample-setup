package com.mirakl.hybris.core.fulfilment.events.listeners;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.fulfilment.events.NotShippableOffersEvent;
import com.mirakl.hybris.core.fulfilment.strategies.NotShippableOffersHandlingStrategy;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

public class NotShippableOffersEventListener extends AbstractEventListener<NotShippableOffersEvent> {

  protected NotShippableOffersHandlingStrategy notShippableOffersHandlingStrategy;

  @Override
  protected void onEvent(NotShippableOffersEvent event) {
    notShippableOffersHandlingStrategy.handleEvent(event);
  }

  @Required
  public void setNotShippableOffersHandlingStrategy(NotShippableOffersHandlingStrategy notShippableOffersHandlingStrategy) {
    this.notShippableOffersHandlingStrategy = notShippableOffersHandlingStrategy;
  }
}
