package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

public interface ProductUpdateStrategy {

  /**
   * Updates all the attributes (core attributes and classification attributes) values of new and existing products. This method
   * does not create any product. New products should be created previously using
   * {@link ProductCreationStrategy#createProduct(ProductImportData, ProductImportFileContextData)}
   *
   * @param integrationData the integration data
   * @param context the product import file context
   * @throws ProductImportException when the values could not be updated
   */
  void applyValues(ProductImportData integrationData, ProductImportFileContextData context) throws ProductImportException;

}
