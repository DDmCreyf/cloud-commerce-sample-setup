package com.mirakl.hybris.promotions.jobs;

import static java.lang.String.format;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.promotions.model.MiraklImportPromotionsCronJobModel;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionImportService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class MiraklImportPromotionsJob extends AbstractJobPerformable<MiraklImportPromotionsCronJobModel> {

  private static final Logger LOG = Logger.getLogger(MiraklImportPromotionsJob.class);

  protected MiraklPromotionImportService miraklPromotionImportService;

  @Override
  public PerformResult perform(MiraklImportPromotionsCronJobModel cronJob) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Started importing Mirakl Promotions");
    }
    Collection<MiraklPromotionModel> importedPromotions = miraklPromotionImportService.importAllPromotions();
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Inserted/Updated [%s] Mirakl Promotions", importedPromotions.size()));
    }

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  @Required
  public void setMiraklPromotionImportService(MiraklPromotionImportService miraklPromotionImportService) {
    this.miraklPromotionImportService = miraklPromotionImportService;
  }
}
