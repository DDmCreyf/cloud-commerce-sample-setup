package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.product.exceptions.ProductImportException;

public interface VariantTypeResolutionStrategy {

  /**
   * Fills {@link ProductImportData#variantType} with the resolved variant type of the imported product
   *
   * @param integrationData the product integration data
   * @param context the product import file context
   * @throws ProductImportException if the variant type resolution failed
   */
  void resolveVariantType(ProductImportData integrationData, ProductImportFileContextData context) throws ProductImportException;
}
