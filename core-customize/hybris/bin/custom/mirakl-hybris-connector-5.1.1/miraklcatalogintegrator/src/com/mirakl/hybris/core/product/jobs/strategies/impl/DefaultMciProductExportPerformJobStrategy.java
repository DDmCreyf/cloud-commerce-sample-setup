package com.mirakl.hybris.core.product.jobs.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;
import com.mirakl.hybris.core.product.services.MciProductExportService;
import com.mirakl.hybris.core.product.strategies.PerformJobStrategy;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultMciProductExportPerformJobStrategy implements PerformJobStrategy<MiraklExportSellableProductsCronJobModel> {

  private static final Logger LOG = Logger.getLogger(DefaultMciProductExportPerformJobStrategy.class);

  protected MciProductExportService productExportService;
  protected ModelService modelService;

  @Override
  public PerformResult perform(MiraklExportSellableProductsCronJobModel cronJob) {
    LOG.info("Started exporting products into MCI..");

    validate(cronJob);

    int exportedProductsCount = 0;
    try {
      String synchronizationFileName = isNotBlank(cronJob.getSynchronizationFileName()) ? cronJob.getSynchronizationFileName()
          : getDefaultSynchronizationFileName(cronJob);
      if (cronJob.isFullExport() || cronJob.getLastExportDate() == null) {
        exportedProductsCount = productExportService.exportAllProducts(cronJob.getRootCategory(), cronJob.getRootBrandCategory(),
            cronJob.getBaseSite(), synchronizationFileName);
      } else {
        exportedProductsCount = productExportService.exportModifiedProducts(cronJob.getRootCategory(),
            cronJob.getRootBrandCategory(), cronJob.getBaseSite(), cronJob.getLastExportDate(), synchronizationFileName);
      }
    } catch (MiraklApiException | IOException e) {
      LOG.error("Exception occurred while exporting products", e);
      return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
    }

    LOG.info(format("Product export finished successfully. %s products synchronized.", exportedProductsCount));
    cronJob.setLastExportDate(cronJob.getStartTime());
    modelService.save(cronJob);

    return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
  }

  protected String getDefaultSynchronizationFileName(MiraklExportSellableProductsCronJobModel cronJob) {
    CatalogModel catalog = cronJob.getRootCategory().getCatalogVersion().getCatalog();
    return format("%s-%s-%s", catalog.getId(), catalog.getVersion(), cronJob.isFullExport() ? "full" : "incremental");
  }

  protected void validate(MiraklExportSellableProductsCronJobModel cronJob) {
    if (cronJob.getRootCategory() == null) {
      throw new IllegalStateException("Root Category must be provided");
    }

    CatalogModel catalog = cronJob.getRootCategory().getCatalogVersion().getCatalog();
    if (catalog.getMiraklCatalogSystem() != null && !MiraklCatalogSystem.MCI.equals(catalog.getMiraklCatalogSystem())) {
      throw new IllegalStateException(format("Catalog [%s] is not configured to use the MCI catalog system", catalog.getName()));
    }
  }

  @Required
  public void setProductExportService(MciProductExportService productExportService) {
    this.productExportService = productExportService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }


}
