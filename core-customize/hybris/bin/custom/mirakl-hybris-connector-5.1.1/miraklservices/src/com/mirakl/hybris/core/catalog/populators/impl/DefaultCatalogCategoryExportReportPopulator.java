package com.mirakl.hybris.core.catalog.populators.impl;

import java.io.File;

import com.mirakl.client.mci.front.domain.hierarchy.MiraklHierarchyImportResult;
import com.mirakl.client.mci.front.request.hierarchy.MiraklHierarchyImportErrorReportRequest;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public class DefaultCatalogCategoryExportReportPopulator
    extends AbstractMiraklCatalogExportReportPopulator<MiraklHierarchyImportResult> {

  @Override
  protected File getReport(MiraklHierarchyImportResult result, MiraklJobReportModel target) {
    return mciApi.getHierarchiyImportErrorReport(new MiraklHierarchyImportErrorReportRequest(target.getJobId()));
  }

}
