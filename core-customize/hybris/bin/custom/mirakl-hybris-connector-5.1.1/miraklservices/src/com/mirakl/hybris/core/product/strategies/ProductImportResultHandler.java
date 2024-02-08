package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportFileContextData;

public interface ProductImportResultHandler {

  /**
   * Handles the result file after a product import. By default it sends the file to Mirakl
   * 
   * @param context the product import file context
   */
  void handleImportResults(ProductImportFileContextData context);

}
