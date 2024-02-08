package com.mirakl.hybris.core.product.jobs.strategies.impl;

import com.mirakl.hybris.core.jobs.strategies.ExportProductsCatalogResolutionStrategy;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;

import de.hybris.platform.catalog.model.CatalogModel;

public class DefaultMciExportProductsCatalogResolutionStrategy implements ExportProductsCatalogResolutionStrategy {

  @Override
  public CatalogModel resolveCatalog(MiraklExportSellableProductsCronJobModel cronJob) {
    if (cronJob.getRootCategory() != null) {
      return cronJob.getRootCategory().getCatalogVersion().getCatalog();
    }
    return null;
  }

}
