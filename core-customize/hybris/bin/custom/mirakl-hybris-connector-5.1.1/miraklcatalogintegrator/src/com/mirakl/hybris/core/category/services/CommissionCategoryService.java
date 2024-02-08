package com.mirakl.hybris.core.category.services;

import de.hybris.platform.category.model.CategoryModel;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

public interface CommissionCategoryService {

  /**
   * Retrieves all the commission categories starting for the given root Category
   *
   * @param rootCategory The category at the root of the commission categories tree
   * @return The commission categories
   */
  Collection<CategoryModel> getCategories(CategoryModel rootCategory);

  /**
   * Generates the content of the CA01 CSV file with all the given categories
   *
   * @param locale The locale used to write the content
   * @param categories The categories to be added to the contents
   * @return The content of the CA01 file
   * @throws IOException
   */
  String getCategoryExportCsvContent(final Locale locale, final Collection<CategoryModel> categories) throws IOException;

  /**
   * Generates the content of the CA01 CSV file with all the given categories
   *
   * @param defaultLocale The default locale used to write the content
   * @param additionalLocales The ladditional ocales used to write the content
   * @param categories The categories to be added to the contents
   * @return The content of the CA01 file
   * @throws IOException
   */
  String getCategoryExportCsvContent(final Locale defaultLocale, final Set<Locale> additionalLocales, final Collection<CategoryModel> categories) throws IOException;

}
