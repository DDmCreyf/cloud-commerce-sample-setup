package com.mirakl.hybris.core.catalog.populators.impl;

import java.io.File;
import java.io.IOException;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mci.front.domain.common.AbstractMiraklCatalogImportResult;
import com.mirakl.hybris.core.jobs.populators.AbstractMiraklJobReportPopulator;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public abstract class AbstractMiraklCatalogExportReportPopulator<RESULT extends AbstractMiraklCatalogImportResult>
    extends AbstractMiraklJobReportPopulator<RESULT, MiraklProcessTrackingStatus, File, MiraklJobReportModel> {

  @Override
  protected void populateFromResult(RESULT result, MiraklJobReportModel target) throws ConversionException {
    target.setStatus(exportStatuses.get(result.getImportStatus()));
    target.setHasErrorReport(result.hasErrorReport());
  }

  @Override
  protected File getErrorReportFile(RESULT result, File report) throws IOException {
    return report;
  }

}
