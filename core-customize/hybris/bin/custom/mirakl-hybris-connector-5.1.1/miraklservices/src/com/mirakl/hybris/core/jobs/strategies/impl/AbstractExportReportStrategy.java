package com.mirakl.hybris.core.jobs.strategies.impl;

import static java.lang.String.format;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.dao.MiraklJobReportDao;
import com.mirakl.hybris.core.jobs.strategies.ExportReportStrategy;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.model.ModelService;

public abstract class AbstractExportReportStrategy<RESULT, STATUS> implements ExportReportStrategy {

  private static final Logger LOG = Logger.getLogger(AbstractExportReportStrategy.class);
  protected static final String ERROR_REPORT_MIME_TYPE = "text/csv";

  protected ModelService modelService;
  protected MiraklJobReportDao miraklJobReportDao;
  protected MiraklCatalogIntegrationFrontApi mciApi;
  protected MiraklMarketplacePlatformFrontApi mmpApi;
  protected Map<STATUS, MiraklExportStatus> exportStatuses;
  protected Populator<RESULT, MiraklJobReportModel> reportPopulator;


  @Override
  public boolean updatePendingExports() {
    List<MiraklJobReportModel> pendingExportReports = getPendingMiraklJobReports();
    List<MiraklJobReportModel> updatedReports = new ArrayList<>();
    boolean allReportsProcessedSuccessfully = true;

    for (MiraklJobReportModel pendingExportReport : pendingExportReports) {
      try {
        RESULT exportResult = getExportResult(pendingExportReport.getJobId());

        if (isExportCompleted(exportResult)) {
          updatePendingReport(pendingExportReport, exportResult);
          updatedReports.add(pendingExportReport);
        }
      } catch (MiraklApiException e) {
        allReportsProcessedSuccessfully &= canHandleMiraklApiException(updatedReports, pendingExportReport, e);
      }
    }
    if (CollectionUtils.isNotEmpty(updatedReports)) {
      modelService.saveAll(updatedReports);
    }

    return allReportsProcessedSuccessfully;
  }

  protected void updatePendingReport(MiraklJobReportModel pendingExportReport, RESULT synchroResult) {
    reportPopulator.populate(synchroResult, pendingExportReport);
  }

  protected boolean canHandleMiraklApiException(List<MiraklJobReportModel> updatedReports,
      MiraklJobReportModel pendingExportReport, MiraklApiException exception) {
    if (isExportNotFound(exception.getError())) {
      LOG.info(format("Export with syncJobId [%s] not found", pendingExportReport.getJobId()));
      pendingExportReport.setStatus(MiraklExportStatus.NOT_FOUND);
      updatedReports.add(pendingExportReport);
      return true;
    }
    LOG.error(format("Exception occurred while updating export report [%s]", pendingExportReport.getJobId()), exception);
    return false;
  }

  protected boolean isExportNotFound(MiraklErrorResponseBean error) {
    return error != null && NOT_FOUND.getStatusCode() == error.getStatus();
  }

  protected abstract RESULT getExportResult(String syncJobId);

  protected abstract boolean isExportCompleted(RESULT exportResult);

  protected abstract List<MiraklJobReportModel> getPendingMiraklJobReports();

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setMiraklJobReportDao(MiraklJobReportDao miraklJobReportDao) {
    this.miraklJobReportDao = miraklJobReportDao;
  }

  @Required
  public void setMciApi(MiraklCatalogIntegrationFrontApi mciApi) {
    this.mciApi = mciApi;
  }

  @Required
  public void setMmpApi(MiraklMarketplacePlatformFrontApi mmpApi) {
    this.mmpApi = mmpApi;
  }

  @Required
  public void setExportStatuses(Map<STATUS, MiraklExportStatus> exportStatuses) {
    this.exportStatuses = exportStatuses;
  }

  @Required
  public void setReportPopulator(Populator<RESULT, MiraklJobReportModel> reportPopulator) {
    this.reportPopulator = reportPopulator;
  }

}
