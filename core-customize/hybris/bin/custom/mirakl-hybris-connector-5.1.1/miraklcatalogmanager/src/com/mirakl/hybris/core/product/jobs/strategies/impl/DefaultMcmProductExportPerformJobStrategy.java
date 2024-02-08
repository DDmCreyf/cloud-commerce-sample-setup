package com.mirakl.hybris.core.product.jobs.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;
import com.mirakl.hybris.core.product.services.McmProductExportService;
import com.mirakl.hybris.core.product.strategies.PerformJobStrategy;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMcmProductExportPerformJobStrategy implements PerformJobStrategy<MiraklExportSellableProductsCronJobModel> {

  private static final Logger LOG = Logger.getLogger(DefaultMcmProductExportPerformJobStrategy.class);

  protected McmProductExportService productExportService;
  protected ModelService modelService;
  protected Converter<MiraklExportSellableProductsCronJobModel, ProductDataSheetExportContextData> productDataSheetExportContextDataConverter;

  @Override
  public PerformResult perform(MiraklExportSellableProductsCronJobModel cronJob) {
    LOG.info("Started exporting products into MCM..");

    validate(cronJob);

    try {
      ProductDataSheetExportContextData context = productDataSheetExportContextDataConverter.convert(cronJob);
      int exportedProductsCount = productExportService.exportProductDataSheets(context);
      LOG.info(format("Product export finished. Synchronized [%s] products.", exportedProductsCount));
      cronJob.setLastExportDate(cronJob.getStartTime());
      modelService.save(cronJob);

      return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

    } catch (Exception e) {
      LOG.error("Exception occurred while exporting products", e);
      return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
    }
  }

  protected void validate(MiraklExportSellableProductsCronJobModel cronJob) {
    if (cronJob.getCatalogVersion() == null) {
      throw new IllegalStateException("Catalog Version must be provided");
    }

    CatalogModel catalog = cronJob.getCatalogVersion().getCatalog();

    if (!MiraklCatalogSystem.MCM.equals(catalog.getMiraklCatalogSystem())) {
      throw new IllegalStateException(format("Catalog [%s] is not configured to use the MCM catalog system", catalog.getName()));
    }
    if (isEmpty(cronJob.getProductOrigins())) {
      throw new IllegalStateException("You must provide at least one origin for the products to export");
    }
  }

  @Required
  public void setProductExportService(McmProductExportService productExportService) {
    this.productExportService = productExportService;
  }

  @Required
  public void setProductDataSheetExportContextDataConverter(
      Converter<MiraklExportSellableProductsCronJobModel, ProductDataSheetExportContextData> productDataSheetExportContextDataConverter) {
    this.productDataSheetExportContextDataConverter = productDataSheetExportContextDataConverter;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}
