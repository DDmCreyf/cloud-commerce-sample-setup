package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklcatalogmanagerConstants.PRODUCTS_DATASHEETS_EXPORT_MAX_PRODUCTS_PER_FILE;
import static com.mirakl.hybris.core.enums.MiraklExportType.PRODUCT_DATASHEET_EXPORT;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncItem;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncTracking;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncRequest;
import com.mirakl.client.mci.request.product.MiraklProductDataSheetSyncRequest.MiraklProductDataSheetSyncRequestBuilder;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.model.MiraklExportProductDataSheetJobReportModel;
import com.mirakl.hybris.core.product.strategies.McmProductExportStrategy;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMcmProductExportStrategy implements McmProductExportStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultMcmProductExportStrategy.class);

  protected ConfigurationService configurationService;
  protected ExportJobReportService exportJobReportService;
  protected ModelService modelService;
  protected MiraklCatalogIntegrationFrontApi mciFrontApi;
  protected Converter<Pair<ProductModel, ProductDataSheetExportContextData>, MiraklProductDataSheetSyncItem> productDataSheetSyncItemConverter;

  @Override
  public int exportProductDataSheets(Collection<ProductModel> products, ProductDataSheetExportContextData context)
      throws IOException {
    if (isEmpty(products)) {
      return 0;
    }

    int exportedProducts = 0;
    int maximumProductsPerFile = getMaximumProductsPerFile();
    MiraklProductDataSheetSyncRequestBuilder requestBuilder = MiraklProductDataSheetSyncRequest.builder();

    for (ProductModel product : products) {
      try {
        requestBuilder.addProduct(productDataSheetSyncItemConverter.convert(Pair.of(product, context)));
        exportedProducts++;
        if (exportedProducts % maximumProductsPerFile == 0) {
          requestBuilder = exportPage(requestBuilder, context);
        }
      } catch (Exception e) {
        LOG.error(format("Failed to convert product [%s] for export", product.getCode()), e);
      }
    }

    if (exportedProducts % maximumProductsPerFile != 0) {
      // Export last page if any pending products
      exportPage(requestBuilder, context);
    }
    return exportedProducts;
  }

  protected MiraklProductDataSheetSyncRequestBuilder exportPage(MiraklProductDataSheetSyncRequestBuilder requestBuilder,
      ProductDataSheetExportContextData context) throws IOException {
    MiraklProductDataSheetSyncRequestBuilder builder = requestBuilder;
    try {
      sendRequest(requestBuilder.build(), modelService.get(context.getProductCatalogVersion()));
    } finally {
      builder = MiraklProductDataSheetSyncRequest.builder();
    }
    return builder;
  }

  @Override
  public MiraklProductDataSheetSyncTracking sendRequest(MiraklProductDataSheetSyncRequest request,
      CatalogVersionModel catalogVersion) {
    MiraklProductDataSheetSyncTracking tracking = mciFrontApi.synchronizeProductDataSheets(request);
    createJobReport(tracking, catalogVersion);
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Exporting products to Mirakl through file [%s]. Tracking id=[%s]", request.getFile(),
          tracking.getTrackingId()));
    }
    return tracking;
  }

  protected void createJobReport(MiraklProductDataSheetSyncTracking tracking, CatalogVersionModel catalogVersion) {
    MiraklExportProductDataSheetJobReportModel jobReport =
        exportJobReportService.createMiraklJobReport(tracking.getTrackingId(), PRODUCT_DATASHEET_EXPORT);
    jobReport.setCatalogVersion(catalogVersion);
    modelService.save(jobReport);
  }

  protected int getMaximumProductsPerFile() {
    return configurationService.getConfiguration().getInt(PRODUCTS_DATASHEETS_EXPORT_MAX_PRODUCTS_PER_FILE);
  }

  @Required
  public void setMciFrontApi(MiraklCatalogIntegrationFrontApi mciFrontApi) {
    this.mciFrontApi = mciFrontApi;
  }

  @Required
  public void setProductDataSheetSyncItemConverter(
      Converter<Pair<ProductModel, ProductDataSheetExportContextData>, MiraklProductDataSheetSyncItem> productDataSheetSyncItemConverter) {
    this.productDataSheetSyncItemConverter = productDataSheetSyncItemConverter;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setExportJobReportService(ExportJobReportService exportJobReportService) {
    this.exportJobReportService = exportJobReportService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}
