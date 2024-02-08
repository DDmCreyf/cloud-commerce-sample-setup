package com.mirakl.hybris.core.product.jobs;

import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.jobs.AbstractJobPerformableWithStrategies;
import com.mirakl.hybris.core.model.MiraklProductImportCronJobModel;

public class MiraklProductImportJob
    extends AbstractJobPerformableWithStrategies<MiraklCatalogSystem, MiraklProductImportCronJobModel> {

  @Override
  protected MiraklCatalogSystem getStrategyKey(MiraklProductImportCronJobModel cronJob) {
    return cronJob.getCatalogVersion().getCatalog().getMiraklCatalogSystem();
  }

}
