package com.mirakl.hybris.core.catalog.strategies.impl;


import static com.mirakl.client.core.internal.util.Preconditions.checkArgument;
import static com.mirakl.hybris.core.enums.MiraklExportType.VALUE_LIST_EXPORT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mci.front.domain.value.list.MiraklValueListImportResult;
import com.mirakl.client.mci.front.request.value.list.MiraklValueListImportStatusRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.strategies.impl.AbstractExportReportStrategy;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public class DefaultValueListExportReportStrategy
    extends AbstractExportReportStrategy<MiraklValueListImportResult, MiraklProcessTrackingStatus> {

  @Override
  protected MiraklValueListImportResult getExportResult(String syncJobId) {
    checkArgument(isNotBlank(syncJobId), "Value List export job id cannot be blank");

    return mciApi.getValueListImportResult(new MiraklValueListImportStatusRequest(syncJobId));
  }

  @Override
  protected boolean isExportCompleted(MiraklValueListImportResult exportResult) {
    return !MiraklExportStatus.PENDING.equals(exportStatuses.get(exportResult.getImportStatus()));
  }

  @Override
  protected List<MiraklJobReportModel> getPendingMiraklJobReports() {
    return miraklJobReportDao.findPendingJobReportsForType(VALUE_LIST_EXPORT);
  }

}
