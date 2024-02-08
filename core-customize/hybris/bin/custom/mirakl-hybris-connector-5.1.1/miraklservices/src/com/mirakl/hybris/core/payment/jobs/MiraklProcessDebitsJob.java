package com.mirakl.hybris.core.payment.jobs;

import static java.lang.String.format;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.fulfilment.strategies.ProcessMarketplacePaymentStrategy;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.model.MiraklProcessDebitsCronJobModel;
import com.mirakl.hybris.core.payment.strategies.LookupMiraklDebitsToProcessStrategy;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class MiraklProcessDebitsJob extends AbstractJobPerformable<MiraklProcessDebitsCronJobModel> {
  private static final Logger LOG = Logger.getLogger(MiraklProcessDebitsJob.class);

  protected ProcessMarketplacePaymentStrategy processMarketplacePaymentStrategy;
  protected LookupMiraklDebitsToProcessStrategy lookupMiraklDebitsToProcessStrategy;

  @Override
  public PerformResult perform(MiraklProcessDebitsCronJobModel cronJob) {
    for (MarketplaceConsignmentModel marketplaceConsignment : lookupMiraklDebitsToProcessStrategy.lookupDebitsToProcess()) {
      try {
        processMarketplacePaymentStrategy.processPayment(marketplaceConsignment);
      } catch (Exception e) {
        LOG.error(format("Unable to process payment for consignment [%s]", marketplaceConsignment.getPk()), e);
      }
    }
    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  @Required
  public void setProcessMarketplacePaymentStrategy(ProcessMarketplacePaymentStrategy processMarketplacePaymentStrategy) {
    this.processMarketplacePaymentStrategy = processMarketplacePaymentStrategy;
  }

  @Required
  public void setLookupMiraklDebitsToProcessStrategy(LookupMiraklDebitsToProcessStrategy lookupMiraklDebitsToProcessStrategy) {
    this.lookupMiraklDebitsToProcessStrategy = lookupMiraklDebitsToProcessStrategy;
  }

}
