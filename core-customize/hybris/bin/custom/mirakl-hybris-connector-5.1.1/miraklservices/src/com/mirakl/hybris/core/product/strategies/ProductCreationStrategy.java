package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

import de.hybris.platform.core.model.product.ProductModel;

public interface ProductCreationStrategy {

  /**
   * Creates a product and its hierarchy if necessary (when it is a variant). This method does not set the values of its
   * attributes.
   *
   * @param data the data concerning the imported product
   * @param context the product import file context
   * @return the model of the created product
   * @throws ProductImportException
   */
  ProductModel createProduct(ProductImportData data, ProductImportFileContextData context) throws ProductImportException;
}
