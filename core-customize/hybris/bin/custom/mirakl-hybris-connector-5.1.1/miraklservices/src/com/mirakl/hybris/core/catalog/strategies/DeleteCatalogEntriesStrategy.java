package com.mirakl.hybris.core.catalog.strategies;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface DeleteCatalogEntriesStrategy {

  /**
   * Write the code of the unknown categories and flags them as deleted
   * 
   * @param context the catalog export context
   */
  void writeRemovedCategories(MiraklExportCatalogContext context);

  /**
   * Write the code of the unknown attributes and flags them as deleted
   * 
   * @param context the catalog export context
   */
  void writeRemovedAttributes(MiraklExportCatalogContext context);

  /**
   * Write the code of the unknown values and flags them as deleted
   * 
   * @param context the catalog export context
   */
  void writeRemovedValues(MiraklExportCatalogContext context);
}
