package com.mirakl.hybris.promotions.jobs;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.promotions.model.MiraklImportPromotionsOfferMappingCronJobModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionImportService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class MiraklImportPromotionsOfferMappingJob
    extends AbstractJobPerformable<MiraklImportPromotionsOfferMappingCronJobModel> {

  private static final Logger LOG = Logger.getLogger(MiraklImportPromotionsOfferMappingJob.class);

  protected MiraklPromotionImportService miraklPromotionImportService;

  @Override
  public PerformResult perform(MiraklImportPromotionsOfferMappingCronJobModel cronJob) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Started mapping Mirakl Promotions with Offers");
    }
    miraklPromotionImportService.importPromotionOffersMapping(cronJob.getLastExecutionDate());

    cronJob.setLastExecutionDate(cronJob.getStartTime());
    modelService.save(cronJob);

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  @Required
  public void setMiraklPromotionImportService(MiraklPromotionImportService miraklPromotionImportService) {
    this.miraklPromotionImportService = miraklPromotionImportService;
  }
}
