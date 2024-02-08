package com.mirakl.hybris.core.payment.strategies.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.payment.events.DebitRequestReceivedEvent;
import com.mirakl.hybris.core.payment.services.MiraklDebitService;
import com.mirakl.hybris.core.payment.strategies.DebitRequestEventHandlingStrategy;

public class DefaultDebitRequestEventHandlingStrategy implements DebitRequestEventHandlingStrategy {

  protected MiraklDebitService miraklDebitService;

  @Override
  public void handleEvent(DebitRequestReceivedEvent event) {
    miraklDebitService.saveReceivedDebitRequest(event.getDebitRequest());
  }

  @Required
  public void setMiraklDebitService(MiraklDebitService miraklDebitService) {
    this.miraklDebitService = miraklDebitService;
  }

}
