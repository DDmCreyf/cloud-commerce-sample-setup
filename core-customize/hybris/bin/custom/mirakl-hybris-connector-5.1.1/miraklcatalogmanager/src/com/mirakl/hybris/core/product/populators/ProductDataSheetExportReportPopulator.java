package com.mirakl.hybris.core.product.populators;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.internal.MiraklStream;
import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetIntegrationGlobalError;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncDetail;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncReport;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncResult;
import com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncReportRequest;
import com.mirakl.hybris.core.jobs.populators.AbstractMiraklJobReportPopulator;
import com.mirakl.hybris.core.model.MiraklExportProductDataSheetJobReportModel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import shaded.com.fasterxml.jackson.core.JsonFactory;
import shaded.com.fasterxml.jackson.core.JsonGenerator;
import shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class ProductDataSheetExportReportPopulator extends
    AbstractMiraklJobReportPopulator<MiraklProductDataSheetSyncResult, MiraklProcessTrackingStatus, MiraklProductDataSheetSyncReport, MiraklExportProductDataSheetJobReportModel> {

  private static final Logger LOG = Logger.getLogger(ProductDataSheetExportReportPopulator.class);

  protected ProductService productService;
  protected ObjectMapper objectMapper;
  protected Converter<MiraklProductDataSheetSyncDetail, MiraklProductDataSheetSyncDetail> syncDetailErrorConverter;

  @Override
  protected void populateFromResult(MiraklProductDataSheetSyncResult result, MiraklExportProductDataSheetJobReportModel target)
      throws ConversionException {
    target.setLinesRead(result.getProcessed());
    target.setLinesInError(result.getFailed());
    target.setLinesInSuccess(result.getSucceeded());
    target.setStatus(exportStatuses.get(result.getStatus()));
  }

  @Override
  protected boolean shouldDownloadReport(MiraklProductDataSheetSyncResult result,
      MiraklExportProductDataSheetJobReportModel target) {
    return true;
  }

  @Override
  protected MiraklProductDataSheetSyncReport getReport(MiraklProductDataSheetSyncResult result,
      MiraklExportProductDataSheetJobReportModel target) {
    return mciApi.getProductDataSheetsSynchronizationReport(new MiraklProductDataSheetSyncReportRequest(target.getJobId()));
  }

  @Override
  protected void populateFromReport(MiraklProductDataSheetSyncResult result, MiraklProductDataSheetSyncReport report,
      MiraklExportProductDataSheetJobReportModel target) {
    updateMiraklProductIds(report, target);
    setErrorReport(result, report, target);
  }

  protected void updateMiraklProductIds(MiraklProductDataSheetSyncReport report,
      MiraklExportProductDataSheetJobReportModel target) {

    List<ProductModel> productsToSave = new ArrayList<>();
    for (MiraklProductDataSheetSyncDetail item : report.getProcessedItems()) {
      if (isNotBlank(item.getMiraklProductId())) {
        try {
          ProductModel product = productService.getProductForCode(target.getCatalogVersion(), item.getProductSku());
          updateMiraklProductId(product, item.getMiraklProductId(), productsToSave);
        } catch (UnknownIdentifierException e) {
          LOG.error(format("Unable to find product [%s] to update its miraklProductId value. Maybe it has been deleted.",
              item.getProductSku()), e);
        }
      }
    }
    if (isNotEmpty(productsToSave)) {
      if (LOG.isDebugEnabled()) {
        LOG.debug(format("Updated [%s] miraklProductId references for import id [%s]", productsToSave.size(), target.getJobId()));
      }
      modelService.saveAll(productsToSave);
    }
  }

  protected void updateMiraklProductId(ProductModel product, String miraklProductId, List<ProductModel> productsToSave) {
    if (!miraklProductId.equals(product.getMiraklProductId())) {
      if (isNotBlank(product.getMiraklProductId())) {
        LOG.warn(format("Overwriting an existing miraklProductId. Previous value=[%s], new value=[%s]",
            product.getMiraklProductId(), miraklProductId));
      }
      product.setMiraklProductId(miraklProductId);
      productsToSave.add(product);
    }
  }

  @Override
  protected void setErrorReport(MiraklProductDataSheetSyncResult source, MiraklProductDataSheetSyncReport jobReport,
      MiraklExportProductDataSheetJobReportModel target) {
    try {
      File errorReportFile = getErrorReportFile(source, jobReport);
      target.setHasErrorReport(errorReportFile != null);
      if (errorReportFile != null) {
        LOG.warn(format("Export with syncJobId [%s] ended with errors - setting error report", target.getJobId()));
        target.setErrorReport(createErrorReport(errorReportFile, target.getJobId(), target.getReportType()));
      }
    } catch (IOException e) {
      LOG.error("Exception occurred while setting error report", e);
    }
  }

  @Override
  protected File getErrorReportFile(MiraklProductDataSheetSyncResult result, MiraklProductDataSheetSyncReport report)
      throws IOException {
    File tempFile = File.createTempFile(format("error-report-%s", result.getTrackingId()), ".json");
    try (JsonGenerator jsonGenerator =
        new JsonFactory().createJsonGenerator(new FileOutputStream(tempFile)).setCodec(objectMapper)) {
      jsonGenerator.writeStartObject();
      boolean hasErrors = writeGlobalErrors(report, jsonGenerator);
      hasErrors |= writeItemsSyncErrors(report, jsonGenerator);

      if (!hasErrors) {
        deleteQuietly(tempFile);
        return null;
      }
      jsonGenerator.writeEndObject();
      return tempFile;
    }

  }

  protected boolean writeGlobalErrors(MiraklProductDataSheetSyncReport report, JsonGenerator jsonGenerator) throws IOException {
    boolean hasErrors = false;
    MiraklStream<MiraklProductDataSheetIntegrationGlobalError> globalErrors = report.getGlobalErrors();
    if (globalErrors.iterator().hasNext()) {
      hasErrors = true;
      jsonGenerator.writeArrayFieldStart("global_errors");
      for (MiraklProductDataSheetIntegrationGlobalError error : globalErrors) {
        jsonGenerator.writeObject(error);
      }
      jsonGenerator.writeEndArray();
    }
    return hasErrors;
  }

  protected boolean writeItemsSyncErrors(MiraklProductDataSheetSyncReport report, JsonGenerator jsonGenerator)
      throws IOException {
    boolean hasErrors = false;
    MiraklStream<MiraklProductDataSheetSyncDetail> processedItems = report.getProcessedItems();
    for (MiraklProductDataSheetSyncDetail item : processedItems) {
      if (isNotEmpty(item.getSynchronizationErrors()) || isNotEmpty(item.getIntegrationErrors())) {
        if (!hasErrors) {
          hasErrors = true;
          jsonGenerator.writeArrayFieldStart("items_in_error");
        }
        jsonGenerator.writeObject(syncDetailErrorConverter.convert(item));
      }
    }

    if (hasErrors) {
      jsonGenerator.writeEndArray();
    }

    return hasErrors;
  }

  @Required
  public void setProductService(ProductService productService) {
    this.productService = productService;
  }

  @Required
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Required
  public void setSyncDetailErrorConverter(
      Converter<MiraklProductDataSheetSyncDetail, MiraklProductDataSheetSyncDetail> syncDetailErrorConverter) {
    this.syncDetailErrorConverter = syncDetailErrorConverter;
  }

}
