package com.mirakl.hybris.core.jobs.services;

import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.jobs.services.impl.DefaultExportJobReportService;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public interface ExportJobReportService {

  /**
   * Creates and saves a job report of the given type
   *
   * @param jobId The id of the job
   * @param miraklExportType The type of report to create (product export, attributes export,...)
   * @return The model of the new job report
   */
  <T extends MiraklJobReportModel> T createMiraklJobReport(String jobId, MiraklExportType miraklExportType);

  /**
   * Updates all the reports in progress, calling the {@link DefaultExportJobReportService#exportTypeStrategies}
   *
   * @return true if all the reports were updated successfully, false otherwise.
   */
  boolean updatePendingExportReports();
}
