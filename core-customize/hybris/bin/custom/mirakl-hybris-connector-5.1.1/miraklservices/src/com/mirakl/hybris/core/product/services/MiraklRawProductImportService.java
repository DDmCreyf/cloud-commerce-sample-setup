package com.mirakl.hybris.core.product.services;

import java.io.File;

import com.mirakl.hybris.beans.ProductImportFileContextData;

public interface MiraklRawProductImportService {

  /**
   * Persists all the products from the given input file as {@link com.mirakl.hybris.core.model.MiraklRawProductModel} (ie: builds
   * the staging table)
   * 
   * @param inputFile the file containing the products of the shop
   * @param context the product import file context
   * @return the id of the import
   */
  String importRawProducts(File inputFile, ProductImportFileContextData context);

}
