package com.mirakl.hybris.core.category.services;

import com.mirakl.client.mmp.domain.category.synchro.MiraklCategorySynchroTracking;
import de.hybris.platform.category.model.CategoryModel;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

public interface CategoryExportService {

  /**
   * Exports the commission categories to Mirakl
   *
   * @param rootCategory The category at the root of the commission categories tree
   * @param locale The locale of the export
   * @param fileName Name of the generated file containing the categories
   * @return The Mirakl tracking number to get the status of the import un Mirakl
   * @throws IOException When the category file cannot be written
   */
  MiraklCategorySynchroTracking exportCommissionCategories(CategoryModel rootCategory, Locale locale, String fileName)
      throws IOException;

  /**
   * Exports the commission categories to Mirakl
   *
   * @param rootCategory The category at the root of the commission categories tree
   * @param defaultLocale The locale of the export
   * @param fileName Name of the generated file containing the categories
   * @param additionalLocales The additional locales of the export
   * @return The Mirakl tracking number to get the status of the import un Mirakl
   * @throws IOException When the category file cannot be written
   */
  MiraklCategorySynchroTracking exportCommissionCategories(CategoryModel rootCategory, Locale defaultLocale, String fileName,
      Set<Locale> additionalLocales) throws IOException;
}
