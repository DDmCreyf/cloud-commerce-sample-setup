package com.mirakl.hybris.core.jobs;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.model.MiraklExportStatusCronJobModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class MiraklExportStatusJob extends AbstractJobPerformable<MiraklExportStatusCronJobModel> {

  private static final Logger LOG = Logger.getLogger(MiraklExportStatusJob.class);

  protected ExportJobReportService exportJobReportService;

  @Override
  public PerformResult perform(MiraklExportStatusCronJobModel miraklExportStatusCronJobModel) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Started importing/updating pending export reports");
    }

    boolean allProcessedSuccessfully = exportJobReportService.updatePendingExportReports();
    if (!allProcessedSuccessfully) {
      LOG.error("Not all pending export reports were processed successfully");
      return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Finished importing/updating pending export reports");
    }

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  @Required
  public void setExportJobReportService(ExportJobReportService exportJobReportService) {
    this.exportJobReportService = exportJobReportService;
  }
}
