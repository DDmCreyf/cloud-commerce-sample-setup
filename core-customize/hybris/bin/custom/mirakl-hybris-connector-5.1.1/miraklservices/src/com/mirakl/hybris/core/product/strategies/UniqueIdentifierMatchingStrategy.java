package com.mirakl.hybris.core.product.strategies;

import java.util.Set;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;

import de.hybris.platform.core.model.product.ProductModel;

public interface UniqueIdentifierMatchingStrategy {

  /**
   * Returns existing products whose unique identifier matches the unique identifier of the imported product
   * @param productImportData the product import data
   * @param context the product import context
   *
   * @return a Set of {@link ProductModel}
   */
  Set<ProductModel> getMatches(ProductImportData productImportData, ProductImportFileContextData context);
}
