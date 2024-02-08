package com.mirakl.hybris.core.catalog.strategies.impl;


import static com.mirakl.client.core.internal.util.Preconditions.checkArgument;
import static com.mirakl.hybris.core.enums.MiraklExportType.ATTRIBUTE_EXPORT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mci.front.domain.attribute.MiraklAttributeImportResult;
import com.mirakl.client.mci.front.request.attribute.MiraklAttributeImportStatusRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.strategies.impl.AbstractExportReportStrategy;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public class DefaultAttributeExportReportStrategy
    extends AbstractExportReportStrategy<MiraklAttributeImportResult, MiraklProcessTrackingStatus> {

  @Override
  protected MiraklAttributeImportResult getExportResult(String syncJobId) {
    checkArgument(isNotBlank(syncJobId), "Attribute export job id cannot be blank");

    return mciApi.getAttributeImportResult(new MiraklAttributeImportStatusRequest(syncJobId));
  }

  @Override
  protected boolean isExportCompleted(MiraklAttributeImportResult exportResult) {
    return !MiraklExportStatus.PENDING.equals(exportStatuses.get(exportResult.getImportStatus()));
  }

  @Override
  protected List<MiraklJobReportModel> getPendingMiraklJobReports() {
    return miraklJobReportDao.findPendingJobReportsForType(ATTRIBUTE_EXPORT);
  }

}
