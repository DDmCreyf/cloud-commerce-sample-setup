package com.mirakl.hybris.core.product.jobs;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.model.MiraklImportOfferStatesCronJobModel;
import com.mirakl.hybris.core.product.services.OfferStateImportService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class MiraklImportOfferStatesJob extends AbstractJobPerformable<MiraklImportOfferStatesCronJobModel> {

  private static final Logger LOG = Logger.getLogger(MiraklImportOfferStatesJob.class);

  protected OfferStateImportService offerStateImportService;

  @Override
  public PerformResult perform(MiraklImportOfferStatesCronJobModel cronJob) {
    Collection<OfferState> importedOfferStates;

    try {
      LOG.info("Performing a full offer states import");
      importedOfferStates = offerStateImportService.importAllOfferStates();
    } catch (MiraklApiException e) {
      LOG.error("Exception occurred while importing offer states", e);
      return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
    }

    LOG.info(String.format("Imported %d offer states", importedOfferStates.size()));
    modelService.save(cronJob);

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  @Required
  public void setOfferStateImportService(OfferStateImportService offerStateImportService) {
    this.offerStateImportService = offerStateImportService;
  }
}
