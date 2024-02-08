package com.mirakl.hybris.core.payment.jobs;

import static java.lang.String.format;

import com.mirakl.hybris.beans.MiraklRefundRequestData;
import org.apache.log4j.Logger;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class MiraklRetrieveRefundRequestsIgnoreUnknownJob extends MiraklRetrieveRefundRequestsJob {
  private static final Logger LOG = Logger.getLogger(MiraklRetrieveRefundRequestsIgnoreUnknownJob.class);

  @Override
  protected void handleSaveException(MiraklRefundRequestData refund, Exception e) {
    if (e instanceof UnknownIdentifierException) {
      LOG.debug(format("Unknown refund with id [%s] Skipping..", refund.getRefundId()));
      return;
    }

    super.handleSaveException(refund, e);
  }


}
