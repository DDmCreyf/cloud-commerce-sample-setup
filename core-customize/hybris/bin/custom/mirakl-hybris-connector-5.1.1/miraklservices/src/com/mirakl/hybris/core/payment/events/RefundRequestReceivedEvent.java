package com.mirakl.hybris.core.payment.events;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.mirakl.hybris.beans.MiraklRefundRequestData;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class RefundRequestReceivedEvent extends AbstractEvent {

  private static final long serialVersionUID = -9131191473095233201L;

  protected MiraklRefundRequestData refundRequest;

  public RefundRequestReceivedEvent(MiraklRefundRequestData refundRequest) {
    super();
    validateParameterNotNullStandardMessage("refundRequest", refundRequest);
    this.refundRequest = refundRequest;
  }

  public MiraklRefundRequestData getRefundRequest() {
    return refundRequest;
  }
}
