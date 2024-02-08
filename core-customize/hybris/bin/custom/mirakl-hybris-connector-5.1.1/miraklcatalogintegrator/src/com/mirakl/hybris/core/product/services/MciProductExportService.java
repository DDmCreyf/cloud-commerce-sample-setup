package com.mirakl.hybris.core.product.services;

import java.io.IOException;
import java.util.Date;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;

public interface MciProductExportService {

  /**
   * Exports all the products to Mirakl, using the P21 API
   *
   * @param rootCategory The products root category Used to determine the category of each product
   * @param rootBrandCategory The brands root category. Used to determine the brand of each product
   * @param baseSite The base site which possesses the products. Used to select only the products currently displayed
   * @param filename The name of the generated P21 file
   * @return The number ofw exported products
   * @throws IOException
   */
  int exportAllProducts(CategoryModel rootCategory, CategoryModel rootBrandCategory, BaseSiteModel baseSite, String filename)
      throws IOException;

  /**
   * Exports all the products modified after the given date to Mirakl, using the P21 API
   *
   * @param rootCategory The products root category Used to determine the category of each product
   * @param rootBrandCategory The brands root category. Used to determine the brand of each product
   * @param baseSite The base site which possesses the products. Used to select only the products currently displayed
   * @param modifiedAfter All the products modified after this date are eligible for export
   * @param fileName The name of the generated P21 file
   * @return The number of exported products
   * @throws IOException
   */
  int exportModifiedProducts(CategoryModel rootCategory, CategoryModel rootBrandCategory, BaseSiteModel baseSite,
      Date modifiedAfter, String fileName) throws IOException;

}
