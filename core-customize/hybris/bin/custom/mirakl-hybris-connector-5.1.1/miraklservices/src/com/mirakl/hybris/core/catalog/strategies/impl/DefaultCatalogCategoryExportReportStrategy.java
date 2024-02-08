package com.mirakl.hybris.core.catalog.strategies.impl;


import static com.mirakl.client.core.internal.util.Preconditions.checkArgument;
import static com.mirakl.hybris.core.enums.MiraklExportType.CATALOG_CATEGORY_EXPORT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mci.front.domain.hierarchy.MiraklHierarchyImportResult;
import com.mirakl.client.mci.front.request.hierarchy.MiraklHierarchyImportStatusRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.strategies.impl.AbstractExportReportStrategy;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public class DefaultCatalogCategoryExportReportStrategy
    extends AbstractExportReportStrategy<MiraklHierarchyImportResult, MiraklProcessTrackingStatus> {

  @Override
  protected MiraklHierarchyImportResult getExportResult(String syncJobId) {
    checkArgument(isNotBlank(syncJobId), "Hierarchy export job id cannot be blank");

    return mciApi.getHierarchyImportResult(new MiraklHierarchyImportStatusRequest(syncJobId));
  }

  @Override
  protected boolean isExportCompleted(MiraklHierarchyImportResult exportResult) {
    return !MiraklExportStatus.PENDING.equals(exportStatuses.get(exportResult.getImportStatus()));
  }

  @Override
  protected List<MiraklJobReportModel> getPendingMiraklJobReports() {
    return miraklJobReportDao.findPendingJobReportsForType(CATALOG_CATEGORY_EXPORT);
  }

}
