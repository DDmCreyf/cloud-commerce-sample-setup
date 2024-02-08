package com.mirakl.hybris.core.catalog.strategies;

import java.io.IOException;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;

public interface PrepareCatalogExportStrategy {

  /**
   * Performs a custom logic before starting the catalog export
   * 
   * @param context the export context
   * @throws IOException
   */
  void prepareExport(MiraklExportCatalogContext context) throws IOException;
}
