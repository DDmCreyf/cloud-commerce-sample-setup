package com.mirakl.hybris.core.jobs.services.impl;

import static com.mirakl.client.core.internal.util.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.jobs.factories.impl.MiraklJobReportFactory;
import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.jobs.strategies.ExportReportStrategy;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultExportJobReportService implements ExportJobReportService {

  protected ModelService modelService;
  protected List<ExportReportStrategy> exportTypeStrategies;
  protected MiraklJobReportFactory miraklJobReportFactory;

  @Override
  public <T extends MiraklJobReportModel> T createMiraklJobReport(String jobId, MiraklExportType miraklExportType) {
    checkArgument(isNotBlank(jobId), "Job Id cannot be blank");
    checkArgument(miraklExportType != null, "Job Id cannot be null");

    T miraklJobReport = miraklJobReportFactory.createMiraklJobReport(miraklExportType);
    miraklJobReport.setJobId(jobId);
    miraklJobReport.setReportType(miraklExportType);
    modelService.save(miraklJobReport);

    return miraklJobReport;
  }

  @Override
  public boolean updatePendingExportReports() {
    boolean allExportsProcessedWithoutErrors = true;
    for (ExportReportStrategy exportTypeStrategy : exportTypeStrategies) {
      allExportsProcessedWithoutErrors &= exportTypeStrategy.updatePendingExports();
    }

    return allExportsProcessedWithoutErrors;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setExportTypeStrategies(List<ExportReportStrategy> exportTypeStrategies) {
    this.exportTypeStrategies = exportTypeStrategies;
  }

  @Required
  public void setMiraklJobReportFactory(MiraklJobReportFactory miraklJobReportFactory) {
    this.miraklJobReportFactory = miraklJobReportFactory;
  }


}
