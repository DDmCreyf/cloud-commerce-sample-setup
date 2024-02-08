package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

public interface ProductIdentificationStrategy {

  /**
   * Fills {@link ProductImportData#identifiedProduct} according to the information already present in the integration data
   *
   * @param integrationData the integration data
   * @throws ProductImportException
   */
  void identifyProduct(ProductImportData integrationData) throws ProductImportException;

}
