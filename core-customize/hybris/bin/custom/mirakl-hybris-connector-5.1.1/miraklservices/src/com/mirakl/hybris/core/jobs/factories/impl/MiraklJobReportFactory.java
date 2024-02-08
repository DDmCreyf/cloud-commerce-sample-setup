package com.mirakl.hybris.core.jobs.factories.impl;

import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public interface MiraklJobReportFactory {

  <T extends MiraklJobReportModel> T createMiraklJobReport(MiraklExportType miraklExportType);

}
