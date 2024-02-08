package com.mirakl.hybris.core.payment.strategies.impl;

import com.mirakl.hybris.beans.MiraklRefundRequestData;
import com.mirakl.hybris.core.payment.events.RefundRequestReceivedEvent;
import com.mirakl.hybris.core.payment.services.MiraklRefundService;
import com.mirakl.hybris.core.payment.strategies.RefundRequestEventHandlingStrategy;

public class DefaultRefundRequestEventHandlingStrategy implements RefundRequestEventHandlingStrategy {

  protected MiraklRefundService miraklRefundService;

  @Override
  public void handleEvent(RefundRequestReceivedEvent event) {
    MiraklRefundRequestData refundRequest = event.getRefundRequest();
    miraklRefundService.saveReceivedRefundRequest(refundRequest);
  }

  public void setMiraklRefundService(MiraklRefundService miraklRefundService) {
    this.miraklRefundService = miraklRefundService;
  }

}
