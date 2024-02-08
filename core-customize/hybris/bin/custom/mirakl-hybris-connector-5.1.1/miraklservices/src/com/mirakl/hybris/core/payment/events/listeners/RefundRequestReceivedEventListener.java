package com.mirakl.hybris.core.payment.events.listeners;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.payment.events.RefundRequestReceivedEvent;
import com.mirakl.hybris.core.payment.strategies.RefundRequestEventHandlingStrategy;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

public class RefundRequestReceivedEventListener extends AbstractEventListener<RefundRequestReceivedEvent> {

  protected RefundRequestEventHandlingStrategy refundRequestEventHandlingStrategy;

  @Override
  protected void onEvent(RefundRequestReceivedEvent event) {
    refundRequestEventHandlingStrategy.handleEvent(event);
  }

  @Required
  public void setRefundRequestEventHandlingStrategy(RefundRequestEventHandlingStrategy refundRequestEventHandlingStrategy) {
    this.refundRequestEventHandlingStrategy = refundRequestEventHandlingStrategy;
  }


}
