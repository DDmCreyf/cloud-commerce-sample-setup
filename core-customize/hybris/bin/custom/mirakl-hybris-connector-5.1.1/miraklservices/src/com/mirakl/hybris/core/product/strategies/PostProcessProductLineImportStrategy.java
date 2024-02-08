package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

public interface PostProcessProductLineImportStrategy {

  /**
   * Called after a product is imported. Can be used to add custom logic after the import
   * @param data the product import data
   * @param rawProduct the received raw product
   * @param context the file import context
   */
  void postProcess(ProductImportData data, MiraklRawProductModel rawProduct, ProductImportFileContextData context);

}
