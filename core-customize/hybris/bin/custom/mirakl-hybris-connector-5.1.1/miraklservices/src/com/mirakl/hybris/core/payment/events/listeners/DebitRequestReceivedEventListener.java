package com.mirakl.hybris.core.payment.events.listeners;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.payment.events.DebitRequestReceivedEvent;
import com.mirakl.hybris.core.payment.strategies.DebitRequestEventHandlingStrategy;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

public class DebitRequestReceivedEventListener extends AbstractEventListener<DebitRequestReceivedEvent> {

  protected DebitRequestEventHandlingStrategy debitRequestEventHandlingStrategy;

  @Override
  protected void onEvent(DebitRequestReceivedEvent event) {
    debitRequestEventHandlingStrategy.handleEvent(event);
  }

  @Required
  public void setDebitRequestEventHandlingStrategy(DebitRequestEventHandlingStrategy debitRequestEventHandlingStrategy) {
    this.debitRequestEventHandlingStrategy = debitRequestEventHandlingStrategy;
  }

}
