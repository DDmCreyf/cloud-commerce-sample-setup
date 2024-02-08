package com.mirakl.hybris.core.product.jobs;

import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.jobs.AbstractJobPerformableWithStrategies;
import com.mirakl.hybris.core.model.MiraklDownloadProductFilesCronjobModel;

public class MiraklDownloadProductFilesJob
    extends AbstractJobPerformableWithStrategies<MiraklCatalogSystem, MiraklDownloadProductFilesCronjobModel> {

  @Override
  protected MiraklCatalogSystem getStrategyKey(MiraklDownloadProductFilesCronjobModel cronJob) {
    return cronJob.getProductimportCronJob().getCatalogVersion().getCatalog().getMiraklCatalogSystem();
  }
}
