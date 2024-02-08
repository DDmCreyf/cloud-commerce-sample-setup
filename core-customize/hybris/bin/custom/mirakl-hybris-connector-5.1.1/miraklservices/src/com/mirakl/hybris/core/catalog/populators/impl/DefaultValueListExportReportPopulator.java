package com.mirakl.hybris.core.catalog.populators.impl;

import java.io.File;

import com.mirakl.client.mci.front.domain.value.list.MiraklValueListImportResult;
import com.mirakl.client.mci.front.request.value.list.MiraklValueListImportErrorReportRequest;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public class DefaultValueListExportReportPopulator
    extends AbstractMiraklCatalogExportReportPopulator<MiraklValueListImportResult> {

  @Override
  protected File getReport(MiraklValueListImportResult result, MiraklJobReportModel target) {
    return mciApi.getValueListImportErrorReport(new MiraklValueListImportErrorReportRequest(target.getJobId()));
  }

}
