package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.enums.MiraklExportType.PRODUCT_DATASHEET_EXPORT;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.List;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncResult;
import com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncResultRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.strategies.impl.AbstractExportReportStrategy;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public class DefaultProductDataSheetExportReportStrategy
    extends AbstractExportReportStrategy<MiraklProductDataSheetSyncResult, MiraklProcessTrackingStatus> {

  @Override
  protected MiraklProductDataSheetSyncResult getExportResult(String syncJobId) {
    if (isBlank(syncJobId)) {
      throw new IllegalArgumentException("Product datasheet export job id cannot be blank");
    }

    return mciApi.getProductDataSheetsSynchronizationResult(new MiraklProductDataSheetSyncResultRequest(syncJobId));
  }

  @Override
  protected boolean isExportCompleted(MiraklProductDataSheetSyncResult exportResult) {
    return !MiraklExportStatus.PENDING.equals(exportStatuses.get(exportResult.getStatus()));
  }

  @Override
  protected List<MiraklJobReportModel> getPendingMiraklJobReports() {
    return miraklJobReportDao.findPendingJobReportsForType(PRODUCT_DATASHEET_EXPORT);
  }

}
