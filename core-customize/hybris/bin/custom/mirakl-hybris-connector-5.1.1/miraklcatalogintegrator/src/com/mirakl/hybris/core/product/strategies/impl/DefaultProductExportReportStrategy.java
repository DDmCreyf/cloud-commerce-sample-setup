package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.client.core.internal.util.Preconditions.checkArgument;
import static com.mirakl.hybris.core.enums.MiraklExportType.PRODUCT_EXPORT;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.List;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mmp.domain.product.synchro.MiraklProductSynchroResult;
import com.mirakl.client.mmp.request.catalog.product.MiraklProductSynchroStatusRequest;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.jobs.strategies.impl.AbstractExportReportStrategy;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

public class DefaultProductExportReportStrategy
    extends AbstractExportReportStrategy<MiraklProductSynchroResult, MiraklProcessTrackingStatus> {

  @Override
  protected MiraklProductSynchroResult getExportResult(String syncJobId) {
    checkArgument(isNotBlank(syncJobId), "Product export job id cannot be blank");

    return mmpApi.getProductSynchroResult(new MiraklProductSynchroStatusRequest(syncJobId));
  }

  @Override
  protected boolean isExportCompleted(MiraklProductSynchroResult exportResult) {
    return !MiraklExportStatus.PENDING.equals(exportStatuses.get(exportResult.getStatus()));
  }

  @Override
  protected List<MiraklJobReportModel> getPendingMiraklJobReports() {
    return miraklJobReportDao.findPendingJobReportsForType(PRODUCT_EXPORT);
  }

}
