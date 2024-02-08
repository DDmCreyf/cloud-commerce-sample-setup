package com.mirakl.hybris.core.catalog.populators.impl;

import java.io.File;

import com.mirakl.client.mci.front.domain.attribute.MiraklAttributeImportResult;
import com.mirakl.client.mci.front.request.attribute.MiraklAttributeImportErrorReportRequest;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public class DefaultAttributeExportReportPopulator
    extends AbstractMiraklCatalogExportReportPopulator<MiraklAttributeImportResult> {

  @Override
  protected File getReport(MiraklAttributeImportResult result, MiraklJobReportModel target) {
    return mciApi.getAttributeImportErrorReport(new MiraklAttributeImportErrorReportRequest(target.getJobId()));
  }

}
