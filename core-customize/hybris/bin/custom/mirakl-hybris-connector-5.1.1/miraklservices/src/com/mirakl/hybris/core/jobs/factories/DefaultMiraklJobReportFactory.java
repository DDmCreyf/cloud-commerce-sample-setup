package com.mirakl.hybris.core.jobs.factories;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.jobs.factories.impl.MiraklJobReportFactory;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMiraklJobReportFactory implements MiraklJobReportFactory {

  protected Class<?> defaultReportClassType;
  protected Map<MiraklExportType, Class<?>> reportClassTypes;
  protected ModelService modelService;

  @Override
  public <T extends MiraklJobReportModel> T createMiraklJobReport(MiraklExportType miraklExportType) {
    if (reportClassTypes.containsKey(miraklExportType)) {
      return modelService.create(reportClassTypes.get(miraklExportType));
    }
    return modelService.create(defaultReportClassType);
  }

  @Required
  public void setDefaultReportClassType(Class<?> defaultReportClassType) {
    this.defaultReportClassType = defaultReportClassType;
  }

  @Required
  public void setReportClassTypes(Map<MiraklExportType, Class<?>> reportClassTypes) {
    this.reportClassTypes = reportClassTypes;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}
