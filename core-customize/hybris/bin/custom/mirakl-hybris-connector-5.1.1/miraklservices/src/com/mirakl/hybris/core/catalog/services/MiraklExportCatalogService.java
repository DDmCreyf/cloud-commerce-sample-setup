package com.mirakl.hybris.core.catalog.services;

import java.io.IOException;
import java.util.Locale;

import com.mirakl.hybris.beans.MiraklExportCatalogResultData;

import de.hybris.platform.category.model.CategoryModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
public interface MiraklExportCatalogService {

  /**
   * Exports the Hybris catalog to Mirakl. Depending on a given configuration, it is able to export Catalog Categories, Attributes
   * or/and Value Lists.
   * 
   * @param context the export context
   * @return the result containing the tracking ids of the different exports
   * @throws IOException
   */
  MiraklExportCatalogResultData export(MiraklExportCatalogContext context) throws IOException;

  /**
   * Returns the formatted value of an attribute. ie: 'description' -> 'description [en]'
   * The localization pattern is defined in the mirakl.catalog.export.localizedattributepattern property
   *
   * @param value the value to format
   * @param locale the locale, can be null
   * @return the formatted value, or the value itself if no locale was provided
   */
  String formatAttributeExportName(String value, Locale locale);

  /**
   * Checks if a category is root and if it should therefore be ignored
   * 
   * @param category the category to check
   * @param context the catalog export context
   * @return true if the category is root and that the export context disables the export of the root level
   */
  boolean isRootAndIgnoredCategory(CategoryModel category, MiraklExportCatalogContext context);


  /**
   * Returns the category code to export
   * 
   * @param category the category to export
   * @param context the export context
   * @return the category code if the category is not ignored, an empty string if the category is root and ignored
   */
  String getCategoryExportCode(CategoryModel category, MiraklExportCatalogContext context);
}
