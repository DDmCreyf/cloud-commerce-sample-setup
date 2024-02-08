package com.mirakl.hybris.core.jobs.populators;

import static com.mirakl.client.core.internal.util.Preconditions.checkArgument;
import static com.mirakl.hybris.core.constants.MiraklservicesConstants.ERROR_REPORT_MIME_TYPE;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.io.Files;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

public abstract class AbstractMiraklJobReportPopulator<RESULT, STATUS, REPORT, TARGET extends MiraklJobReportModel>
    implements Populator<RESULT, TARGET> {

  private static final Logger LOG = Logger.getLogger(AbstractMiraklJobReportPopulator.class);

  protected ModelService modelService;
  protected MediaService mediaService;
  protected MiraklMarketplacePlatformFrontApi mmpApi;
  protected MiraklCatalogIntegrationFrontApi mciApi;
  protected Map<STATUS, MiraklExportStatus> exportStatuses;

  @Override
  public void populate(RESULT result, TARGET target) throws ConversionException {
    populateFromResult(result, target);
    if (shouldDownloadReport(result, target)) {
      populateFromReport(result, getReport(result, target), target);
    }
  }

  protected abstract void populateFromResult(RESULT result, TARGET target) throws ConversionException;

  protected abstract REPORT getReport(RESULT result, TARGET target);

  protected abstract File getErrorReportFile(RESULT result, REPORT report) throws IOException;

  protected void populateFromReport(RESULT result, REPORT report, TARGET target) {
    setErrorReport(result, report, target);
  }

  protected boolean shouldDownloadReport(RESULT result, TARGET target) {
    return target.getHasErrorReport();
  }

  protected void setErrorReport(RESULT source, REPORT jobReport, TARGET target) {
    if (BooleanUtils.isTrue(target.getHasErrorReport())) {
      String jobId = target.getJobId();
      LOG.warn(format("Export of type [%s] with syncJobId [%s] ended with errors - setting error report", target.getReportType(),
          jobId));
      try {
        target.setErrorReport(createErrorReport(getErrorReportFile(source, jobReport), jobId, target.getReportType()));
      } catch (IOException e) {
        LOG.error("Exception occurred while setting error report", e);
      }
    }
  }

  protected MediaModel createErrorReport(File errorReport, String jobId, MiraklExportType reportType) throws IOException {
    checkArgument(isNotBlank(jobId), "Cannot create error report - missing job ID");
    checkArgument(reportType != null, "Cannot create error report - missing report type");

    CatalogUnawareMediaModel mediaErrorReport = modelService.create(CatalogUnawareMediaModel.class);
    mediaErrorReport.setCode(format("%s-%s", reportType, jobId));
    mediaErrorReport.setRealFileName(errorReport.getName());
    mediaErrorReport.setMime(ERROR_REPORT_MIME_TYPE);

    modelService.save(mediaErrorReport);
    mediaService.setStreamForMedia(mediaErrorReport, Files.asByteSource(errorReport).openStream());
    modelService.save(mediaErrorReport);

    return mediaErrorReport;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMediaService(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @Required
  public void setMmpApi(MiraklMarketplacePlatformFrontApi mmpApi) {
    this.mmpApi = mmpApi;
  }

  @Required
  public void setMciApi(MiraklCatalogIntegrationFrontApi mciApi) {
    this.mciApi = mciApi;
  }

  @Required
  public void setExportStatuses(Map<STATUS, MiraklExportStatus> exportStatues) {
    this.exportStatuses = exportStatues;
  }

}
