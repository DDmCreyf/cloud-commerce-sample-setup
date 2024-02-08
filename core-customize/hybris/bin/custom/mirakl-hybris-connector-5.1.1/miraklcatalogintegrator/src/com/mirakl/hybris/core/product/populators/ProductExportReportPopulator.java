package com.mirakl.hybris.core.product.populators;

import java.io.File;
import java.io.IOException;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mmp.domain.product.synchro.MiraklProductSynchroResult;
import com.mirakl.client.mmp.request.catalog.product.MiraklProductSynchroErrorReportRequest;
import com.mirakl.hybris.core.jobs.populators.AbstractMiraklJobReportPopulator;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ProductExportReportPopulator extends
    AbstractMiraklJobReportPopulator<MiraklProductSynchroResult, MiraklProcessTrackingStatus, File, MiraklJobReportModel> {

  @Override
  protected void populateFromResult(MiraklProductSynchroResult result, MiraklJobReportModel target) throws ConversionException {
    target.setLinesRead(result.getLinesRead());
    target.setLinesInError(result.getLinesInError());
    target.setLinesInSuccess(result.getLinesInSuccess());
    target.setStatus(exportStatuses.get(result.getStatus()));
    target.setItemsDeleted(result.getProductDeleted());
    target.setItemsInserted(result.getProductInserted());
    target.setItemsUpdated(result.getProductUpdated());
    target.setHasErrorReport(result.hasErrorReport());
    target.setCreationDate(result.getDateCreated());
  }

  @Override
  protected File getReport(MiraklProductSynchroResult result, MiraklJobReportModel target) {
    return mmpApi.getProductSynchroErrorReport(new MiraklProductSynchroErrorReportRequest(target.getJobId()));
  }

  @Override
  protected File getErrorReportFile(MiraklProductSynchroResult result, File report) throws IOException {
    return report;
  }

}
