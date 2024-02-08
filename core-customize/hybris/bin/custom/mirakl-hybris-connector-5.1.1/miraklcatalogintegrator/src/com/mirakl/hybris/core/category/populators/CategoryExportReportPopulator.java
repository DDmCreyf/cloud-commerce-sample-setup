package com.mirakl.hybris.core.category.populators;

import java.io.File;
import java.io.IOException;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mmp.domain.category.synchro.MiraklCategorySynchroResult;
import com.mirakl.client.mmp.request.catalog.category.MiraklCategorySynchroErrorReportRequest;
import com.mirakl.hybris.core.jobs.populators.AbstractMiraklJobReportPopulator;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class CategoryExportReportPopulator extends
    AbstractMiraklJobReportPopulator<MiraklCategorySynchroResult, MiraklProcessTrackingStatus, File, MiraklJobReportModel> {

  @Override
  protected void populateFromResult(MiraklCategorySynchroResult result, MiraklJobReportModel target) throws ConversionException {
    target.setLinesRead(result.getLinesRead());
    target.setLinesInError(result.getLinesInError());
    target.setLinesInSuccess(result.getLinesInSuccess());
    target.setStatus(exportStatuses.get(result.getStatus()));
    target.setItemsDeleted(result.getCategoryDeleted());
    target.setItemsInserted(result.getCategoryInserted());
    target.setItemsUpdated(result.getCategoryUpdated());
    target.setHasErrorReport(result.hasErrorReport());
    target.setCreationDate(result.getDateCreated());
  }

  @Override
  protected File getReport(MiraklCategorySynchroResult result, MiraklJobReportModel target) {
    return mmpApi.getCategorySynchroErrorReport(new MiraklCategorySynchroErrorReportRequest(target.getJobId()));
  }

  @Override
  protected File getErrorReportFile(MiraklCategorySynchroResult result, File report) throws IOException {
    return report;
  }
}
