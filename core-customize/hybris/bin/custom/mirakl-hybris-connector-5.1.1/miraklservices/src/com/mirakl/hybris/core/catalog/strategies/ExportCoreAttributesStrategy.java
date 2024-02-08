package com.mirakl.hybris.core.catalog.strategies;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;

public interface ExportCoreAttributesStrategy {

  /**
   * Exports product core attributes (non classification attributes)
   * 
   * @param context the export context
   */
  void exportCoreAttributes(MiraklExportCatalogContext context);

}
