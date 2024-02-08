package com.mirakl.hybris.core.product.jobs.strategies.impl;

import com.mirakl.hybris.core.jobs.strategies.ExportProductsCatalogResolutionStrategy;
import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;

import de.hybris.platform.catalog.model.CatalogModel;

public class DefaultMcmExportProductsCatalogResolutionStrategy implements ExportProductsCatalogResolutionStrategy {

  @Override
  public CatalogModel resolveCatalog(MiraklExportSellableProductsCronJobModel cronJob) {
    return cronJob.getCatalogVersion().getCatalog();
  }

}
