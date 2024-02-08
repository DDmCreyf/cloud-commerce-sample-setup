package com.mirakl.hybris.core.jobs.strategies;

import com.mirakl.hybris.core.model.MiraklExportSellableProductsCronJobModel;

import de.hybris.platform.catalog.model.CatalogModel;

public interface ExportProductsCatalogResolutionStrategy {

  CatalogModel resolveCatalog(MiraklExportSellableProductsCronJobModel cronJob);

}
