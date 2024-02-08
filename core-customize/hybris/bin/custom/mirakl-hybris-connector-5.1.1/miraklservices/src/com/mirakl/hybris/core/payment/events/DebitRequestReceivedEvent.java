package com.mirakl.hybris.core.payment.events;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class DebitRequestReceivedEvent extends AbstractEvent {

  private static final long serialVersionUID = -2848355932515180653L;

  protected MiraklOrderPayment debitRequest;

  public DebitRequestReceivedEvent(MiraklOrderPayment debitRequest) {
    super();
    validateParameterNotNullStandardMessage("debitRequest", debitRequest);
    this.debitRequest = debitRequest;
  }

  public MiraklOrderPayment getDebitRequest() {
    return debitRequest;
  }

}
