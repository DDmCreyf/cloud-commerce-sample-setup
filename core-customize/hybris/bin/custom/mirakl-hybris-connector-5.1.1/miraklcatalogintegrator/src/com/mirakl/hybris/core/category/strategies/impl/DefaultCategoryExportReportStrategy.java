package com.mirakl.hybris.core.category.strategies.impl;

import static com.mirakl.client.core.internal.util.Preconditions.checkArgument;
import static com.mirakl.hybris.core.enums.MiraklExportType.COMMISSION_CATEGORY_EXPORT;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.List;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mmp.domain.category.synchro.MiraklCategorySynchroResult;
import com.mirakl.client.mmp.request.catalog.category.MiraklCategorySynchroStatusRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.strategies.impl.AbstractExportReportStrategy;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public class DefaultCategoryExportReportStrategy
    extends AbstractExportReportStrategy<MiraklCategorySynchroResult, MiraklProcessTrackingStatus> {

  @Override
  protected MiraklCategorySynchroResult getExportResult(String syncJobId) {
    checkArgument(isNotBlank(syncJobId), "Category export job id cannot be blank");

    return mmpApi.getCategorySynchroResult(new MiraklCategorySynchroStatusRequest(syncJobId));
  }

  @Override
  protected boolean isExportCompleted(MiraklCategorySynchroResult exportResult) {
    return !MiraklExportStatus.PENDING.equals(exportStatuses.get(exportResult.getStatus()));
  }

  @Override
  protected List<MiraklJobReportModel> getPendingMiraklJobReports() {
    return miraklJobReportDao.findPendingJobReportsForType(COMMISSION_CATEGORY_EXPORT);
  }
}
