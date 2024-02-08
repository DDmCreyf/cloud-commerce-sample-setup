package com.mirakl.hybris.fulfilmentprocess.jobs;

import static java.lang.String.format;

import org.apache.log4j.Logger;

import com.mirakl.client.mmp.domain.order.MiraklOrder;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class MiraklRetrieveConsignmentUpdatesIgnoreUnknownJob extends MiraklRetrieveConsignmentUpdatesJob {

  private static final Logger LOG = Logger.getLogger(MiraklRetrieveConsignmentUpdatesIgnoreUnknownJob.class);

  @Override
  protected void handleUpdateException(MiraklOrder miraklOrder, Exception e) {
    if (e instanceof UnknownIdentifierException) {
      LOG.debug(format("Unknown consignment [%s]. Skipping..", miraklOrder.getId()));
      return;
    }

    super.handleUpdateException(miraklOrder, e);
  }

}
