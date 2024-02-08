package com.mirakl.hybris.core.payment.jobs;

import com.mirakl.client.mmp.domain.payment.refund.MiraklOrderLineRefund;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.payment.debit.MiraklConfirmOrderRefundRequest;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.model.MiraklRefundPaymentCronJobModel;
import com.mirakl.hybris.core.returns.strategies.MiraklRefundLookupStrategy;
import com.mirakl.hybris.core.returns.strategies.MiraklRefundProcessingStrategy;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class MiraklRefundPaymentJob extends AbstractJobPerformable<MiraklRefundPaymentCronJobModel> {

  private static final Logger LOG = Logger.getLogger(MiraklRefundPaymentJob.class);
  private static final int REFUNDS_CONFIRMATION_BATCHSIZE_DEFAULT_VALUE = 1;

  protected MiraklRefundLookupStrategy refundLookupStrategy;

  protected MiraklRefundProcessingStrategy refundProcessingStrategy;

  protected MiraklMarketplacePlatformFrontApi miraklApi;

  protected Converter<RefundEntryModel, MiraklOrderLineRefund> miraklOrderLineRefundConverter;
  protected ConfigurationService configurationService;

  @Override
  public PerformResult perform(MiraklRefundPaymentCronJobModel cronJob) {
    processWaitingEntries();
    confirmRefundedEntries();

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  protected void processWaitingEntries() {
    List<RefundEntryModel> pendingRefundEntries = refundLookupStrategy.getRefundEntriesPendingPayment();

    if (isNotEmpty(pendingRefundEntries)) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(format("Found [%d] refund entries waiting to be processed", pendingRefundEntries.size()));
      }

      for (RefundEntryModel refundEntry : pendingRefundEntries) {
        try {
          refundProcessingStrategy.processRefund(refundEntry);
        } catch (Exception e) {
          LOG.error(format("Unable to process refund entry [%s]", refundEntry.getMiraklRefundId()), e);
        }
      }
    }
  }

  protected void confirmRefundedEntries() {
    List<RefundEntryModel> processedRefunds = refundLookupStrategy.getProcessedRefundEntriesPendingConfirmation();
    if (isNotEmpty(processedRefunds)) {
      //The batch size will be at least REFUNDS_CONFIRMATION_BATCHSIZE_DEFAULT_VALUE (1)
      int configuredBatchSize = configurationService.getConfiguration()
          .getInt(MiraklservicesConstants.REFUNDS_CONFIRMATION_BATCHSIZE_PROPERTY_KEY, REFUNDS_CONFIRMATION_BATCHSIZE_DEFAULT_VALUE);
      int batchSize = Math.signum(configuredBatchSize) < 1 ? REFUNDS_CONFIRMATION_BATCHSIZE_DEFAULT_VALUE : configuredBatchSize;

      List<List<RefundEntryModel>> refundConfirmationBatches = ListUtils.partition(processedRefunds, batchSize);
      if (LOG.isDebugEnabled()) {
        LOG.debug(format(
            "Found [%d] refund entries already processed and waiting confirmation. They will be sent to Mirakl in [%d] batches of up to [%d] entries",
            processedRefunds.size(), refundConfirmationBatches.size(), batchSize));
      }

      for (List<RefundEntryModel> refundConfirmation : refundConfirmationBatches) {
        List<MiraklOrderLineRefund> miraklOrderLineRefunds = new ArrayList<>();
        for (RefundEntryModel refundEntry : refundConfirmation) {
          miraklOrderLineRefunds.add(miraklOrderLineRefundConverter.convert(refundEntry));
          refundEntry.setConfirmedToMirakl(true);
        }

        try {
          miraklApi.confirmOrderRefund(new MiraklConfirmOrderRefundRequest(miraklOrderLineRefunds));
          modelService.saveAll(refundConfirmation);
        } catch (Exception e) {
          LOG.error(String.format("An error occurred during orders refund confirmation: [%s]. These refunds have not been properly confirmed.",
              e.getMessage()), e);
        }
      }

    }
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setRefundLookupStrategy(MiraklRefundLookupStrategy refundLookupStrategy) {
    this.refundLookupStrategy = refundLookupStrategy;
  }

  @Required
  public void setRefundProcessingStrategy(MiraklRefundProcessingStrategy refundProcessingStrategy) {
    this.refundProcessingStrategy = refundProcessingStrategy;
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

  @Required
  public void setMiraklOrderLineRefundConverter(Converter<RefundEntryModel, MiraklOrderLineRefund> miraklOrderLineRefundConverter) {
    this.miraklOrderLineRefundConverter = miraklOrderLineRefundConverter;
  }
}
