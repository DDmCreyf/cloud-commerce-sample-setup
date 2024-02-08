package com.mirakl.hybris.core.payment.jobs;

import static java.lang.String.format;

import org.apache.log4j.Logger;

import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class MiraklRetrieveDebitRequestsIgnoreUnknownJob extends MiraklRetrieveDebitRequestsJob {
  private static final Logger LOG = Logger.getLogger(MiraklRetrieveDebitRequestsIgnoreUnknownJob.class);

  @Override
  protected void handleSaveException(MiraklOrderPayment debit, Exception e) {
    if (e instanceof UnknownIdentifierException) {
      LOG.debug(format("Unknown debit for order id [%s]. Skipping..", debit.getOrderId()));
      return;
    }

    super.handleSaveException(debit, e);
  }


}
