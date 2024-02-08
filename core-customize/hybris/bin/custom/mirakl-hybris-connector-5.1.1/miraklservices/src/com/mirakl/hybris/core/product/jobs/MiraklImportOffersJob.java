package com.mirakl.hybris.core.product.jobs;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.USE_ASYNC_OFFER_IMPORT;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.hybris.core.model.MiraklImportOffersCronJobModel;
import com.mirakl.hybris.core.product.strategies.PerformJobStrategy;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class MiraklImportOffersJob extends AbstractJobPerformable<MiraklImportOffersCronJobModel> {

  private static final Logger LOG = Logger.getLogger(MiraklImportOffersJob.class);

  protected ConfigurationService configurationService;
  protected PerformJobStrategy<MiraklImportOffersCronJobModel> syncOfferImportStrategy;
  protected PerformJobStrategy<MiraklImportOffersCronJobModel> asyncOfferImportSchedulingStrategy;

  @Override
  public PerformResult perform(MiraklImportOffersCronJobModel cronJob) {
    try {
      return resolveImportStrategy().perform(cronJob);
    } catch (MiraklApiException e) {
      LOG.error("Exception occurred while importing offers", e);
      return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
    }
  }

  protected PerformJobStrategy<MiraklImportOffersCronJobModel> resolveImportStrategy() {
    boolean useMiraklAsyncTask = configurationService.getConfiguration() //
        .getBoolean(USE_ASYNC_OFFER_IMPORT, false);
    return useMiraklAsyncTask ? asyncOfferImportSchedulingStrategy : syncOfferImportStrategy;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setSyncOfferImportStrategy(PerformJobStrategy<MiraklImportOffersCronJobModel> syncOfferImportStrategy) {
    this.syncOfferImportStrategy = syncOfferImportStrategy;
  }

  @Required
  public void setAsyncOfferImportSchedulingStrategy(
      PerformJobStrategy<MiraklImportOffersCronJobModel> asyncOfferImportSchedulingStrategy) {
    this.asyncOfferImportSchedulingStrategy = asyncOfferImportSchedulingStrategy;
  }
}
